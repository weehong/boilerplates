using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Application.Common.Persistence;
using DotnetApiBoilerplate.Domain.Common;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;

namespace DotnetApiBoilerplate.Infrastructure.Persistence;

/// <summary>
///     EF Core context and the application's unit of work. Entity mappings are discovered from
///     <see cref="IEntityTypeConfiguration{TEntity}" /> implementations in this assembly, so adding a
///     feature requires no change here. On save it stamps audit fields, persists the changes, then
///     dispatches collected domain events in-process.
/// </summary>
public sealed class DotnetApiBoilerplateDbContext(
    DbContextOptions<DotnetApiBoilerplateDbContext> options,
    IDateTimeProvider dateTimeProvider,
    IDomainEventDispatcher domainEventDispatcher)
    : DbContext(options), IUnitOfWork
{
    private readonly IDateTimeProvider _dateTimeProvider = dateTimeProvider;
    private readonly IDomainEventDispatcher _domainEventDispatcher = domainEventDispatcher;

    public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        ApplyAuditInformation();

        IReadOnlyList<IDomainEvent> domainEvents = CollectDomainEvents();
        int result = await base.SaveChangesAsync(cancellationToken);

        await _domainEventDispatcher.DispatchAsync(domainEvents, cancellationToken);

        return result;
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(DotnetApiBoilerplateDbContext).Assembly);

        base.OnModelCreating(modelBuilder);
    }

    private void ApplyAuditInformation()
    {
        DateTimeOffset now = _dateTimeProvider.UtcNow;

        foreach (EntityEntry<IAuditableEntity> entry in ChangeTracker.Entries<IAuditableEntity>())
        {
            switch (entry.State)
            {
                case EntityState.Added:
                    entry.Entity.SetCreated(now);
                    break;
                case EntityState.Modified:
                    entry.Entity.SetModified(now);
                    break;
            }
        }
    }

    private IReadOnlyList<IDomainEvent> CollectDomainEvents()
    {
        IHasDomainEvents[] entitiesWithEvents = ChangeTracker
            .Entries<IHasDomainEvents>()
            .Select(e => e.Entity)
            .Where(e => e.DomainEvents.Count != 0)
            .ToArray();

        IDomainEvent[] domainEvents = entitiesWithEvents.SelectMany(e => e.DomainEvents).ToArray();

        foreach (IHasDomainEvents? entity in entitiesWithEvents)
        {
            entity.ClearDomainEvents();
        }

        return domainEvents;
    }
}
