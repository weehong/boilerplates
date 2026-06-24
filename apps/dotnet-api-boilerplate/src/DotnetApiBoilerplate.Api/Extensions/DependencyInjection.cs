using DotnetApiBoilerplate.Api.Infrastructure;

namespace DotnetApiBoilerplate.Api.Extensions;

/// <summary>Registration entry point for the API/presentation layer.</summary>
public static class DependencyInjection
{
    public static IServiceCollection AddApi(this IServiceCollection services)
    {
        services.AddControllers();

        services.AddOpenApi();

        // RFC 7807 problem responses + a single handler that maps exceptions to them.
        services.AddProblemDetails(options =>
            options.CustomizeProblemDetails = context =>
            {
                context.ProblemDetails.Instance ??= context.HttpContext.Request.Path;
                context.ProblemDetails.Extensions.TryAdd("traceId", context.HttpContext.TraceIdentifier);
            });
        services.AddExceptionHandler<GlobalExceptionHandler>();

        return services;
    }
}
