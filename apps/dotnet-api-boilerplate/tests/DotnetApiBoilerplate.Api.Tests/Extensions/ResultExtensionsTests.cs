using DotnetApiBoilerplate.Api.Extensions;
using DotnetApiBoilerplate.Domain.Common;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Shouldly;

namespace DotnetApiBoilerplate.Api.Tests.Extensions;

public sealed class ResultExtensionsTests
{
    [Fact]
    public void Match_Generic_WhenSuccess_ShouldInvokeOnSuccess()
    {
        Result<int> result = 7;
        bool invoked = false;

        IActionResult response = result.Match(value =>
        {
            invoked = true;
            value.ShouldBe(7);
            return new OkObjectResult(value);
        });

        invoked.ShouldBeTrue();
        response.ShouldBeOfType<OkObjectResult>().Value.ShouldBe(7);
    }

    [Fact]
    public void Match_Generic_WhenFailure_ShouldReturnProblem()
    {
        Result<int> result = Error.NotFound("X.NotFound", "missing");

        IActionResult response = result.Match(value => new OkObjectResult(value));

        ObjectResult problem = response.ShouldBeOfType<ObjectResult>();
        problem.StatusCode.ShouldBe(StatusCodes.Status404NotFound);
        problem.Value.ShouldBeOfType<ProblemDetails>().Status.ShouldBe(StatusCodes.Status404NotFound);
    }

    [Fact]
    public void Match_NonGeneric_WhenSuccess_ShouldInvokeOnSuccess()
    {
        Result result = Result.Success();

        IActionResult response = result.Match(() => new NoContentResult());

        response.ShouldBeOfType<NoContentResult>();
    }

    [Fact]
    public void Match_NonGeneric_WhenFailure_ShouldReturnProblem()
    {
        Result result = Error.Conflict("X.Conflict", "conflict");

        IActionResult response = result.Match(() => new NoContentResult());

        response.ShouldBeOfType<ObjectResult>()
            .StatusCode.ShouldBe(StatusCodes.Status409Conflict);
    }

    [Theory]
    [InlineData(ErrorType.Validation, StatusCodes.Status400BadRequest)]
    [InlineData(ErrorType.Unauthorized, StatusCodes.Status401Unauthorized)]
    [InlineData(ErrorType.Forbidden, StatusCodes.Status403Forbidden)]
    [InlineData(ErrorType.NotFound, StatusCodes.Status404NotFound)]
    [InlineData(ErrorType.Conflict, StatusCodes.Status409Conflict)]
    [InlineData(ErrorType.Failure, StatusCodes.Status500InternalServerError)]
    public void ToProblem_ShouldMapErrorTypeToStatusCode(ErrorType type, int expectedStatus)
    {
        Error error = new("Some.Code", "Some description", type);

        ObjectResult result = error.ToProblem();

        result.StatusCode.ShouldBe(expectedStatus);
        result.ContentTypes.ShouldContain("application/problem+json");

        ProblemDetails problem = result.Value.ShouldBeOfType<ProblemDetails>();
        problem.Status.ShouldBe(expectedStatus);
        problem.Detail.ShouldBe("Some description");
        problem.Extensions["code"].ShouldBe("Some.Code");
    }
}
