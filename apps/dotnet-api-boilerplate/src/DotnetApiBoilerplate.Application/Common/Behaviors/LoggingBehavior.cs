using DotnetApiBoilerplate.Domain.Common;
using MediatR;
using Microsoft.Extensions.Logging;

namespace DotnetApiBoilerplate.Application.Common.Behaviors;

/// <summary>
///     MediatR pipeline behavior that logs the start, outcome, and failures of each request.
///     Result failures are logged at Warning; unhandled exceptions are logged at Error with the
///     request name, then rethrown so <c>GlobalExceptionHandler</c> still owns the HTTP response.
/// </summary>
public sealed class LoggingBehavior<TRequest, TResponse>(ILogger<LoggingBehavior<TRequest, TResponse>> logger)
    : IPipelineBehavior<TRequest, TResponse>
    where TRequest : notnull
{
    private readonly ILogger<LoggingBehavior<TRequest, TResponse>> _logger = logger;

    public async Task<TResponse> Handle(
        TRequest request,
        RequestHandlerDelegate<TResponse> next,
        CancellationToken cancellationToken)
    {
        string requestName = typeof(TRequest).Name;
        _logger.LogInformation("Handling {RequestName}", requestName);

        try
        {
            TResponse response = await next(cancellationToken);

            if (response is Result { IsFailure: true } result)
            {
                _logger.LogWarning(
                    "{RequestName} completed with failure {ErrorCode}: {ErrorDescription}",
                    requestName,
                    result.Error.Code,
                    result.Error.Description);
            }
            else
            {
                _logger.LogInformation("{RequestName} handled successfully", requestName);
            }

            return response;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "{RequestName} threw an unhandled exception", requestName);
            throw;
        }
    }
}
