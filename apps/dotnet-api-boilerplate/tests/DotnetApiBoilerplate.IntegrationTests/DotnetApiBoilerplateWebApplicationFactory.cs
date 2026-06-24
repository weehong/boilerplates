using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Domain.Common;
using DotnetApiBoilerplate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Testcontainers.PostgreSql;

namespace DotnetApiBoilerplate.IntegrationTests;

/// <summary>
///     Boots the real API host against a throwaway PostgreSQL container (via Testcontainers), so the
///     integration tests exercise the full HTTP + MediatR + EF + Npgsql pipeline against the same
///     relational engine used in production, including constraints, transactions, SQL translation,
///     and migrations.
///     <para>
///         The container is started once in <see cref="InitializeAsync" /> and shared across the
///         whole test suite via <see cref="IntegrationTestCollection" />. Migrations are applied on
///         startup, so the schema matches what <c>make migrate</c> produces in production.
///     </para>
/// </summary>
public sealed class DotnetApiBoilerplateWebApplicationFactory : WebApplicationFactory<Program>, IAsyncLifetime
{
    private readonly PostgreSqlContainer _container = new PostgreSqlBuilder("postgres:17-alpine")
        .Build();

    public async ValueTask InitializeAsync()
    {
        await _container.StartAsync();

        // Apply migrations with a standalone context BEFORE the host starts, mirroring production
        // (`make migrate` runs as its own step). The host is built lazily on the first CreateClient(),
        // picking up the connection string set in ConfigureWebHost.
        DbContextOptions<DotnetApiBoilerplateDbContext> options = new DbContextOptionsBuilder<DotnetApiBoilerplateDbContext>()
            .UseNpgsql(_container.GetConnectionString())
            .Options;

        await using DotnetApiBoilerplateDbContext dbContext = new(options, new StubDateTimeProvider(), new NoOpDispatcher());
        await dbContext.Database.MigrateAsync();
    }

    public override async ValueTask DisposeAsync()
    {
        await _container.DisposeAsync();
        await base.DisposeAsync();
    }

    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.UseEnvironment("Testing");

        // Point the real Npgsql-backed DbContext at the throwaway container. No provider swap: the
        // tests exercise the same persistence stack used by production.
        builder.UseSetting("ConnectionStrings:Default", _container.GetConnectionString());
    }

    // Minimal stand-ins so the migration context can be constructed without the DI container. Neither
    // is exercised by MigrateAsync (no audit stamping or event dispatch happens during migration).
    private sealed class StubDateTimeProvider : IDateTimeProvider
    {
        public DateTimeOffset UtcNow => DateTimeOffset.UtcNow;
    }

    private sealed class NoOpDispatcher : IDomainEventDispatcher
    {
        public Task DispatchAsync(IEnumerable<IDomainEvent> domainEvents,
            CancellationToken cancellationToken = default) =>
            Task.CompletedTask;
    }
}
