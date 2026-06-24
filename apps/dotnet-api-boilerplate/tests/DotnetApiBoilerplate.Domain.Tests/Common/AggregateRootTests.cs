using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class AggregateRootTests
{
    [Fact]
    public void AggregateRoot_ShouldExposeItsIdentity()
    {
        Guid id = Guid.NewGuid();

        new TestAggregate(id).Id.ShouldBe(id);
    }

    [Fact]
    public void AggregateRoot_ShouldRaiseDomainEvents()
    {
        TestAggregate aggregate = new(Guid.NewGuid());

        aggregate.Create();

        aggregate.DomainEvents.ShouldHaveSingleItem().ShouldBeOfType<CreatedEvent>();
    }

    [Fact]
    public void AggregateRoots_WithSameId_ShouldBeEqual()
    {
        Guid id = Guid.NewGuid();

        new TestAggregate(id).ShouldBe(new TestAggregate(id));
    }

    private sealed record CreatedEvent(DateTimeOffset OccurredOnUtc) : IDomainEvent;

    private sealed class TestAggregate(Guid id) : AggregateRoot(id)
    {
        public void Create() => RaiseDomainEvent(new CreatedEvent(DateTimeOffset.UtcNow));
    }
}
