using DotnetApiBoilerplate.Api.Extensions;
using DotnetApiBoilerplate.Domain.Common;
using Microsoft.AspNetCore.Mvc;

namespace DotnetApiBoilerplate.Api.Controllers;

/// <summary>
///     Example controller demonstrating the API-layer conventions: attribute routing under
///     <c>/api/v1</c> (inherited from <see cref="ApiControllerBase" />), response contracts kept
///     separate from domain entities, and translating a domain <see cref="Result" /> into an
///     <see cref="IActionResult" /> with <c>result.Match(...)</c>.
///     <para>
///         In a real feature, inject <c>MediatR.ISender</c> and replace the in-memory data with
///         <c>sender.Send(query, cancellationToken)</c>, then <c>Match</c> the returned
///         <see cref="Result{TValue}" /> — see the "How to add a new feature" recipe in CLAUDE.md.
///     </para>
/// </summary>
public sealed class SamplesController : ApiControllerBase
{
    private static readonly SampleResponse[] Samples =
    [
        new(new Guid("11111111-1111-1111-1111-111111111111"), "First sample"),
        new(new Guid("22222222-2222-2222-2222-222222222222"), "Second sample")
    ];

    /// <summary>Returns all samples.</summary>
    [HttpGet]
    [ProducesResponseType<IReadOnlyCollection<SampleResponse>>(StatusCodes.Status200OK)]
    public IActionResult GetAll() => Ok(Samples);

    /// <summary>Returns a single sample by id, or a 404 problem when it does not exist.</summary>
    [HttpGet("{id:guid}")]
    [ProducesResponseType<SampleResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public IActionResult GetById(Guid id)
    {
        Result<SampleResponse> result = Array.Find(Samples, sample => sample.Id == id) is { } found
            ? found
            : Error.NotFound("Samples.NotFound", $"No sample with id '{id}' was found.");

        return result.Match(sample => Ok(sample));
    }
}

/// <summary>Response contract for a sample resource (kept separate from any domain entity).</summary>
public sealed record SampleResponse(Guid Id, string Name);
