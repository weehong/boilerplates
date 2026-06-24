using DotnetApiBoilerplate.Domain.Common;
using MediatR;

namespace DotnetApiBoilerplate.Application.Common.Messaging;

/// <summary>Handles an <see cref="IQuery{TResponse}" />.</summary>
public interface IQueryHandler<in TQuery, TResponse> : IRequestHandler<TQuery, Result<TResponse>>
    where TQuery : IQuery<TResponse>;
