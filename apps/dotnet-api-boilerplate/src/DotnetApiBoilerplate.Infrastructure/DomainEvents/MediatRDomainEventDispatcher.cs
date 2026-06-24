using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Domain.Common;
using MediatR;

namespace DotnetApiBoilerplate.Infrastructure.DomainEvents;

internal sealed class MediatRDomainEventDispatcher(IPublisher publisher) : IDomainEventDispatcher
{
    private readonly IPublisher _publisher = publisher;

    public async Task DispatchAsync(
        IEnumerable<IDomainEvent> domainEvents,
        CancellationToken cancellationToken = default)
    {
        foreach (IDomainEvent domainEvent in domainEvents)
        {
            Type notificationType = typeof(DomainEventNotification<>).MakeGenericType(domainEvent.GetType());
            object notification = Activator.CreateInstance(notificationType, domainEvent)
                                  ?? throw new InvalidOperationException(
                                      $"Could not create notification for domain event {domainEvent.GetType().Name}.");

            await _publisher.Publish(notification, cancellationToken);
        }
    }
}
