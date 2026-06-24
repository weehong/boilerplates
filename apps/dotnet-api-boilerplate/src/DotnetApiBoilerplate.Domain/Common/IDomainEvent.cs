namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Marker for something that happened in the domain and may have side effects.
///     Kept free of any infrastructure dependency (e.g. MediatR) so the Domain layer stays pure.
/// </summary>
public interface IDomainEvent
{
    DateTimeOffset OccurredOnUtc { get; }
}
