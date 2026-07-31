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
| Observability | Actuator, Micrometer metrics, OpenTelemetry tracing with OTLP export, AOP logging |
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
- Liveness probe: `GET /actuator/health/liveness`
- Readiness probe: `GET /actuator/health/readiness`
- Prometheus metrics: `GET /actuator/prometheus` (authentication required)
- OpenAPI JSON: `GET /v3/api-docs` when `SWAGGER_ENABLED=true`
- Swagger UI: `GET /swagger-ui.html` when `SWAGGER_ENABLED=true`

Every HTTP response includes an `X-Trace-Id` header containing the request's W3C trace identifier.
Send a W3C `traceparent` header to continue an upstream trace. Trace identifiers and log correlation
remain active in every profile, including when span export is disabled.

Span sampling and export are independent controls. Set `TRACING_SAMPLING_PROBABILITY` to choose the
fraction of spans recorded for export, set `TRACING_EXPORT_ENABLED=true` to enable OTLP export, and
point `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` at any OTLP/HTTP-compatible collector. Do not disable the
tracer to reduce export cost: the tracer is also what supplies identifiers for response headers and logs.

The health endpoint and its liveness/readiness probes remain anonymous for orchestrators. All other
management routes are protected by default, including the Prometheus endpoint and actuator discovery
page. Configure credentials accepted by the deployment's authentication provider in the Prometheus
scrape job; an anonymous scrape is rejected.

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
