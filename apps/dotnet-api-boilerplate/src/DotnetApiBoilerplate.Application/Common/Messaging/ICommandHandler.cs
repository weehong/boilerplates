using DotnetApiBoilerplate.Domain.Common;
using MediatR;

namespace DotnetApiBoilerplate.Application.Common.Messaging;

/// <summary>Handles an <see cref="ICommand" />.</summary>
public interface ICommandHandler<in TCommand> : IRequestHandler<TCommand, Result>
    where TCommand : ICommand;

/// <summary>Handles an <see cref="ICommand{TResponse}" />.</summary>
public interface ICommandHandler<in TCommand, TResponse> : IRequestHandler<TCommand, Result<TResponse>>
    where TCommand : ICommand<TResponse>;
