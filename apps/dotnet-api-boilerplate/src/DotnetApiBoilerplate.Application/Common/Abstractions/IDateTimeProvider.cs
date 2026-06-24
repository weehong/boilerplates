namespace DotnetApiBoilerplate.Application.Common.Abstractions;

/// <summary>
///     Abstraction over the system clock so time-dependent logic stays testable.
///     Implemented in Infrastructure.
/// </summary>
public interface IDateTimeProvider
{
    DateTimeOffset UtcNow { get; }
}
