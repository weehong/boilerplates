using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class ErrorTests
{
    [Fact]
    public void None_ShouldBeAnEmptyFailure()
    {
        Error.None.Code.ShouldBe(string.Empty);
        Error.None.Description.ShouldBe(string.Empty);
        Error.None.Type.ShouldBe(ErrorType.Failure);
    }

    // ErrorType is used as the (serializable) row key so Test Explorer enumerates each case;
    // the matching factory is selected inside the test rather than passed as a Func.
    [Theory]
    [InlineData(ErrorType.Failure)]
    [InlineData(ErrorType.Validation)]
    [InlineData(ErrorType.NotFound)]
    [InlineData(ErrorType.Conflict)]
    [InlineData(ErrorType.Unauthorized)]
    [InlineData(ErrorType.Forbidden)]
    public void Factory_ShouldSetCodeDescriptionAndType(ErrorType type)
    {
        Error error = type switch
        {
            ErrorType.Failure => Error.Failure("Some.Code", "Some description."),
            ErrorType.Validation => Error.Validation("Some.Code", "Some description."),
            ErrorType.NotFound => Error.NotFound("Some.Code", "Some description."),
            ErrorType.Conflict => Error.Conflict("Some.Code", "Some description."),
            ErrorType.Unauthorized => Error.Unauthorized("Some.Code", "Some description."),
            ErrorType.Forbidden => Error.Forbidden("Some.Code", "Some description."),
            _ => throw new ArgumentOutOfRangeException(nameof(type))
        };

        error.Code.ShouldBe("Some.Code");
        error.Description.ShouldBe("Some description.");
        error.Type.ShouldBe(type);
    }

    [Fact]
    public void Errors_WithSameValues_ShouldBeEqual()
    {
        Error a = Error.Validation("Code", "Description");
        Error b = Error.Validation("Code", "Description");

        a.ShouldBe(b);
        (a == b).ShouldBeTrue();
    }

    [Fact]
    public void Errors_WithDifferentValues_ShouldNotBeEqual() =>
        Error.NotFound("A", "x").ShouldNotBe(Error.NotFound("B", "x"));
}
