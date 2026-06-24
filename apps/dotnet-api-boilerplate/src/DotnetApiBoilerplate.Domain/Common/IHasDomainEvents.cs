namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Implemented by entities that buffer domain events. Lets the persistence layer collect and
///     clear events without knowing the entity's concrete identifier type.
/// </summary>
public interface IHasDomainEvents
{
    IReadOnlyCollection<IDomainEvent> DomainEvents { get; }

    void ClearDomainEvents();
}
