using DotnetApiBoilerplate.Application.Common.Models;
using Shouldly;

namespace DotnetApiBoilerplate.Application.Tests.Common;

public sealed class PaginationParametersTests
{
    [Theory]
    [InlineData(0, 1)]
    [InlineData(-5, 1)]
    [InlineData(3, 3)]
    public void Page_ShouldBeClampedToAtLeastOne(int requested, int expected) =>
        new PaginationParameters(requested).Page.ShouldBe(expected);

    [Theory]
    [InlineData(0, PaginationParameters.DefaultPageSize)]
    [InlineData(1000, PaginationParameters.MaxPageSize)]
    [InlineData(25, 25)]
    public void PageSize_ShouldBeClampedToBounds(int requested, int expected) =>
        new PaginationParameters(1, requested).PageSize.ShouldBe(expected);

    [Fact]
    public void Skip_ShouldReflectPageAndSize() =>
        new PaginationParameters(3, 10).Skip.ShouldBe(20);
}
