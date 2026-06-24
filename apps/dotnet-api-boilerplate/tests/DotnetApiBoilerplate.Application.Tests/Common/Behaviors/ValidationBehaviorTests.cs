using DotnetApiBoilerplate.Application.Common.Behaviors;
using FluentValidation;
using FluentValidation.Results;
using MediatR;
using NSubstitute;
using Shouldly;
using ValidationException = DotnetApiBoilerplate.Application.Common.Exceptions.ValidationException;

namespace DotnetApiBoilerplate.Application.Tests.Common.Behaviors;

public sealed class ValidationBehaviorTests
{
    private static RequestHandlerDelegate<string> NextReturning(string value)
    {
        RequestHandlerDelegate<string> next = Substitute.For<RequestHandlerDelegate<string>>();
        next(Arg.Any<CancellationToken>()).Returns(value);
        return next;
    }

    [Fact]
    public async Task Handle_WithNoValidators_ShouldCallNext()
    {
        ValidationBehavior<TestRequest, string> behavior = new([]);

        string result = await behavior.Handle(new TestRequest("x"), NextReturning("passed"), CancellationToken.None);

        result.ShouldBe("passed");
    }

    [Fact]
    public async Task Handle_WhenValidationPasses_ShouldCallNext()
    {
        IValidator<TestRequest> validator = Substitute.For<IValidator<TestRequest>>();
        validator
            .ValidateAsync(Arg.Any<ValidationContext<TestRequest>>(), Arg.Any<CancellationToken>())
            .Returns(new ValidationResult());
        ValidationBehavior<TestRequest, string> behavior = new([validator]);

        string result = await behavior.Handle(new TestRequest("x"), NextReturning("valid"), CancellationToken.None);

        result.ShouldBe("valid");
    }

    [Fact]
    public async Task Handle_WhenValidationFails_ShouldThrowWithGroupedErrors()
    {
        IValidator<TestRequest> validator = Substitute.For<IValidator<TestRequest>>();
        validator
            .ValidateAsync(Arg.Any<ValidationContext<TestRequest>>(), Arg.Any<CancellationToken>())
            .Returns(new ValidationResult([
                new ValidationFailure("Name", "Name is required."),
                new ValidationFailure("Name", "Name is too short.")
            ]));
        ValidationBehavior<TestRequest, string> behavior = new([validator]);

        ValidationException exception = await Should.ThrowAsync<ValidationException>(() =>
            behavior.Handle(new TestRequest(""), NextReturning("never"), CancellationToken.None));

        exception.Errors.Keys.ShouldContain("Name");
        exception.Errors["Name"].ShouldBe(["Name is required.", "Name is too short."]);
    }

    public sealed record TestRequest(string Name);
}
