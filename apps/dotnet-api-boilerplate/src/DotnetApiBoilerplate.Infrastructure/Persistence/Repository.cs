using DotnetApiBoilerplate.Domain.Common;
using Microsoft.EntityFrameworkCore;

namespace DotnetApiBoilerplate.Infrastructure.Persistence;

/// <summary>
///     EF Core base for aggregate repositories. A feature repository derives from it, implements its
///     own port and adds only that feature's queries. Saving stays with the unit of work
///     (<c>IUnitOfWork.SaveChangesAsync</c>), so one use case commits all its changes together.
/// </summary>
public abstract class Repository<TAggregate, TId>(DotnetApiBoilerplateDbContext dbContext)
    : IRepository<TAggregate, TId>
    where TAggregate : AggregateRoot<TId>
    where TId : notnull
{
    protected DotnetApiBoilerplateDbContext DbContext { get; } = dbContext;

    protected DbSet<TAggregate> Set => DbContext.Set<TAggregate>();

    public virtual async Task<TAggregate?> GetByIdAsync(TId id, CancellationToken cancellationToken = default) =>
        await Set.FindAsync([id], cancellationToken);

    public void Add(TAggregate aggregate) => Set.Add(aggregate);

    public void Remove(TAggregate aggregate) => Set.Remove(aggregate);
}
