# UAT Test Plan (modern-rest-service)

## Preconditions
- Rancher Desktop running
- Start app + DB: `run.bat` (or use smoke script)
- Base URL: `http://localhost:8080`
- Test users available:
  - `admin / admin123`
  - `user / user123`

---

## 1) Service Health
- `GET /actuator/health`
- Expected:
  - `200 OK`
  - body contains `"status":"UP"`

## 2) Authentication
### 2.1 Admin login
- `POST /api/v1/auth/login`
- Body:
```json
{ "username": "admin", "password": "admin123" }
```
- Expected: `200`, token returned

### 2.2 User login
- `POST /api/v1/auth/login`
- Body:
```json
{ "username": "user", "password": "user123" }
```
- Expected: `200`, token returned

---

## 3) User CRUD + Validation
> Use **admin token** unless explicitly mentioned.

### 3.1 Create user
- `POST /api/v1/users`
```json
{
  "username": "uat_user_1",
  "email": "uat1@example.com",
  "first_name": "Uat",
  "last_name": "User"
}
```
- Expected: `201`, capture `id`

### 3.2 Get user by id
- `GET /api/v1/users/{id}`
- Expected: `200`, fields match

### 3.3 List/filter users
- `GET /api/v1/users?username=uat_user_1&page=0&size=10`
- Expected: `200`, paged wrapper returned, user present

### 3.4 Update user
- `PUT /api/v1/users/{id}`
```json
{
  "email": "uat1.updated@example.com",
  "first_name": "UatUpdated",
  "status": "INACTIVE"
}
```
- Expected: `200`, updated fields reflected

### 3.5 Validation check
- `POST /api/v1/users` with invalid email
- Expected: `400` with problem-details style payload

### 3.6 Duplicate check
- Create another user with same username/email
- Expected: `409`

### 3.7 Delete user
- `DELETE /api/v1/users/{id}`
- Expected: `204`

### 3.8 Verify deletion
- `GET /api/v1/users/{id}`
- Expected: `404`

---

## 4) RBAC Authorization
### 4.1 USER role cannot create
- Login as `user`
- `POST /api/v1/users`
- Expected: `403 Forbidden`

### 4.2 USER role can read
- Login as `user`
- `GET /api/v1/users?page=0&size=5`
- Expected: `200 OK`

---

## 5) Content Negotiation + HATEOAS
### 5.1 HATEOAS resource
- `GET /api/v1/users/{id}/resource`
- Expected: `200`, `_links` present

### 5.2 XML response
- `GET /api/v1/users/{id}` with `Accept: application/xml`
- Expected: `200`, XML body

---

## 6) File Upload/Download
### 6.1 Upload
- `POST /api/v1/files/upload` (multipart `file`)
- Expected: `200`, returns `filename` + `downloadUrl`

### 6.2 Download
- `GET /api/v1/files/download/{filename}`
- Expected: `200`, attachment downloaded

---

## 7) Async / Scheduling / Messaging
### 7.1 Async report endpoint
- `POST /api/v1/ops/reports/users`
- Expected: `202 Accepted`, message returned

### 7.2 Messaging endpoint
- `POST /api/v1/ops/messages`
```json
{ "message": "uat ping" }
```
- Expected: `202 Accepted`, `{ "status": "queued" }`

### 7.3 Scheduler activity
- Check logs for scheduled cleanup tick entry
- Expected: periodic `Scheduled cleanup tick` log appears

---

## 8) Section 22 Features (Events + Audit + Correlation)
### 8.1 Correlation ID propagation
- Call any endpoint with header: `X-Correlation-Id: uat-corr-123`
- Expected:
  - Response includes same `X-Correlation-Id`
  - Log line contains correlation id

### 8.2 Domain events fired
- Perform create/update/delete user flow
- Expected: logs show `User event action=CREATED/UPDATED/DELETED`

### 8.3 Audit trail endpoint (ADMIN only)
- `GET /api/v1/platform/audit-trail` (admin token)
- Expected: `200`, recent event entries present

### 8.4 Audit trail forbidden for USER
- same endpoint with user token
- Expected: `403`

---

## 9) UI/Template Check
- Open `GET /` or `GET /home`
- Expected: page renders with "Modern REST Service"

---

## UAT Sign-off
- [ ] Pass
- [ ] Fail

### Notes
- Tester:
- Date:
- Build/Commit:
- Defects found:
