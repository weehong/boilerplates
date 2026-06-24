using System.Net;
using System.Net.Http.Json;
using DotnetApiBoilerplate.Api.Controllers;
using Shouldly;

namespace DotnetApiBoilerplate.IntegrationTests;

/// <summary>
///     End-to-end checks that the MVC controller pipeline is wired up: routing under
///     <c>/api/v1</c>, JSON responses, and the <c>Result.Match</c> → RFC 7807 problem mapping.
/// </summary>
[Collection(IntegrationTestCollection.Name)]
public sealed class SamplesEndpointTests(DotnetApiBoilerplateWebApplicationFactory factory)
{
    private readonly DotnetApiBoilerplateWebApplicationFactory _factory = factory;

    [Fact]
    public async Task GetAll_ShouldReturnOkWithSamples()
    {
        CancellationToken cancellationToken = TestContext.Current.CancellationToken;
        HttpClient client = _factory.CreateClient();

        HttpResponseMessage response = await client.GetAsync("/api/v1/samples", cancellationToken);

        response.StatusCode.ShouldBe(HttpStatusCode.OK);
        SampleResponse[]? samples = await response.Content.ReadFromJsonAsync<SampleResponse[]>(cancellationToken);
        samples.ShouldNotBeNull();
        samples.ShouldNotBeEmpty();
    }

    [Fact]
    public async Task GetById_WhenUnknown_ShouldReturnNotFoundProblem()
    {
        CancellationToken cancellationToken = TestContext.Current.CancellationToken;
        HttpClient client = _factory.CreateClient();

        HttpResponseMessage response = await client.GetAsync($"/api/v1/samples/{Guid.NewGuid()}", cancellationToken);

        response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
        response.Content.Headers.ContentType?.MediaType.ShouldBe("application/problem+json");
    }
}
