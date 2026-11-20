using System.Reflection;
using DotnetApiBoilerplate.Domain.Common;
using Shouldly;

namespace DotnetApiBoilerplate.Domain.Tests.Common;

public sealed class AuditableAggregateRootTests
{
    private static readonly DateTimeOffset s_created = new(2026, 1, 1, 8, 0, 0, TimeSpan.Zero);

    [Fact]
    public void SetCreated_ShouldStampCreationTime()
    {
        TestAudited aggregate = new(Guid.NewGuid());

        ((IAuditableEntity)aggregate).SetCreated(s_created);

        aggregate.CreatedOnUtc.ShouldBe(s_created);
        aggregate.ModifiedOnUtc.ShouldBeNull();
    }

    [Fact]
    public void SetCreated_Twice_ShouldThrow()
    {
        IAuditableEntity aggregate = new TestAudited(Guid.NewGuid());
        aggregate.SetCreated(s_created);

        Should.Throw<InvalidOperationException>(() => aggregate.SetCreated(s_created.AddDays(1)));
    }

    [Fact]
    public void SetModified_ShouldStampModificationTime()
    {
        IAuditableEntity aggregate = new TestAudited(Guid.NewGuid());
        DateTimeOffset modified = s_created.AddHours(1);

        aggregate.SetModified(modified);

        aggregate.ModifiedOnUtc.ShouldBe(modified);
    }

    [Theory]
    [InlineData(nameof(IAuditableEntity.SetCreated))]
    [InlineData(nameof(IAuditableEntity.SetModified))]
    public void AuditSetters_ShouldNotBePublicOnTheEntity(string methodName) =>
        typeof(TestAudited)
            .GetMethod(methodName, BindingFlags.Public | BindingFlags.Instance)
            .ShouldBeNull();

    [Fact]
    public void Constructor_WithEmptyGuid_ShouldThrow() =>
        Should.Throw<ArgumentException>(() => new TestAudited(Guid.Empty));

    private sealed class TestAudited(Guid id) : AuditableAggregateRoot(id);
}
