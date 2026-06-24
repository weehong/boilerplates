using System.Net;
using Shouldly;

namespace DotnetApiBoilerplate.IntegrationTests;

[Collection(IntegrationTestCollection.Name)]
public sealed class HealthEndpointTests(DotnetApiBoilerplateWebApplicationFactory factory)
{
    private readonly DotnetApiBoilerplateWebApplicationFactory _factory = factory;

    [Fact]
    public async Task Health_ShouldReturnHealthy()
    {
        CancellationToken cancellationToken = TestContext.Current.CancellationToken;
        HttpClient client = _factory.CreateClient();

        HttpResponseMessage response = await client.GetAsync("/health", cancellationToken);

        response.StatusCode.ShouldBe(HttpStatusCode.OK);
        (await response.Content.ReadAsStringAsync(cancellationToken)).ShouldBe("Healthy");
    }
}
