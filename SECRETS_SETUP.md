# SECRETS_SETUP.md

Use this file to configure GitHub Actions secrets for CI/CD deployment workflows.

Repository path:
- `D:\SPRING BOOT\modern-rest-service`

GitHub path:
- Repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

---

## 1) AWS ECS Deployment Secrets
Used by: `.github/workflows/deploy-aws-ecs.yml`

### Required secrets

#### `AWS_ROLE_ARN`
Example:
```text
arn:aws:iam::123456789012:role/github-actions-ecs-deploy-role
```

#### `AWS_REGION`
Example:
```text
ap-northeast-1
```

#### `ECR_REPOSITORY`
Example:
```text
modern-rest-service
```

#### `ECS_CLUSTER`
Example:
```text
modern-rest-cluster
```

#### `ECS_SERVICE`
Example:
```text
modern-rest-service
```

#### `AWS_DB_USERNAME_SECRET_ARN`
Example:
```text
arn:aws:secretsmanager:ap-northeast-1:123456789012:secret:modern/db/username-AbCdEf
```

#### `AWS_DB_PASSWORD_SECRET_ARN`
Example:
```text
arn:aws:secretsmanager:ap-northeast-1:123456789012:secret:modern/db/password-AbCdEf
```

#### `AWS_APP_JWT_SECRET_ARN`
Example:
```text
arn:aws:secretsmanager:ap-northeast-1:123456789012:secret:modern/app/jwt-secret-AbCdEf
```

### AWS prerequisites checklist
- [ ] ECR repository created
- [ ] ECS cluster created (Fargate)
- [ ] ECS service created
- [ ] IAM role with OIDC trust for GitHub created
- [ ] Secrets created in AWS Secrets Manager
- [ ] ECS task execution role can read those secrets (`secretsmanager:GetSecretValue`)

---

## 2) GCP Cloud Run Deployment Secrets
Used by: `.github/workflows/deploy-gcp-cloudrun.yml`

### Required secrets

#### `GCP_WORKLOAD_IDENTITY_PROVIDER`
Example:
```text
projects/123456789012/locations/global/workloadIdentityPools/github-pool/providers/github-provider
```

#### `GCP_SERVICE_ACCOUNT`
Example:
```text
github-deployer@my-gcp-project.iam.gserviceaccount.com
```

#### `GCP_PROJECT_ID`
Example:
```text
my-gcp-project
```

#### `GCP_REGION`
Example:
```text
asia-northeast1
```

#### `GCP_ARTIFACT_REPO`
Example:
```text
modern-rest-repo
```

#### `CLOUD_RUN_SERVICE`
Example:
```text
modern-rest-service
```

#### `GCP_DB_USERNAME_SECRET_NAME`
Example:
```text
modern-db-username
```

#### `GCP_DB_PASSWORD_SECRET_NAME`
Example:
```text
modern-db-password
```

#### `GCP_APP_JWT_SECRET_NAME`
Example:
```text
modern-app-jwt-secret
```

### GCP prerequisites checklist
- [ ] Artifact Registry repo created
- [ ] Cloud Run service created (or permission to create)
- [ ] Workload Identity Federation configured
- [ ] Deploy service account created with proper roles
- [ ] Secrets created in GCP Secret Manager
- [ ] Cloud Run runtime service account has `Secret Manager Secret Accessor`

---

## 3) Optional application runtime secrets (recommended)
These are not currently wired in workflow files, but should be moved to cloud secret stores:

- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_JWT_SECRET`

Use:
- AWS Secrets Manager / SSM Parameter Store
- GCP Secret Manager

---

## 4) How to validate secrets quickly

1. Add all required secrets.
2. Open GitHub → **Actions**.
3. Run:
   - **Deploy AWS ECS** (if AWS configured)
   - **Deploy GCP Cloud Run** (if GCP configured)
4. Check workflow logs for authentication + image push + deploy completion.

---

## 5) Common failure causes
- Wrong secret name (case-sensitive)
- OIDC trust policy missing repo/branch claims
- Missing IAM roles/permissions
- Wrong region/repository/service names
- Task definition not updated with real ARNs
