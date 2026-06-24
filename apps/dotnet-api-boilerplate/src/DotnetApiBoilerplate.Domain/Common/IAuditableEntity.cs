namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Entities implementing this interface get their timestamps stamped automatically
///     by the persistence layer on save. Keeps cross-cutting auditing out of business code.
/// </summary>
public interface IAuditableEntity
{
    DateTimeOffset CreatedOnUtc { get; }

    DateTimeOffset? ModifiedOnUtc { get; }

    void SetCreated(DateTimeOffset timestamp);

    void SetModified(DateTimeOffset timestamp);
}
