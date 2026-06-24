using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class EntityEqualityTests
{
    [Fact]
    public void Equals_WithNull_ShouldBeFalse()
    {
        FooEntity entity = new(Guid.NewGuid());

        entity.Equals(null).ShouldBeFalse();
        entity.Equals((object?)null).ShouldBeFalse();
    }

    [Fact]
    public void Equals_SameReference_ShouldBeTrue()
    {
        FooEntity entity = new(Guid.NewGuid());

        entity.Equals(entity).ShouldBeTrue();
    }

    [Fact]
    public void Equals_DifferentTypeSameId_ShouldBeFalse()
    {
        Guid id = Guid.NewGuid();

        new FooEntity(id).Equals(new BarEntity(id)).ShouldBeFalse();
    }

    [Fact]
    public void Equals_NonEntityObject_ShouldBeFalse()
    {
        FooEntity entity = new(Guid.NewGuid());

        entity.Equals("not an entity").ShouldBeFalse();
    }

    [Fact]
    public void Operators_ShouldHandleNullsAndValues()
    {
        FooEntity? left = null;
        FooEntity? right = null;
        FooEntity entity = new(Guid.NewGuid());

        (left == right).ShouldBeTrue();
        (left == entity).ShouldBeFalse();
        (entity != left).ShouldBeTrue();
    }

    private sealed class FooEntity(Guid id) : Entity(id);

    private sealed class BarEntity(Guid id) : Entity(id);
}
