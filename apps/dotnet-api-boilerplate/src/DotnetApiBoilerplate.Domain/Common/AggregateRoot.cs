namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Marks an entity as the root of an aggregate — the only entry point through which
///     the aggregate may be modified and the natural boundary for repositories.
/// </summary>
public abstract class AggregateRoot<TId> : Entity<TId>
    where TId : notnull
{
    protected AggregateRoot(TId id)
        : base(id)
    {
    }
}

/// <summary>Convenience base for aggregate roots keyed by <see cref="Guid" />.</summary>
public abstract class AggregateRoot : AggregateRoot<Guid>
{
    protected AggregateRoot(Guid id)
        : base(id)
    {
    }
}
