namespace DotnetApiBoilerplate.Application.Common.Exceptions;

/// <summary>
///     Thrown by the validation pipeline when input fails FluentValidation rules.
///     Carries a field-keyed error map that the API maps to an RFC 7807 validation problem (HTTP 400).
///     Input shape validation uses exceptions; business-rule failures use the Result pattern.
/// </summary>
public sealed class ValidationException(IReadOnlyDictionary<string, string[]> errors)
    : Exception("One or more validation errors occurred.")
{
    public IReadOnlyDictionary<string, string[]> Errors { get; } = errors;
}
