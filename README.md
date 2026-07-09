# Modern REST Service

Production-style Spring Boot 3.3 REST API built from a single-file Java source and upgraded with security, validation, observability, testing, and deployment workflows.

## Tech Stack
- Java 21
- Spring Boot 3.3.1
- Spring Security + JWT
- Spring Data JPA + Liquibase
- PostgreSQL
- Testcontainers + JUnit + Mockito
- Actuator + Prometheus metrics
- HATEOAS + XML content negotiation
- Async/Scheduling + AMQP starter
- Docker + Docker Compose

## Project Structure
- `src/main/java` - application source
- `src/test/java` - unit, web-slice, integration, data-jpa tests
- `src/main/resources` - config, Liquibase, templates, logging
- `.github/workflows` - CI/CD workflows
- `UAT_CHECKLIST.md` - complete UAT checklist
- `postman_collection.json` - Postman UAT collection

---

## Quick Start (Local)

### Prerequisites
- Java 21 installed
- Rancher Desktop / Docker running
- Windows PowerShell or CMD

### 1) Start DB and run app
```powershell
cd "D:\SPRING BOOT\modern-rest-service"
.\run.bat
```

### 2) Verify health
```powershell
curl http://localhost:8080/actuator/health
```
Expected: `{"status":"UP"}`

### 3) Run tests
```powershell
.\mvnw.cmd test
```

---

## Authentication

### Login (admin)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Login (user)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

Use returned token as:
`Authorization: Bearer <token>`

---

## Key API Endpoints

### Users
- `POST /api/v1/users` (ADMIN)
- `GET /api/v1/users/{id}` (USER/ADMIN)
- `GET /api/v1/users` (USER/ADMIN)
- `PUT /api/v1/users/{id}` (ADMIN)
- `DELETE /api/v1/users/{id}` (ADMIN)
- `GET /api/v1/users/{id}/resource` (HATEOAS)

### Files
- `POST /api/v1/files/upload` (USER/ADMIN)
- `GET /api/v1/files/download/{filename}` (USER/ADMIN)

### Ops
- `POST /api/v1/ops/reports/users` (async)
- `POST /api/v1/ops/messages` (AMQP publish)

### Platform
- `GET /api/v1/platform/audit-trail` (ADMIN)

### System
- `GET /actuator/health`
- `GET /actuator/prometheus`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## UAT / Postman

### UAT checklist
Use:
- `UAT_CHECKLIST.md`

### Postman collection
Import:
- `postman_collection.json`

This collection includes login, CRUD, RBAC checks, ops endpoints, and audit trail checks.

---

## One-command Smoke Scripts

Already included:
- `quick-smoke-with-start.ps1`
- `quick-smoke-with-start-cleanup.ps1`

Run:
```powershell
powershell -ExecutionPolicy Bypass -File .\quick-smoke-with-start-cleanup.ps1
```

---

## CI/CD

This project now includes:
- CI: `.github/workflows/ci.yml`
- AWS deploy (ECS/Fargate): `.github/workflows/deploy-aws-ecs.yml`
- GCP deploy (Cloud Run): `.github/workflows/deploy-gcp-cloudrun.yml`

### 1) How to execute CI

#### Trigger conditions
- Push to `main`/`master`
- Pull request to `main`/`master`

#### What it does
1. Checks out code
2. Sets Java 21
3. Runs `./mvnw -B test`
4. Builds Docker image

#### Manual execution
- GitHub → **Actions** → **CI** → **Run workflow**

---

### 2) AWS ECS CI/CD (Fargate)

#### Files used
- Workflow: `.github/workflows/deploy-aws-ecs.yml`
- Task definition template: `deploy/aws/task-definition.json`

#### One-time AWS setup
1. Create ECR repository (name should match `ECR_REPOSITORY`).
2. Create ECS cluster and service (Fargate).
3. Create IAM role for GitHub OIDC deployment.
4. Configure GitHub OIDC trust with your repo.
5. Update `deploy/aws/task-definition.json`:
   - `executionRoleArn`
   - `taskRoleArn`
   - log group/region
   - environment/secrets for DB/JWT

#### GitHub Secrets required
- `AWS_ROLE_ARN`
- `AWS_REGION`
- `ECR_REPOSITORY`
- `ECS_CLUSTER`
- `ECS_SERVICE`
- `AWS_DB_USERNAME_SECRET_ARN`
- `AWS_DB_PASSWORD_SECRET_ARN`
- `AWS_APP_JWT_SECRET_ARN`

#### How to execute AWS deployment
- GitHub → **Actions** → **Deploy AWS ECS** → **Run workflow**
- Or push to `main`/`master` (auto-trigger enabled).

#### Verify deployment
- Check ECS service events and task health
- Hit app health endpoint via ALB/public URL:
  - `/actuator/health`

---

### 3) GCP Cloud Run CI/CD

#### Files used
- Workflow: `.github/workflows/deploy-gcp-cloudrun.yml`

#### One-time GCP setup
1. Enable APIs: Cloud Run, Artifact Registry, IAM, Cloud Build.
2. Create Artifact Registry repository.
3. Create Cloud Run service (or allow workflow to create).
4. Configure Workload Identity Federation for GitHub OIDC.
5. Create deploy service account and assign roles:
   - Cloud Run Admin
   - Artifact Registry Writer
   - Service Account User

#### GitHub Secrets required
- `GCP_WORKLOAD_IDENTITY_PROVIDER`
- `GCP_SERVICE_ACCOUNT`
- `GCP_PROJECT_ID`
- `GCP_REGION`
- `GCP_ARTIFACT_REPO`
- `CLOUD_RUN_SERVICE`
- `GCP_DB_USERNAME_SECRET_NAME`
- `GCP_DB_PASSWORD_SECRET_NAME`
- `GCP_APP_JWT_SECRET_NAME`

#### How to execute GCP deployment
- GitHub → **Actions** → **Deploy GCP Cloud Run** → **Run workflow**
- Or push to `main`/`master` (auto-trigger enabled).

#### Verify deployment
- Open Cloud Run service URL
- Check:
  - `/actuator/health`

---

### 4) Recommended secret/env handling for production
- Do **not** keep DB/JWT secrets in source code.
- Use:
  - AWS Secrets Manager / SSM Parameter Store
  - GCP Secret Manager
- Inject at runtime via ECS task definition / Cloud Run env secrets.

---

### 5) Typical end-to-end execution flow
1. Developer raises PR → CI runs tests.
2. Merge to `main` → deployment workflow runs.
3. Deployment updates container image in target platform.
4. Smoke/UAT run using:
   - `quick-smoke-with-start-cleanup.ps1`
   - `postman_collection.json`

---

## Production Hardening Suggestions
- Move DB credentials and JWT secrets to secret manager (AWS/GCP)
- Set `spring.jpa.open-in-view=false`
- Configure CORS for frontend domains
- Add structured log shipping and alerts
- Add branch protection + required checks in GitHub

---

## License / Ownership
Internal project generated and iterated for your environment.
