# Temporary AI Context Checkpoint

This is a temporary handoff/checkpoint note for AI context only.
Safe to delete after work resumes.

## Current branch and status

- Branch: `feat/single-entry-nginx-deploy`
- State: temporary stop/checkpoint commit requested by user.
- Scope included in this checkpoint:
  - Nginx `/api/auth/*` proxy path fix.
  - Frontend auth handling improvements (Google conflict message, auth header handling).
  - Backend auth robustness updates (`JwtAuthenticationFilter`, `UserRoleRepository`, global error mapping).
  - Profile completion validation/error UX improvements.
  - OTP delivery stabilization in Docker dev using Mailpit + fail-fast behavior when mail send fails.

## What has been changed so far

1. Auth/API routing and runtime hardening
   - `deploy/nginx/nginx.conf`: `/api/auth/` now proxies to backend `/auth/`.
   - `src/main/java/com/example/tms/security/JwtAuthenticationFilter.java`: token-processing failures now clear security context without crashing public auth requests.
   - `src/main/java/com/example/tms/repository/UserRoleRepository.java`: role fetch query uses `join fetch` to avoid lazy-loading failures in filter.
   - `src/main/java/com/example/tms/exception/GlobalExceptionHandler.java`: added `IllegalArgumentException` -> `400 BAD_REQUEST`.

2. Frontend auth/profile behavior
   - `frontend/src/services/api.js`: does not attach bearer token to `/auth/*` calls.
   - `frontend/src/pages/LandingPage.jsx`: Google `EMAIL_CONFLICT` now shows guidance instead of unauthenticated link attempt.
   - `frontend/src/components/profile/ProfileCompletion.jsx`: surfaces field-level validation errors from API details.
   - `frontend/src/utils/validation.js`: normalizes phone values and auto-prefixes facebook URL with `https://` if missing.

3. OTP/mail delivery (Docker dev)
   - `compose.yaml`: added `mailpit` service and wired app mail env to `MAIL_HOST=mailpit`, `MAIL_PORT=1025`.
   - `src/main/java/com/example/tms/service/MailService.java`: mail send failure now throws `ApiException` (fail-fast) instead of logging-only success.
   - `README.md`: updated setup notes for OAuth env parity and Mailpit OTP flow.

## Pending/known context

- This commit is a temporary stop-point for continued stabilization/testing.
- `TEMP_AI_CONTEXT_CHECKPOINT.md` itself is temporary and intended for deletion later.

## .env setup required for other machines

Create root `.env` in repo root with at least:

```env
# OAuth
GOOGLE_CLIENT_ID=your-google-web-client-id.apps.googleusercontent.com
REACT_APP_GOOGLE_CLIENT_ID=your-google-web-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# JWT
JWT_SECRET=use-a-strong-secret-at-least-32-bytes

# Database (host run defaults; compose overrides app datasource host internally)
DATABASE_URL=jdbc:postgresql://localhost:5433/tms_db
DATABASE_USERNAME=tms_user
DATABASE_PASSWORD=tms_password
POSTGRES_DB=tms_db

# Server/CORS
SERVER_PORT=8081
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8081

# Mail (Docker dev uses compose override to mailpit container)
MAIL_HOST=localhost
MAIL_PORT=1025
```

Notes:
- `GOOGLE_CLIENT_ID` and `REACT_APP_GOOGLE_CLIENT_ID` must match.
- If running via Docker Compose, Mailpit UI is available at `http://localhost:8025`.
- For local frontend-only run (without docker nginx), `frontend/.env` should contain:

```env
REACT_APP_API_URL=http://localhost:8081
REACT_APP_GOOGLE_CLIENT_ID=your-google-web-client-id.apps.googleusercontent.com
```
