using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class ResultGenericTests
{
    [Fact]
    public void StaticSuccess_WithValue_ShouldExposeValue()
    {
        Result<int> result = Result.Success(123);

        result.IsSuccess.ShouldBeTrue();
        result.IsFailure.ShouldBeFalse();
        result.Value.ShouldBe(123);
        result.Error.ShouldBe(Error.None);
    }

    [Fact]
    public void StaticFailure_OfT_ShouldCarryErrorAndBlockValue()
    {
        Error error = Error.NotFound("X.NotFound", "nope");

        Result<int> result = Result.Failure<int>(error);

        result.IsFailure.ShouldBeTrue();
        result.Error.ShouldBe(error);
        Should.Throw<InvalidOperationException>(() => result.Value);
    }

    [Fact]
    public void ImplicitConversion_FromValue_ShouldProduceSuccess()
    {
        Result<string> result = "hello";

        result.IsSuccess.ShouldBeTrue();
        result.Value.ShouldBe("hello");
    }

    [Fact]
    public void ImplicitConversion_FromError_ShouldProduceFailure()
    {
        Result<string> result = Error.Validation("X.Invalid", "bad");

        result.IsFailure.ShouldBeTrue();
        result.Error.Type.ShouldBe(ErrorType.Validation);
    }

    [Fact]
    public void Failure_OfT_WithNoneError_ShouldThrow() =>
        Should.Throw<InvalidOperationException>(() => Result.Failure<int>(Error.None));
}
