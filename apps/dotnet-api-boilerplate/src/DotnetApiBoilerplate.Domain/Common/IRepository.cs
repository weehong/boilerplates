namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Persistence port for one aggregate. Repositories load and track whole aggregates only, so
///     the constraint allows aggregate roots and nothing else. Changes are saved through the
///     application's unit of work, not here.
///     <para>
///         A feature declares its own port that extends this one and adds only the queries it needs,
///         for example <c>IWidgetRepository : IRepository&lt;Widget&gt;</c>.
///     </para>
/// </summary>
public interface IRepository<TAggregate, TId>
    where TAggregate : AggregateRoot<TId>
    where TId : notnull
{
    Task<TAggregate?> GetByIdAsync(TId id, CancellationToken cancellationToken = default);

    void Add(TAggregate aggregate);

    void Remove(TAggregate aggregate);
}

/// <summary>Convenience port for aggregates keyed by <see cref="Guid" />.</summary>
public interface IRepository<TAggregate> : IRepository<TAggregate, Guid>
    where TAggregate : AggregateRoot
{
}
