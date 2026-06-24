# Spring Boot Boilerplate

Reusable Spring Boot 4 API boilerplate using Java 25, Maven, WebMVC, validation, security scaffolding, actuator health, OpenAPI, PostgreSQL/JPA, Flyway, Redis cache support, AOP logging, metrics, tracing, Docker, and tests.

The template is intentionally domain-neutral. The health endpoint is the only sample feature.

## Tech Stack

| Concern | Choice |
| --- | --- |
| Framework | Spring Boot 4, Java 25 |
| Build | Maven wrapper |
| HTTP | Spring WebMVC |
| Validation | Jakarta Bean Validation |
| Security | Stateless Spring Security skeleton |
| Persistence | Spring Data JPA, PostgreSQL, Flyway |
| Cache | Spring Cache with optional Redis |
| API docs | springdoc OpenAPI and Swagger UI |
| Observability | Actuator, Micrometer metrics, tracing MDC filter, AOP logging |
| Tests | JUnit, Spring Boot Test, H2, Spring Security Test |

## Commands

```sh
./mvnw test
./mvnw verify
./mvnw spring-boot:run
```

The `Makefile` wraps local Docker tasks:

```sh
make up
make down
```

## Run Locally

Copy the example env file and start the stack:

```sh
cp .env.example .env.docker
make up
```

The API listens on `http://localhost:8080`.

Useful endpoints:

- Health feature: `GET /api/v1/public/health-checks`
- Actuator health: `GET /actuator/health`
- OpenAPI JSON: `GET /v3/api-docs` when `SWAGGER_ENABLED=true`
- Swagger UI: `GET /swagger-ui.html` when `SWAGGER_ENABLED=true`

## Structure

```text
src/main/java/com/example/boilerplate/
  features/        # self-contained feature modules
  shared/          # cross-cutting configuration, responses, errors, helpers
```

Feature modules should use this shape where relevant:

```text
features/<feature>/
  controllers/
  entities/
  repositories/
  requests/
  responses/
  services/
  validators/
  valueobjects/
```

## Source

Adapted from the local `upmatches-dev/upmatches` Spring Boot project, then renamed and trimmed for reusable boilerplate use.
