using DotnetApiBoilerplate.Api.Infrastructure;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging.Abstractions;
using NSubstitute;
using Shouldly;
using ValidationException = DotnetApiBoilerplate.Application.Common.Exceptions.ValidationException;

namespace DotnetApiBoilerplate.Api.Tests.Infrastructure;

public sealed class GlobalExceptionHandlerTests
{
    // NullLogger (not a substitute): GlobalExceptionHandler is internal, so Castle/NSubstitute
    // cannot proxy ILogger<GlobalExceptionHandler> across the strong-named abstractions assembly.
    private static GlobalExceptionHandler CreateHandler(IProblemDetailsService problemDetailsService) =>
        new(problemDetailsService, NullLogger<GlobalExceptionHandler>.Instance);

    [Fact]
    public async Task TryHandleAsync_WithValidationException_ShouldWriteValidationProblem()
    {
        IProblemDetailsService problemDetailsService = Substitute.For<IProblemDetailsService>();
        problemDetailsService.TryWriteAsync(Arg.Any<ProblemDetailsContext>()).Returns(true);
        GlobalExceptionHandler handler = CreateHandler(problemDetailsService);
        DefaultHttpContext context = new();
        Dictionary<string, string[]> errors = new() { ["Name"] = ["Required."] };

        bool handled = await handler.TryHandleAsync(context, new ValidationException(errors), CancellationToken.None);

        handled.ShouldBeTrue();
        context.Response.StatusCode.ShouldBe(StatusCodes.Status400BadRequest);
        await problemDetailsService.Received(1).TryWriteAsync(
            Arg.Is<ProblemDetailsContext>(c => c.ProblemDetails is ValidationProblemDetails));
    }

    [Fact]
    public async Task TryHandleAsync_WithUnhandledException_ShouldWriteGeneric500()
    {
        IProblemDetailsService problemDetailsService = Substitute.For<IProblemDetailsService>();
        problemDetailsService.TryWriteAsync(Arg.Any<ProblemDetailsContext>()).Returns(true);
        GlobalExceptionHandler handler = CreateHandler(problemDetailsService);
        DefaultHttpContext context = new();

        bool handled = await handler.TryHandleAsync(
            context, new InvalidOperationException("boom"), CancellationToken.None);

        handled.ShouldBeTrue();
        context.Response.StatusCode.ShouldBe(StatusCodes.Status500InternalServerError);
    }

    [Fact]
    public async Task TryHandleAsync_WhenProblemDetailsCannotBeWritten_ShouldReturnFalse()
    {
        IProblemDetailsService problemDetailsService = Substitute.For<IProblemDetailsService>();
        problemDetailsService.TryWriteAsync(Arg.Any<ProblemDetailsContext>()).Returns(false);
        GlobalExceptionHandler handler = CreateHandler(problemDetailsService);

        bool handled = await handler.TryHandleAsync(
            new DefaultHttpContext(), new InvalidOperationException("boom"), CancellationToken.None);

        handled.ShouldBeFalse();
    }
}
