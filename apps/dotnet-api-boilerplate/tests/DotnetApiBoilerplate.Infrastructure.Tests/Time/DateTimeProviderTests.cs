using DotnetApiBoilerplate.Infrastructure.Time;
using Shouldly;

namespace DotnetApiBoilerplate.Infrastructure.Tests.Time;

public sealed class DateTimeProviderTests
{
    [Fact]
    public void UtcNow_ShouldReturnCurrentUtcInstant()
    {
        DateTimeProvider provider = new();

        DateTimeOffset before = DateTimeOffset.UtcNow;
        DateTimeOffset value = provider.UtcNow;
        DateTimeOffset after = DateTimeOffset.UtcNow;

        value.ShouldBeInRange(before, after);
        value.Offset.ShouldBe(TimeSpan.Zero);
    }
}
