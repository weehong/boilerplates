using DotnetApiBoilerplate.Domain.Common;
using MediatR;

namespace DotnetApiBoilerplate.Application.Common.Abstractions;

/// <summary>
///     Adapts a pure <see cref="IDomainEvent" /> into a MediatR <see cref="INotification" /> so the
///     Domain layer need not know about MediatR. Handlers implement
///     <c>INotificationHandler&lt;DomainEventNotification&lt;TDomainEvent&gt;&gt;</c>.
/// </summary>
public sealed record DomainEventNotification<TDomainEvent>(TDomainEvent DomainEvent) : INotification
    where TDomainEvent : IDomainEvent;
