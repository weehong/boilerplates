using DotnetApiBoilerplate.Application.Common.Abstractions;

namespace DotnetApiBoilerplate.Infrastructure.Time;

/// <summary>System-clock implementation of <see cref="IDateTimeProvider" />.</summary>
internal sealed class DateTimeProvider : IDateTimeProvider
{
    public DateTimeOffset UtcNow => DateTimeOffset.UtcNow;
}
