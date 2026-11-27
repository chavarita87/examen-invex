# examen-invex

Employee management microservice built with Spring Boot 2.7 and Java 17: a secured REST API to
create, read, update, delete and search employees, with validation, uniform error responses,
OpenAPI documentation, request logging, tests with coverage checks, Docker and a CI pipeline.

## Contents

| Path | What it holds |
|---|---|
| `src/main/java/com/invex/examen` | `controller` (API and its contract), `service`, `repository`, `model`, `dto`, `mapper`, `validation`, `exception`, `filter`, `config`, `constant` |
| `src/test/java/com/invex/examen` | Unit and integration tests |
| `database/` | SQL scripts: schema and sample data |
| `docs/openapi.yaml` | OpenAPI contract |
| `docs/evidence/` | Screenshots and test runs |
| `postman/` | Postman collection and local environment |
| `Dockerfile`, `docker-compose.yml` | Container image and local stack |
| `.github/workflows/ci.yml`, `Jenkinsfile` | CI/CD pipelines |

## Requirements

- Java 17
- MySQL 8 (profile `dev`), or nothing extra for tests (H2 in memory)
- Docker, only to run it in containers

## Run locally

```bash
./mvnw spring-boot:run
```

The default profile is `dev`, which connects to a local MySQL and defines local users.
Database settings can be overridden with `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.

## Database scripts

The service creates its table on startup, but the schema and some sample data are also provided
as plain SQL in `database/`:

| Script | What it does |
|---|---|
| `01-schema.sql` | Creates the `examen_invex` database and the `employees` table, with its indexes and checks on age (18 to 100) and gender. Safe to run more than once. |
| `02-sample-data.sql` | Inserts ten sample employees, including names with accents and one inactive employee. |

```bash
mysql -u root -p --default-character-set=utf8mb4 < database/01-schema.sql
mysql -u root -p --default-character-set=utf8mb4 < database/02-sample-data.sql
```

Use `--default-character-set=utf8mb4` so names with accents are stored correctly. `created_at` is
stored in UTC, like the service does.

## Run with Docker

```bash
cp .env.example .env    # then set real values in .env
docker compose up --build
```

This starts MySQL 8 and the service with the `prod` profile. The image is built in two stages,
runs as a non-root user on a read-only file system and reports its state through
`/actuator/health/readiness`.

## Security

Every `/employees` endpoint requires HTTP Basic authentication.

| Role | Can do |
|---|---|
| `READER` | `GET /employees`, `GET /employees/{id}`, `GET /employees/search` |
| `ADMIN` | everything above, plus `POST`, `PUT` and `DELETE` |

- Users come from `API_READER_USERNAME`, `API_READER_PASSWORD`, `API_ADMIN_USERNAME` and
  `API_ADMIN_PASSWORD`. Outside `dev` there are no defaults: the service refuses to start if any is
  missing or a password is shorter than 8 characters. Passwords are stored hashed (BCrypt).
- Local `dev` users: `reader` / `reader123` and `admin` / `admin123`.
- Public without credentials: `/actuator/health`, `/actuator/info` and the API docs.
  Any other path is denied.
- 401 and 403 responses use the same error body as every other error.

```bash
curl -u reader:reader123 http://localhost:8080/employees
```

## API documentation

With the application running:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI contract (JSON): http://localhost:8080/v3/api-docs
- OpenAPI contract (YAML): http://localhost:8080/v3/api-docs.yaml

A copy of the contract is kept in [`docs/openapi.yaml`](docs/openapi.yaml).

All endpoints can also return 401 (no or wrong credentials); writes return 403 for `READER`.

| Method | Path | Success | Errors |
|---|---|---|---|
| GET | `/employees` | 200 | 500 |
| GET | `/employees/{id}` | 200 | 400, 404 |
| POST | `/employees` (array of 1 to 100 employees) | 201 | 400, 409 |
| PUT | `/employees/{id}` (partial update) | 200 | 400, 404 |
| DELETE | `/employees/{id}` | 204 | 400, 404 |
| GET | `/employees/search?name={name}` | 200 | 400 |

Dates use the format `dd-MM-yyyy`. Every error returns the same body:

```json
{
  "timestamp": "26-11-2026 18:42:42",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/employees",
  "fieldErrors": [
    { "field": "requests[0].age", "message": "must be at least 18" }
  ]
}
```

## Logging

Logging is configured in `application.yml`.

- Every request is logged with its method, path, headers, status and duration.
  `Authorization`, `Proxy-Authorization`, `Cookie`, `Set-Cookie` and `X-API-Key` are masked.
- Each request carries a correlation id: the caller's `X-Correlation-Id` header when it is valid
  (letters, digits and hyphens, up to 64 characters), or a generated UUID. It is added to every
  log line and returned in the `X-Correlation-Id` response header.
- `LOG_LEVEL` sets the application log level and `LOG_FILE` also writes logs to a rolling file
  (10 MB per file, 7 files kept).

## Test

```bash
./mvnw test      # unit and slice tests (JUnit 5, Mockito, MockMvc, H2)
./mvnw verify    # tests plus JaCoCo coverage check (80% lines and branches)
```

The coverage report is written to `target/site/jacoco/index.html`.

### Postman

Import `postman/examen-invex.postman_collection.json` and `postman/local.postman_environment.json`,
select the environment and run the collection. It has 16 requests in three folders (happy path,
validation and errors, security), each with its own assertions. From the command line:

```bash
npx newman run postman/examen-invex.postman_collection.json -e postman/local.postman_environment.json
```

### Evidence

Screenshots of Swagger UI and the coverage report, the test results and a full Postman run are in
[`docs/evidence`](docs/evidence/README.md).

## CI/CD

`.github/workflows/ci.yml` (GitHub Actions) runs on pushes and pull requests to `develop` and `main`.
`Jenkinsfile` runs the same stages on Jenkins.

1. **Build, test and coverage**: `./mvnw verify`, failing below 80% line or branch coverage.
2. **Dependency scan**: Trivy looks for vulnerable dependencies, leaked secrets and
   misconfigurations, publishes the results to GitHub code scanning and fails on critical
   vulnerabilities that have a fix.
3. **Docker image**: builds the image and scans it with Trivy the same way.

Accepted vulnerabilities are listed with their reason in `.trivyignore`.

## Branching

Git Flow: `main` (releases), `develop` (integration), `feature/*`, `release/*`, `hotfix/*`.
