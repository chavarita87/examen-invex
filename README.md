# examen-invex

Employee management microservice built with Spring Boot 2.7 and Java 17.

## Requirements

- Java 17
- MySQL 8 (profile `dev`), or nothing extra for tests (H2 in memory)

## Run

```bash
./mvnw spring-boot:run
```

Database settings can be overridden with `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.

## Test

```bash
./mvnw test
```

## Branching

Git Flow: `main` (releases), `develop` (integration), `feature/*`, `release/*`, `hotfix/*`.
