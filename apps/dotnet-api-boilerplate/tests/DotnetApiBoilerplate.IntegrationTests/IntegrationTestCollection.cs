namespace DotnetApiBoilerplate.IntegrationTests;

/// <summary>
///     Shares a single <see cref="DotnetApiBoilerplateWebApplicationFactory" /> (and therefore one PostgreSQL
///     container) across every integration test class, so the container is started and migrated once
///     per run rather than per class. Annotate test classes with
///     <c>[Collection(IntegrationTestCollection.Name)]</c>.
/// </summary>
[CollectionDefinition(Name)]
public sealed class IntegrationTestCollection : ICollectionFixture<DotnetApiBoilerplateWebApplicationFactory>
{
    public const string Name = "Integration";
}
