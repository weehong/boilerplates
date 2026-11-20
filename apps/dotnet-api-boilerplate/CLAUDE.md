# CLAUDE.md

Guidance for working in this .NET API boilerplate.

## Architecture

Clean Architecture with four runtime projects and six test projects.

```text
Api -> Infrastructure -> Application -> Domain
Api -> Application -> Domain
```

| Project | Responsibility | May reference |
| --- | --- | --- |
| `Domain` | Entities, aggregates, domain events, repository ports, `Result`/`Error` | nothing |
| `Application` | CQRS use cases, validation/logging behaviors, ports | `Domain` |
| `Infrastructure` | EF Core, repositories, time, unit of work, dispatch, DI | `Application`, `Domain` |
| `Api` | MVC host, ProblemDetails, OpenAPI, composition | `Application`, `Infrastructure` |

## Rules

- Domain stays pure .NET: no EF Core, ASP.NET Core, MediatR, FluentValidation, or project references.
- Application references Domain only. It defines the non-repository ports: `IUnitOfWork`, `IDateTimeProvider`, `IDomainEventDispatcher`.
- Repository ports live in Domain and extend `IRepository<TAggregate>`. Repositories exist only for aggregate roots.
- `tests/DotnetApiBoilerplate.ArchitectureTests` enforces these dependency rules and fails if Domain exposes a mutable collection. Do not weaken a rule to make a change pass; fix the change.
- Infrastructure implements ports and keeps EF types behind its boundary.
- Api remains a thin HTTP composition layer. Keep business logic in Application/Domain.
- Add package versions only in `Directory.Packages.props`.
- Keep nullable, warnings-as-errors, file-scoped namespaces, `_camelCase` private fields, and `sealed` by default.
- Expected failures return `Result` / `Result<T>` with typed `Error`; reserve exceptions for unexpected failures.
- Reuse `CommonErrors` for errors that apply to any feature; put feature errors in `<Aggregate>Errors` beside the aggregate.
- Guid-keyed entities and aggregates reject `Guid.Empty`; create ids in the factory method (for example `Guid.CreateVersion7()`).
- Every async public method should accept and pass through a `CancellationToken`.

## Domain Events

Aggregates raise `IDomainEvent`s. `DotnetApiBoilerplateDbContext.SaveChangesAsync` collects events, saves changes, then dispatches the events in-process through MediatR as `DomainEventNotification<T>`.

This is intentionally lightweight. If a product needs durable asynchronous delivery later, add message bus infrastructure as a product-specific extension rather than in the base template.

## Adding A Feature

For a `Widget` aggregate:

1. Domain: add `Widget` (derive from `AggregateRoot`, or `AuditableAggregateRoot` for created/modified timestamps), `WidgetErrors`, domain events, and `IWidgetRepository : IRepository<Widget>` with only the extra queries the feature needs.
2. Application: add commands/queries, handlers, DTOs, mappers, and FluentValidation validators. Handlers save through `IUnitOfWork`.
3. Infrastructure: add the EF configuration and `WidgetRepository : Repository<Widget, Guid>, IWidgetRepository`, then register it in `DependencyInjection.AddRepositories`.
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
