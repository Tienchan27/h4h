# Frontend Overview

This frontend provides:

- Authentication and account onboarding flows.
- A minimal white UI style system.
- An API Tester page for running backend requests from the browser.

## Routes

- `/` Authentication page (sign in, sign up, OTP, Google sign-in)
- `/profile-completion` Profile completion form for new users
- `/dashboard` User dashboard after authentication
- `/api-tester` Postman-like API testing page

## Environment

Create `frontend/.env`:

```env
REACT_APP_API_URL=/api
REACT_APP_GOOGLE_CLIENT_ID=your-google-client-id
```

`REACT_APP_API_URL=/api` is recommended when frontend is served behind Nginx reverse proxy.

## Scripts

From `frontend/`:

- `npm start` start local development server
- `npm run build` create production build
- `npm test` run tests

## API Tester workflow

1. Open `/api-tester`.
2. Select a domain and endpoint template.
3. Optionally edit path params, query, headers, and body JSON.
4. Choose auth mode:
   - `No Auth` for public endpoints.
   - `Bearer Token` for protected endpoints.
5. Click **Send Request** to view:
   - status code
   - response headers
   - response body
6. Use the request history panel to review recent calls.

For protected requests, the page can auto-fill token from local storage after login.
