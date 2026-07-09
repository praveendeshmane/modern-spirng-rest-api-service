# CLOUD_SETUP_STEP_BY_STEP.md

End-to-end setup to make GitHub Actions deploy this project to AWS ECS and GCP Cloud Run.

> This guide assumes your code is already in a GitHub repo and Actions are enabled.

---

## A) AWS ECS (OIDC + ECR + ECS)

### 1) Create ECR repository
AWS Console → ECR → Repositories → Create repository
- Name: `modern-rest-service` (or your custom name)
- Save name for GitHub secret: `ECR_REPOSITORY`

### 2) Create ECS cluster + service (Fargate)
AWS Console → ECS
1. Create cluster (Networking only / Fargate)
2. Create task definition (Fargate)
3. Create service in the cluster
4. Keep these values:
   - Cluster name → `ECS_CLUSTER`
   - Service name → `ECS_SERVICE`

### 3) Create CloudWatch Logs group
AWS Console → CloudWatch → Log groups
- Create `/ecs/modern-rest-service`
- Ensure region matches your deployment region

### 4) Create AWS Secrets Manager secrets
AWS Console → Secrets Manager → Store a new secret
Create 3 secrets:
- DB username secret (e.g. `modern/db/username`)
- DB password secret (e.g. `modern/db/password`)
- JWT secret (e.g. `modern/app/jwt-secret`)

Copy each secret ARN for GitHub secrets:
- `AWS_DB_USERNAME_SECRET_ARN`
- `AWS_DB_PASSWORD_SECRET_ARN`
- `AWS_APP_JWT_SECRET_ARN`

### 5) IAM role for GitHub Actions (OIDC)
AWS Console → IAM

#### 5.1 Add Identity Provider
- Type: OpenID Connect
- URL: `https://token.actions.githubusercontent.com`
- Audience: `sts.amazonaws.com`

#### 5.2 Create IAM role (example: `github-actions-ecs-deploy-role`)
Trusted entity: Web identity (GitHub OIDC provider)

Use trust policy (replace placeholders):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": [
            "repo:<GITHUB_ORG_OR_USER>/<REPO_NAME>:ref:refs/heads/main",
            "repo:<GITHUB_ORG_OR_USER>/<REPO_NAME>:ref:refs/heads/master"
          ]
        }
      }
    }
  ]
}
```

#### 5.3 Attach permissions to this role
Minimum practical permissions:
- ECR push/pull
- ECS deploy/update service
- Pass role for ECS task roles
- Describe services/tasks

(You can start with scoped policies based on your resources.)

### 6) Ensure ECS Task Execution Role can read secrets
The role in `deploy/aws/task-definition.json` (`executionRoleArn`) must have:
- `secretsmanager:GetSecretValue`
- KMS decrypt if custom KMS key is used

### 7) Update `deploy/aws/task-definition.json`
Replace placeholders:
- `<ACCOUNT_ID>` in ARNs
- proper `executionRoleArn`
- proper `taskRoleArn`
- correct `awslogs-region`

### 8) Add GitHub repository secrets (AWS)
GitHub → Repo → Settings → Secrets and variables → Actions
Add:
- `AWS_ROLE_ARN`
- `AWS_REGION`
- `ECR_REPOSITORY`
- `ECS_CLUSTER`
- `ECS_SERVICE`
- `AWS_DB_USERNAME_SECRET_ARN`
- `AWS_DB_PASSWORD_SECRET_ARN`
- `AWS_APP_JWT_SECRET_ARN`

### 9) Execute deployment
GitHub → Actions → **Deploy AWS ECS** → Run workflow

### 10) Validate
- ECS service stable
- Task running latest image
- App endpoint `/actuator/health` returns UP

---

## B) GCP Cloud Run (OIDC + Artifact Registry + Secret Manager)

### 1) Enable required APIs
GCP Console → APIs & Services → Enable APIs:
- Cloud Run Admin API
- Artifact Registry API
- IAM API
- Cloud Resource Manager API
- Secret Manager API

### 2) Create Artifact Registry repository
GCP Console → Artifact Registry → Create Repository
- Format: Docker
- Name: e.g. `modern-rest-repo`
- Region: e.g. `asia-northeast1`

### 3) Create service account for GitHub deploy
GCP Console → IAM & Admin → Service Accounts
Create: e.g. `github-deployer`

Grant roles:
- `roles/run.admin`
- `roles/artifactregistry.writer`
- `roles/iam.serviceAccountUser`

### 4) Create Workload Identity Federation
GCP Console → IAM & Admin → Workload Identity Federation
1. Create pool (e.g. `github-pool`)
2. Create provider (OIDC)
   - Issuer URL: `https://token.actions.githubusercontent.com`
3. Attribute mapping with GitHub claims (per GCP docs)
4. Grant principal access to the service account (Workload Identity User)

### 5) Create GCP Secret Manager secrets
GCP Console → Security → Secret Manager
Create secrets:
- `modern-db-username`
- `modern-db-password`
- `modern-app-jwt-secret`

Add latest versions with real values.

### 6) Add GitHub repository secrets (GCP)
Add:
- `GCP_WORKLOAD_IDENTITY_PROVIDER`
- `GCP_SERVICE_ACCOUNT`
- `GCP_PROJECT_ID`
- `GCP_REGION`
- `GCP_ARTIFACT_REPO`
- `CLOUD_RUN_SERVICE`
- `GCP_DB_USERNAME_SECRET_NAME`
- `GCP_DB_PASSWORD_SECRET_NAME`
- `GCP_APP_JWT_SECRET_NAME`

### 7) Execute deployment
GitHub → Actions → **Deploy GCP Cloud Run** → Run workflow

### 8) Validate
- Cloud Run revision deployed
- service URL reachable
- `/actuator/health` returns UP

---

## C) Final smoke verification after deploy

Use one of these:
- Postman collection: `postman_collection.json`
- PowerShell script: `quick-smoke-with-start-cleanup.ps1` (for local)

For cloud, hit deployed base URL and run:
1. `/actuator/health`
2. `/api/v1/auth/login`
3. one secured endpoint with JWT

---

## D) Common issues checklist

- GitHub secret typo (case-sensitive)
- OIDC trust condition uses wrong repo/branch
- Missing IAM role permissions (AWS) / missing GCP roles
- Artifact/ECR repo name mismatch
- Cloud region mismatch
- Secret exists but runtime identity lacks read permission

---

## E) Recommended next hardening (after first successful deploy)

- Restrict deployment to protected branch only
- Add manual approval gate for production environment
- Add blue/green or canary strategy
- Add DB migration safety checks in pipeline
- Add SAST/Dependency scan stage
