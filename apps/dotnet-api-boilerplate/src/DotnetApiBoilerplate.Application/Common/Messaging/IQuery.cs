using DotnetApiBoilerplate.Domain.Common;
using MediatR;

namespace DotnetApiBoilerplate.Application.Common.Messaging;

/// <summary>A read-only request returning a value on success.</summary>
public interface IQuery<TResponse> : IRequest<Result<TResponse>>;
