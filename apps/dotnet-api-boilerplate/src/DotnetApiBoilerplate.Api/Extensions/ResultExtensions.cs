using DotnetApiBoilerplate.Domain.Common;
using Microsoft.AspNetCore.Mvc;

namespace DotnetApiBoilerplate.Api.Extensions;

/// <summary>Maps the domain <see cref="Result" /> pattern to MVC responses (RFC 7807 problems on failure).</summary>
public static class ResultExtensions
{
    /// <summary>Returns <paramref name="onSuccess" /> when successful, otherwise an RFC 7807 problem.</summary>
    public static IActionResult Match<TValue>(this Result<TValue> result, Func<TValue, IActionResult> onSuccess) =>
        result.IsSuccess ? onSuccess(result.Value) : result.Error.ToProblem();

    /// <summary>Returns <paramref name="onSuccess" /> when successful, otherwise an RFC 7807 problem.</summary>
    public static IActionResult Match(this Result result, Func<IActionResult> onSuccess) =>
        result.IsSuccess ? onSuccess() : result.Error.ToProblem();

    public static ObjectResult ToProblem(this Error error)
    {
        int statusCode = error.Type switch
        {
            ErrorType.Validation => StatusCodes.Status400BadRequest,
            ErrorType.Unauthorized => StatusCodes.Status401Unauthorized,
            ErrorType.Forbidden => StatusCodes.Status403Forbidden,
            ErrorType.NotFound => StatusCodes.Status404NotFound,
            ErrorType.Conflict => StatusCodes.Status409Conflict,
            _ => StatusCodes.Status500InternalServerError
        };

        ProblemDetails problem = new()
        {
            Title = ReasonPhrase(error.Type),
            Detail = error.Description,
            Status = statusCode,
            Extensions = { ["code"] = error.Code }
        };

        return new ObjectResult(problem)
        {
            StatusCode = statusCode,
            ContentTypes = { "application/problem+json" }
        };
    }

    private static string ReasonPhrase(ErrorType type) => type switch
    {
        ErrorType.Validation => "Validation failed",
        ErrorType.Unauthorized => "Unauthorized",
        ErrorType.Forbidden => "Forbidden",
        ErrorType.NotFound => "Resource not found",
        ErrorType.Conflict => "Conflict",
        _ => "An error occurred"
    };
}
