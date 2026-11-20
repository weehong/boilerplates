using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class CommonErrorsTests
{
    [Fact]
    public void NullValue_ShouldBeAValidationError()
    {
        CommonErrors.NullValue.Code.ShouldBe("General.NullValue");
        CommonErrors.NullValue.Type.ShouldBe(ErrorType.Validation);
    }

    [Fact]
    public void ConcurrencyConflict_ShouldBeAConflictError()
    {
        CommonErrors.ConcurrencyConflict.Code.ShouldBe("General.ConcurrencyConflict");
        CommonErrors.ConcurrencyConflict.Type.ShouldBe(ErrorType.Conflict);
    }

    [Fact]
    public void NotFound_ShouldNameTheEntityInCodeAndDescription()
    {
        Guid id = Guid.NewGuid();

        Error error = CommonErrors.NotFound("Widget", id);

        error.Code.ShouldBe("Widget.NotFound");
        error.Description.ShouldContain(id.ToString());
        error.Type.ShouldBe(ErrorType.NotFound);
    }
}
