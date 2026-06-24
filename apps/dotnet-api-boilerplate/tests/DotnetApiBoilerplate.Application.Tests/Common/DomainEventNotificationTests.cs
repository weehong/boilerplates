using DotnetApiBoilerplate.Application.Common.Abstractions;
using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Application.Tests.Common;

public sealed class DomainEventNotificationTests
{
    [Fact]
    public void Notification_ShouldWrapTheDomainEvent()
    {
        SampleEvent domainEvent = new(DateTimeOffset.UtcNow);

        DomainEventNotification<SampleEvent> notification = new(domainEvent);

        notification.DomainEvent.ShouldBe(domainEvent);
    }

    private sealed record SampleEvent(DateTimeOffset OccurredOnUtc) : IDomainEvent;
}
