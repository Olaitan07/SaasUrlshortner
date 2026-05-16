# SaaS URL Shortener

A production-ready **SaaS URL Shortener** backend built with **Spring Boot 4**, **Spring Security**, and **PostgreSQL**. The service provides secure multi-device user authentication, stateful JWT session management, and a clean RESTful API — designed following **Domain-Driven Design (DDD)** principles.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT (JJWT 0.13) |
| Persistence | Spring Data JPA + PostgreSQL |
| Mapping | MapStruct 1.5.5 |
| Build | Maven |
| Containerisation | Docker Compose |

---

## Architecture

The project follows a **Domain-Driven Design (DDD)** layered architecture:

```
├── auth/           # Spring Security config, JWT filter, UserDetails
├── controller/     # Presentation layer — REST endpoints
├── service/        # Application layer — use cases (interfaces + impls)
├── model/          # Domain aggregates (UserModel, UserSession)
├── repository/     # Infrastructure layer — JPA repositories
├── dto/
│   ├── request/    # Inbound contracts (RegisterRequest, LoginRequest)
│   └── response/   # Read models / projections (AuthResponse, UserProxy, SessionResponse)
├── mapper/         # Anti-corruption layer — MapStruct mappers
├── enums/          # Domain enumerations (Roles)
└── exceptions/     # Global exception handling with structured logging
```

---

## Features

### Authentication
- **Register** — create an account with email, password, roles, and device info
- **Login** — authenticate and receive a signed JWT; supports `rememberMe` for extended token expiry
- **Logout** — invalidates the current session token server-side; the JWT is rendered unusable immediately without waiting for natural expiry

### Session Management
- **Multi-device sessions** — up to 2 concurrent active sessions per user; oldest session is evicted automatically when the limit is exceeded
- **View active sessions** — list all active sessions with device info, last active timestamp, and expiry
- **Revoke a session** — invalidate any specific session by ID
- **Revoke all sessions** — sign out from every device at once

### User Profile
- **GET /profile** — returns a `UserProxy` read model (id, name, email, roles, createdAt, updatedAt); password is never exposed

---

## API Reference

### Auth — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/signup` | Public | Register a new user |
| `POST` | `/login` | Public | Login and receive JWT |
| `POST` | `/logout` | Bearer | Invalidate current session |

### User — `/api/v1/user`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/profile` | Bearer | Get authenticated user profile |

### Sessions — `/api/v1/sessions`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/` | Bearer | List all active sessions |
| `DELETE` | `/{sessionId}` | Bearer | Revoke a specific session |
| `DELETE` | `/all` | Bearer | Revoke all active sessions |

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose

### 1. Start the database

```bash
docker-compose up -d
```

This spins up a PostgreSQL instance on port `5433`.

### 2. Configure environment variables

The application reads the following environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5433/productdb
DB_USERNAME=admin
DB_PASSWORD=admin123
JWT_SECRET=<your-base64-encoded-256-bit-secret>
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

---

## JWT Configuration

| Property | Default | Description |
|---|---|---|
| `jwt.expiration` | `86400000` (24h) | Standard token TTL in ms |
| `jwt.remember-me-expiration` | `2592000000` (30d) | Remember-me token TTL in ms |

Tokens are validated on every request by `JwtAuthFilter`. A token is considered valid only if it is **cryptographically sound AND its corresponding session is still active** in the database — revoking a session immediately invalidates the token.

---

## Security Design

- Passwords are hashed with **BCrypt**
- All endpoints except `/signup` and `/login` require a valid `Bearer` token
- Session state is persisted in the `user_sessions` table — stateless JWT combined with stateful session tracking gives the best of both worlds (scalable verification + instant revocation)
- `GlobalExceptionHandler` catches and logs all errors centrally; sensitive details are never leaked to the client
