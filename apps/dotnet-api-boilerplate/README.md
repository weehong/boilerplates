# Dotnet API Boilerplate

Lightweight .NET 10 backend API boilerplate built with Clean Architecture, CQRS, EF Core PostgreSQL, Result-based error handling, OpenAPI/Scalar, health checks, Docker, and focused tests.

This template is domain-neutral. The sample controller only demonstrates routing and HTTP error mapping; replace it with your first feature.

## Tech Stack

| Concern | Choice |
| --- | --- |
| Framework | .NET 10, MVC controllers |
| Architecture | Clean Architecture, DDD-oriented domain model, CQRS |
| Mediator | MediatR v12 |
| Validation | FluentValidation |
| Persistence | EF Core 10 + Npgsql PostgreSQL |
| API docs | Microsoft.AspNetCore.OpenApi + Scalar UI |
| Tests | xUnit, Shouldly, NSubstitute, WebApplicationFactory, Testcontainers |
| Tooling | Central Package Management, Docker, GitHub Actions |

## Prerequisites

- .NET SDK 10.0.300+ pinned by `global.json`
- Docker and Docker Compose for PostgreSQL, Docker runs, and integration tests
- `dotnet tool restore` before EF migration commands

## Common Commands

```sh
dotnet restore DotnetApiBoilerplate.slnx
dotnet build DotnetApiBoilerplate.slnx -c Release
dotnet test DotnetApiBoilerplate.slnx
dotnet run --project src/DotnetApiBoilerplate.Api
dotnet format DotnetApiBoilerplate.slnx
```

The `Makefile` wraps the same tasks:

```sh
make restore
make build
make test
make run
make format-check
```

## Run Locally

Start PostgreSQL:

```sh
docker compose up -d db
```

Set the connection string for non-Docker API runs:

```sh
export ConnectionStrings__Default="Host=localhost;Port=5432;Database=dotnet_api_boilerplate;Username=dotnet_api_boilerplate;Password=dotnet_api_boilerplate"
dotnet run --project src/DotnetApiBoilerplate.Api
```

Browse:

- API base: `http://localhost:5080/api/v1`
- OpenAPI document: `http://localhost:5080/openapi/v1.json`
- Scalar UI: `http://localhost:5080/scalar/v1`
- Health: `http://localhost:5080/health`

## Run With Docker

```sh
cp .env.example .env
make up
make down
```

The containerized API listens on `http://localhost:8080` and waits for PostgreSQL to become healthy.

## Database Migrations

No migrations ship with the boilerplate because there are no business entities yet. After adding an entity and EF configuration:

```sh
dotnet tool restore
make migration NAME=Initial
make migrate
```

Migrations live in `src/DotnetApiBoilerplate.Infrastructure/Persistence/Migrations`.

## Project Structure

```text
src/
  DotnetApiBoilerplate.Domain          # Entities, value objects, domain events, Result/Error
  DotnetApiBoilerplate.Application     # CQRS use cases, validation/logging behaviors, ports
  DotnetApiBoilerplate.Infrastructure  # EF Core, repositories, domain-event dispatch, DI
  DotnetApiBoilerplate.Api             # MVC host, ProblemDetails, OpenAPI
tests/
  DotnetApiBoilerplate.Domain.Tests
  DotnetApiBoilerplate.Application.Tests
  DotnetApiBoilerplate.Infrastructure.Tests
  DotnetApiBoilerplate.Api.Tests
  DotnetApiBoilerplate.IntegrationTests
```

## Architecture

Dependencies point inward:

```text
Api -> Infrastructure -> Application -> Domain
Api -> Application -> Domain
```

- Domain contains framework-free entities, domain events, `Result`, and `Error`.
- Application contains MediatR commands/queries, validation, behaviors, and ports.
- Infrastructure implements persistence, time, repositories, unit of work, and in-process domain-event dispatch.
- Api translates HTTP requests/responses and maps errors to RFC 7807 problem documents.

## Source

Adapted from `github.com/upmatches/api_v2` commit `7b18471d65c8526311a144a44cb4ab61d1eaab0b`, then renamed and trimmed for reusable boilerplate use.
