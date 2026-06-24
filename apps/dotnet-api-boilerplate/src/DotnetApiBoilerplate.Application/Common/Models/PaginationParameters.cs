namespace DotnetApiBoilerplate.Application.Common.Models;

/// <summary>
///     Normalized paging input. Clamps to sane bounds so callers cannot request page 0 or an unbounded page size.
/// </summary>
public sealed record PaginationParameters
{
    public const int DefaultPageSize = 20;
    public const int MaxPageSize = 100;

    public PaginationParameters(int page = 1, int pageSize = DefaultPageSize)
    {
        Page = page < 1 ? 1 : page;
        PageSize = pageSize switch
        {
            < 1 => DefaultPageSize,
            > MaxPageSize => MaxPageSize,
            _ => pageSize
        };
    }

    public int Page { get; }

    public int PageSize { get; }

    public int Skip => (Page - 1) * PageSize;
}
