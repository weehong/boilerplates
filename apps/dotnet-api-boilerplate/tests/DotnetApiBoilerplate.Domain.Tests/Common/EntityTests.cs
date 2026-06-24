using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class EntityTests
{
    [Fact]
    public void RaiseDomainEvent_ShouldBufferEvent()
    {
        TestEntity entity = new(Guid.NewGuid());

        entity.DoSomething();

        entity.DomainEvents.Count.ShouldBe(1);
        entity.DomainEvents.ShouldHaveSingleItem().ShouldBeOfType<TestEvent>();
    }

    [Fact]
    public void ClearDomainEvents_ShouldEmptyBuffer()
    {
        TestEntity entity = new(Guid.NewGuid());
        entity.DoSomething();

        entity.ClearDomainEvents();

        entity.DomainEvents.ShouldBeEmpty();
    }

    [Fact]
    public void Entities_WithSameId_ShouldBeEqual()
    {
        Guid id = Guid.NewGuid();

        TestEntity a = new(id);
        TestEntity b = new(id);

        a.ShouldBe(b);
        (a == b).ShouldBeTrue();
        a.GetHashCode().ShouldBe(b.GetHashCode());
    }

    [Fact]
    public void Entities_WithDifferentIds_ShouldNotBeEqual()
    {
        TestEntity a = new(Guid.NewGuid());
        TestEntity b = new(Guid.NewGuid());

        (a != b).ShouldBeTrue();
    }

    private sealed record TestEvent(DateTimeOffset OccurredOnUtc) : IDomainEvent;

    private sealed class TestEntity(Guid id) : Entity(id)
    {
        public void DoSomething() => RaiseDomainEvent(new TestEvent(DateTimeOffset.UtcNow));
    }
}
