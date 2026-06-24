using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Domain.Common;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;

namespace DotnetApiBoilerplate.Infrastructure.Persistence;

/// <summary>
///     Lets <c>dotnet ef</c> create the context at design time (e.g. when adding migrations)
///     without booting the full application. Commands that touch a database (e.g.
///     <c>database update</c>) need credentials via the <c>ConnectionStrings__Default</c>
///     environment variable; the credential-free fallback only supports offline commands
///     like <c>migrations add</c>, which never open a connection.
/// </summary>
public sealed class DesignTimeDbContextFactory : IDesignTimeDbContextFactory<DotnetApiBoilerplateDbContext>
{
    public DotnetApiBoilerplateDbContext CreateDbContext(string[] args)
    {
        string connectionString =
            Environment.GetEnvironmentVariable("ConnectionStrings__Default")
            ?? "Host=localhost;Port=5432;Database=dotnet_api_boilerplate";

        DbContextOptions<DotnetApiBoilerplateDbContext> options = new DbContextOptionsBuilder<DotnetApiBoilerplateDbContext>()
            .UseNpgsql(connectionString)
            .Options;

        return new DotnetApiBoilerplateDbContext(options, new DesignTimeDateTimeProvider(), new NoOpDomainEventDispatcher());
    }

    private sealed class DesignTimeDateTimeProvider : IDateTimeProvider
    {
        public DateTimeOffset UtcNow => DateTimeOffset.UtcNow;
    }

    private sealed class NoOpDomainEventDispatcher : IDomainEventDispatcher
    {
        public Task DispatchAsync(IEnumerable<IDomainEvent> domainEvents,
            CancellationToken cancellationToken = default) =>
            Task.CompletedTask;
    }
}
