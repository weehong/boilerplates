using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class ResultTests
{
    [Fact]
    public void Success_ShouldHaveNoError()
    {
        Result result = Result.Success();

        result.IsSuccess.ShouldBeTrue();
        result.IsFailure.ShouldBeFalse();
        result.Error.ShouldBe(Error.None);
    }

    [Fact]
    public void Failure_ShouldCarryError()
    {
        Error error = Error.NotFound("X.NotFound", "Not found.");

        Result result = Result.Failure(error);

        result.IsFailure.ShouldBeTrue();
        result.Error.ShouldBe(error);
    }

    [Fact]
    public void Failure_WithNoneError_ShouldThrow() =>
        Should.Throw<InvalidOperationException>(() => Result.Failure(Error.None));

    [Fact]
    public void Constructor_SuccessfulResultWithError_ShouldThrow() =>
        Should.Throw<InvalidOperationException>(() => new SuccessWithErrorResult());

    [Fact]
    public void GenericSuccess_ShouldExposeValue()
    {
        Result<int> result = 42;

        result.IsSuccess.ShouldBeTrue();
        result.Value.ShouldBe(42);
    }

    [Fact]
    public void GenericFailure_AccessingValue_ShouldThrow()
    {
        Result<int> result = Error.Failure("X", "boom");

        result.IsFailure.ShouldBeTrue();
        Should.Throw<InvalidOperationException>(() => result.Value);
    }

    [Fact]
    public void ImplicitConversion_FromError_ShouldProduceFailure()
    {
        Result result = Error.Conflict("X.Conflict", "conflict");

        result.IsFailure.ShouldBeTrue();
        result.Error.Type.ShouldBe(ErrorType.Conflict);
    }

    // Exercises the protected constructor's guard against a "successful" result that
    // still carries an error — unreachable via the public factories, so a tiny
    // subclass calls the protected ctor directly.
    private sealed class SuccessWithErrorResult : Result
    {
        public SuccessWithErrorResult()
            : base(true, Error.Conflict("X.Conflict", "successful results must not carry an error"))
        {
        }
    }
}
