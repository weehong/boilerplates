namespace DotnetApiBoilerplate.Domain.Common;

/// <summary>
///     Guards for <see cref="Guid" /> identifiers. Only the Guid-keyed bases use it: the generic
///     <see cref="Entity{TId}" /> stays open to keys the database generates on insert.
/// </summary>
internal static class EntityId
{
    public static Guid EnsureNotEmpty(Guid id) =>
        id == Guid.Empty
            ? throw new ArgumentException("An entity identifier cannot be Guid.Empty.", nameof(id))
            : id;
}
