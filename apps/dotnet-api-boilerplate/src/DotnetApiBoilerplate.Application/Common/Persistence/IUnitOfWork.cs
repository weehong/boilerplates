namespace DotnetApiBoilerplate.Application.Common.Persistence;

/// <summary>
///     Commits all changes tracked within a single business transaction.
///     Implemented by the persistence layer; consumed by command handlers.
/// </summary>
public interface IUnitOfWork
{
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
