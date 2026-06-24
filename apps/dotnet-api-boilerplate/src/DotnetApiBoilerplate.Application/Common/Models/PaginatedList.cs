namespace DotnetApiBoilerplate.Application.Common.Models;

/// <summary>An immutable page of results plus the metadata a client needs to navigate.</summary>
public sealed record PaginatedList<T>(
    IReadOnlyList<T> Items,
    int Page,
    int PageSize,
    int TotalCount)
{
    public int TotalPages => PageSize == 0 ? 0 : (int)Math.Ceiling(TotalCount / (double)PageSize);

    public bool HasPreviousPage => Page > 1;

    public bool HasNextPage => Page < TotalPages;

    public static PaginatedList<T> Create(IReadOnlyList<T> items, int totalCount, PaginationParameters pagination) =>
        new(items, pagination.Page, pagination.PageSize, totalCount);
}
