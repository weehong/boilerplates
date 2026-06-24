using DotnetApiBoilerplate.Application.Common.Behaviors;
using DotnetApiBoilerplate.Domain.Common;
using MediatR;
using Microsoft.Extensions.Logging;
using NSubstitute;
using NSubstitute.ExceptionExtensions;
using Shouldly;

namespace DotnetApiBoilerplate.Application.Tests.Common.Behaviors;

public sealed class LoggingBehaviorTests
{
    [Fact]
    public async Task Handle_ShouldReturnTheResponseFromNext()
    {
        ILogger<LoggingBehavior<TestRequest, string>> logger =
            Substitute.For<ILogger<LoggingBehavior<TestRequest, string>>>();
        LoggingBehavior<TestRequest, string> behavior = new(logger);
        RequestHandlerDelegate<string> next = Substitute.For<RequestHandlerDelegate<string>>();
        next(Arg.Any<CancellationToken>()).Returns("done");

        string result = await behavior.Handle(new TestRequest(), next, CancellationToken.None);

        result.ShouldBe("done");
    }

    [Fact]
    public async Task Handle_WhenResultSucceeds_ShouldReturnSuccess()
    {
        ILogger<LoggingBehavior<TestRequest, Result>> logger =
            Substitute.For<ILogger<LoggingBehavior<TestRequest, Result>>>();
        LoggingBehavior<TestRequest, Result> behavior = new(logger);
        RequestHandlerDelegate<Result> next = Substitute.For<RequestHandlerDelegate<Result>>();
        next(Arg.Any<CancellationToken>()).Returns(Result.Success());

        Result result = await behavior.Handle(new TestRequest(), next, CancellationToken.None);

        result.IsSuccess.ShouldBeTrue();
    }

    [Fact]
    public async Task Handle_WhenResultFails_ShouldReturnFailure()
    {
        ILogger<LoggingBehavior<TestRequest, Result>> logger =
            Substitute.For<ILogger<LoggingBehavior<TestRequest, Result>>>();
        LoggingBehavior<TestRequest, Result> behavior = new(logger);
        RequestHandlerDelegate<Result> next = Substitute.For<RequestHandlerDelegate<Result>>();
        next(Arg.Any<CancellationToken>()).Returns(Result.Failure(Error.Conflict("X.Conflict", "boom")));

        Result result = await behavior.Handle(new TestRequest(), next, CancellationToken.None);

        result.IsFailure.ShouldBeTrue();
        result.Error.Code.ShouldBe("X.Conflict");
    }

    [Fact]
    public async Task Handle_WhenNextThrows_ShouldLogErrorAndRethrow()
    {
        ILogger<LoggingBehavior<TestRequest, string>> logger =
            Substitute.For<ILogger<LoggingBehavior<TestRequest, string>>>();
        LoggingBehavior<TestRequest, string> behavior = new(logger);
        RequestHandlerDelegate<string> next = Substitute.For<RequestHandlerDelegate<string>>();
        InvalidOperationException expected = new("boom");
        next(Arg.Any<CancellationToken>()).ThrowsAsync(expected);

        InvalidOperationException thrown =
            await Should.ThrowAsync<InvalidOperationException>(() =>
                behavior.Handle(new TestRequest(), next, CancellationToken.None));

        thrown.ShouldBeSameAs(expected);
        logger.Received(1).Log(
            LogLevel.Error,
            Arg.Any<EventId>(),
            Arg.Is<object>(state => state.ToString()!.Contains(nameof(TestRequest))),
            expected,
            Arg.Any<Func<object, Exception?, string>>());
    }

    public sealed record TestRequest;
}
