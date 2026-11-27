# Evidence

Captured on 2026-11-26 against the service running locally with the `dev` profile and MySQL 8.0.

| File | What it shows |
|---|---|
| [`01-swagger-ui.png`](01-swagger-ui.png) | Swagger UI with the six endpoints, the HTTP Basic "Authorize" button and the schemas |
| [`02-jacoco-coverage.png`](02-jacoco-coverage.png) | JaCoCo report: 99% instructions and 93% branches covered |
| [`03-test-results.txt`](03-test-results.txt) | 70 unit and integration tests, all passing |
| [`04-postman-newman-run.txt`](04-postman-newman-run.txt) | Postman collection run with Newman: 16 requests, 41 assertions, 0 failures |

To reproduce them:

```bash
./mvnw verify                                   # tests and target/site/jacoco/index.html
./mvnw spring-boot:run                          # then open http://localhost:8080/swagger-ui.html
npx newman run postman/examen-invex.postman_collection.json -e postman/local.postman_environment.json
```
