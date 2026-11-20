namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Aggregate root whose creation and modification times are stamped by the persistence layer.
///     The timestamps are read-only to business code: the setters are explicit
///     <see cref="IAuditableEntity" /> members, so only code that deliberately works through that
///     interface (the DbContext on save) can call them.
/// </summary>
public abstract class AuditableAggregateRoot<TId> : AggregateRoot<TId>, IAuditableEntity
    where TId : notnull
{
    protected AuditableAggregateRoot(TId id)
        : base(id)
    {
    }

    public DateTimeOffset CreatedOnUtc { get; private set; }

    public DateTimeOffset? ModifiedOnUtc { get; private set; }

    void IAuditableEntity.SetCreated(DateTimeOffset timestamp)
    {
        if (CreatedOnUtc != default)
        {
            throw new InvalidOperationException("The creation timestamp is already set and cannot change.");
        }

        CreatedOnUtc = timestamp;
    }

    void IAuditableEntity.SetModified(DateTimeOffset timestamp) => ModifiedOnUtc = timestamp;
}

/// <summary>
///     Convenience base for auditable aggregate roots keyed by <see cref="Guid" />.
///     Rejects <see cref="Guid.Empty" />, like <see cref="AggregateRoot" />.
/// </summary>
public abstract class AuditableAggregateRoot : AuditableAggregateRoot<Guid>
{
    protected AuditableAggregateRoot(Guid id)
        : base(EntityId.EnsureNotEmpty(id))
    {
    }
}
