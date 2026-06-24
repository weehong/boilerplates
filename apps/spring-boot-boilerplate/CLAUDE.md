# CLAUDE.md

Guidance for working in this Spring Boot boilerplate.

## Project Shape

- Root package: `com.example.boilerplate`
- Runtime stack: Spring Boot 4, Java 25, WebMVC, validation, security skeleton, actuator, OpenAPI, JPA/PostgreSQL, Flyway, Redis cache support, metrics, tracing, and AOP logging.
- Features live under `com.example.boilerplate.features.<feature>`.
- Cross-cutting concerns live under `com.example.boilerplate.shared`.

## Coding Rules

- Study nearby package patterns before editing.
- Use wrapper types (`Integer`, `Long`, `Boolean`) instead of primitives in records, requests, and responses.
- Do not create `dtos` packages; use `requests`, `responses`, or `models`.
- Keep JSON and query parameter contracts as `snake_case`; Java records may remain `camelCase` and rely on Jackson config.
- Keep controllers thin. Put business logic in services.
- Use constructor injection. Do not use field `@Autowired`.
- Use Java records for request/response contracts.
- Use `ProblemDetail` for errors and avoid exposing stack traces to clients.
- Add package versions and plugin versions in `pom.xml` properties when needed.

## Feature Convention

```text
features/<feature>/
  configurations/
  controllers/
  entities/
  enums/
  exceptions/
  filters/
  models/
  properties/
  repositories/
  requests/
  responses/
  services/
  utils/
  validators/
  valueobjects/
```

Controllers should use plural kebab-case resources under `/api/v1`. Public unauthenticated endpoints should stay under `/api/v1/public`.

Services should expose a sealed interface and a non-sealed implementation where a feature has meaningful business behavior.

Entities should avoid public setters, expose business methods for mutation, and use protected no-arg constructors for JPA.

## Tests

- Mirror `src/main` structure under `src/test`.
- Use BDD-style test names: `given_[State]_when_[Action]_then_[Result]`.
- Use Spring Boot tests for wiring and plain unit tests for isolated shared/domain behavior.
- Keep the health endpoint and application context tests passing after structural changes.

## Commands

```sh
./mvnw test
./mvnw verify
docker compose config
```
