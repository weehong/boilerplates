using DotnetApiBoilerplate.Domain.Common;

namespace DotnetApiBoilerplate.Application.Common.Abstractions;

/// <summary>
///     Publishes domain events raised by aggregates. Implemented in Infrastructure (over MediatR)
///     and invoked by the persistence layer after changes are saved.
/// </summary>
public interface IDomainEventDispatcher
{
    Task DispatchAsync(IEnumerable<IDomainEvent> domainEvents, CancellationToken cancellationToken = default);
}
