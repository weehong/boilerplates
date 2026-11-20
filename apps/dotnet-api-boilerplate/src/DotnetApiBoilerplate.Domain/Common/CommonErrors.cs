namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Errors that apply to any feature. Errors specific to one aggregate live beside it
///     (for example <c>WidgetErrors</c>) and use the <c>&lt;Aggregate&gt;.&lt;Reason&gt;</c> code format.
/// </summary>
public static class CommonErrors
{
    public static readonly Error NullValue =
        Error.Validation("General.NullValue", "A required value was not provided.");

    public static readonly Error ConcurrencyConflict =
        Error.Conflict(
            "General.ConcurrencyConflict",
            "The resource was changed by another request. Reload it and try again.");

    public static Error NotFound(string entityName, object id) =>
        Error.NotFound($"{entityName}.NotFound", $"{entityName} with id '{id}' was not found.");
}
