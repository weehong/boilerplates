using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Application.Common.Persistence;
using DotnetApiBoilerplate.Infrastructure.DomainEvents;
using DotnetApiBoilerplate.Infrastructure.Persistence;
using DotnetApiBoilerplate.Infrastructure.Time;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace DotnetApiBoilerplate.Infrastructure;

/// <summary>Registration entry point for the Infrastructure layer.</summary>
public static class DependencyInjection
{
    public static IServiceCollection AddInfrastructure(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        // Fail fast: a missing connection string is a configuration error, not a runtime surprise.
        string connectionString = configuration.GetConnectionString("Default")
                                  ?? throw new InvalidOperationException(
                                      "Connection string 'Default' is not configured. Set ConnectionStrings:Default " +
                                      "(env var ConnectionStrings__Default).");

        services.AddDbContext<DotnetApiBoilerplateDbContext>(options =>
            options.UseNpgsql(connectionString, npgsql =>
                npgsql.MigrationsAssembly(typeof(DotnetApiBoilerplateDbContext).Assembly.FullName)));

        // The context IS the unit of work for the current scope.
        services.AddScoped<IUnitOfWork>(sp => sp.GetRequiredService<DotnetApiBoilerplateDbContext>());

        services.AddSingleton<IDateTimeProvider, DateTimeProvider>();
        services.AddScoped<IDomainEventDispatcher, MediatRDomainEventDispatcher>();

        // --- Feature repositories are registered here (see AddRepositories). ---
        services.AddRepositories();

        services.AddHealthChecks()
            .AddDbContextCheck<DotnetApiBoilerplateDbContext>("database");

        return services;
    }

    /// <summary>
    ///     Aggregate repository registrations. Add one line per aggregate as features are introduced.
    /// </summary>
    private static IServiceCollection AddRepositories(this IServiceCollection services) =>
        // Register one repository per aggregate as features are added, e.g.:
        // services.AddScoped<IWidgetRepository, WidgetRepository>();
        services;
}
