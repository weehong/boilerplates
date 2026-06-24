# CLAUDE.md

Guidance for working in this .NET API boilerplate.

## Architecture

Clean Architecture with four runtime projects and five test projects.

```text
Api -> Infrastructure -> Application -> Domain
Api -> Application -> Domain
```

| Project | Responsibility | May reference |
| --- | --- | --- |
| `Domain` | Entities, aggregates, domain events, `Result`/`Error` | nothing |
| `Application` | CQRS use cases, validation/logging behaviors, ports | `Domain` |
| `Infrastructure` | EF Core, repositories, time, unit of work, dispatch, DI | `Application`, `Domain` |
| `Api` | MVC host, ProblemDetails, OpenAPI, composition | `Application`, `Infrastructure` |

## Rules

- Domain stays pure .NET: no EF Core, ASP.NET Core, MediatR, FluentValidation, or project references.
- Application references Domain only. Define ports such as `IUnitOfWork`, `IDateTimeProvider`, `IDomainEventDispatcher`, and repository interfaces here or in Domain.
- Infrastructure implements ports and keeps EF types behind its boundary.
- Api remains a thin HTTP composition layer. Keep business logic in Application/Domain.
- Add package versions only in `Directory.Packages.props`.
- Keep nullable, warnings-as-errors, file-scoped namespaces, `_camelCase` private fields, and `sealed` by default.
- Expected failures return `Result` / `Result<T>` with typed `Error`; reserve exceptions for unexpected failures.
- Every async public method should accept and pass through a `CancellationToken`.

## Domain Events

Aggregates raise `IDomainEvent`s. `DotnetApiBoilerplateDbContext.SaveChangesAsync` collects events, saves changes, then dispatches the events in-process through MediatR as `DomainEventNotification<T>`.

This is intentionally lightweight. If a product needs durable asynchronous delivery later, add message bus infrastructure as a product-specific extension rather than in the base template.

## Adding A Feature

For a `Widget` aggregate:

1. Domain: add `Widget`, errors, domain events, and an `IWidgetRepository` port.
2. Application: add commands/queries, handlers, DTOs, mappers, and FluentValidation validators.
3. Infrastructure: add EF configuration, repository implementation, and register the repository in `DependencyInjection.AddRepositories`.
4. Api: add a controller that sends commands/queries through MediatR and maps `Result` with `result.Match(...)`.
5. Tests: cover domain invariants, handlers/validators, repository behavior where useful, API mapping, and one integration path.

Handlers, validators, EF configurations, and controllers are discovered by scanning. Repository registrations are explicit.

## Commands

```sh
dotnet restore DotnetApiBoilerplate.slnx
dotnet build DotnetApiBoilerplate.slnx -c Release
dotnet test DotnetApiBoilerplate.slnx
dotnet format DotnetApiBoilerplate.slnx --verify-no-changes
```

`make help` lists shortcuts. Integration tests use Testcontainers PostgreSQL, so Docker must be running for the full suite.
