using DotnetApiBoilerplate.Api.Controllers;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Shouldly;

namespace DotnetApiBoilerplate.Api.Tests.Controllers;

public sealed class SamplesControllerTests
{
    private static readonly Guid KnownId = new("11111111-1111-1111-1111-111111111111");

    private readonly SamplesController _controller = new();

    [Fact]
    public void GetAll_ShouldReturnOkWithAllSamples()
    {
        IActionResult response = _controller.GetAll();

        OkObjectResult ok = response.ShouldBeOfType<OkObjectResult>();
        IReadOnlyCollection<SampleResponse> samples =
            ok.Value.ShouldBeAssignableTo<IReadOnlyCollection<SampleResponse>>()!;

        samples.Count.ShouldBe(2);
        samples.ShouldContain(s => s.Id == KnownId && s.Name == "First sample");
    }

    [Fact]
    public void GetById_WhenSampleExists_ShouldReturnOkWithSample()
    {
        IActionResult response = _controller.GetById(KnownId);

        OkObjectResult ok = response.ShouldBeOfType<OkObjectResult>();
        SampleResponse sample = ok.Value.ShouldBeOfType<SampleResponse>();

        sample.Id.ShouldBe(KnownId);
        sample.Name.ShouldBe("First sample");
    }

    [Fact]
    public void GetById_WhenSampleMissing_ShouldReturnNotFoundProblem()
    {
        IActionResult response = _controller.GetById(Guid.NewGuid());

        ObjectResult problem = response.ShouldBeOfType<ObjectResult>();
        problem.StatusCode.ShouldBe(StatusCodes.Status404NotFound);

        ProblemDetails details = problem.Value.ShouldBeOfType<ProblemDetails>();
        details.Status.ShouldBe(StatusCodes.Status404NotFound);
        details.Extensions["code"].ShouldBe("Samples.NotFound");
    }
}
