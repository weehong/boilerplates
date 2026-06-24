using DotnetApiBoilerplate.Application.Common.Exceptions;
using Shouldly;

namespace DotnetApiBoilerplate.Application.Tests.Common;

public sealed class ValidationExceptionTests
{
    [Fact]
    public void Constructor_ShouldExposeErrorsAndStandardMessage()
    {
        Dictionary<string, string[]> errors = new() { ["Name"] = ["Required."] };

        ValidationException exception = new(errors);

        exception.Errors.ShouldBe(errors);
        exception.Message.ShouldBe("One or more validation errors occurred.");
    }
}
