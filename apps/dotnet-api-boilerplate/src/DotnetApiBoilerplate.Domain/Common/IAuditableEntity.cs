namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Entities implementing this interface get their timestamps stamped automatically
///     by the persistence layer on save. Keeps cross-cutting auditing out of business code.
///     <para>
///         Derive from <see cref="AuditableAggregateRoot{TId}" /> rather than implementing this
///         directly. If you must implement it yourself, implement the setters explicitly so they do
///         not become part of the entity's public surface.
///     </para>
/// </summary>
public interface IAuditableEntity
{
    DateTimeOffset CreatedOnUtc { get; }

    DateTimeOffset? ModifiedOnUtc { get; }

    void SetCreated(DateTimeOffset timestamp);

    void SetModified(DateTimeOffset timestamp);
}
