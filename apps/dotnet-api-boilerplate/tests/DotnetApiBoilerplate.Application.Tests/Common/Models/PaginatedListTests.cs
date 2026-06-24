using DotnetApiBoilerplate.Application.Common.Models;
using Shouldly;

namespace DotnetApiBoilerplate.Application.Tests.Common.Models;

public sealed class PaginatedListTests
{
    [Fact]
    public void TotalPages_ShouldRoundUp()
    {
        PaginatedList<int> list = new([1, 2, 3], 1, 10, 25);

        list.TotalPages.ShouldBe(3);
    }

    [Fact]
    public void TotalPages_WithZeroPageSize_ShouldBeZero()
    {
        PaginatedList<int> list = new([], 1, 0, 25);

        list.TotalPages.ShouldBe(0);
    }

    [Theory]
    [InlineData(1, false)]
    [InlineData(2, true)]
    public void HasPreviousPage_ShouldReflectPage(int page, bool expected)
    {
        PaginatedList<int> list = new([], page, 10, 100);

        list.HasPreviousPage.ShouldBe(expected);
    }

    [Theory]
    [InlineData(1, true)]
    [InlineData(10, false)]
    public void HasNextPage_ShouldReflectPosition(int page, bool expected)
    {
        PaginatedList<int> list = new([], page, 10, 100);

        list.HasNextPage.ShouldBe(expected);
    }

    [Fact]
    public void Create_ShouldProjectPaginationParameters()
    {
        PaginationParameters pagination = new(2, 15);

        PaginatedList<string> list = PaginatedList<string>.Create(["a"], 30, pagination);

        list.Page.ShouldBe(2);
        list.PageSize.ShouldBe(15);
        list.TotalCount.ShouldBe(30);
        list.Items.ShouldHaveSingleItem().ShouldBe("a");
    }
}
