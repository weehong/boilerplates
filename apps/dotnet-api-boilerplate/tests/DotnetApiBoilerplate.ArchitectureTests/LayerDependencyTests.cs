using System.Reflection;
using DotnetApiBoilerplate.Api.Controllers;
using DotnetApiBoilerplate.Domain.Common;
using Shouldly;
using ApplicationLayer = DotnetApiBoilerplate.Application.DependencyInjection;
using InfrastructureLayer = DotnetApiBoilerplate.Infrastructure.DependencyInjection;

namespace DotnetApiBoilerplate.ArchitectureTests;

/// <summary>
///     Enforces the Clean Architecture dependency rules from CLAUDE.md, so a wrong reference fails
///     the build pipeline instead of relying on review. Checks the assemblies each layer actually
///     uses at compile time; a package or project reference that no code uses does not count.
/// </summary>
public sealed class LayerDependencyTests
{
    private static readonly Assembly s_domain = typeof(Entity).Assembly;
    private static readonly Assembly s_application = typeof(ApplicationLayer).Assembly;
    private static readonly Assembly s_infrastructure = typeof(InfrastructureLayer).Assembly;
    private static readonly Assembly s_api = typeof(ApiControllerBase).Assembly;

    private static readonly string[] s_persistenceAndWeb =
    [
        "Microsoft.EntityFrameworkCore",
        "Npgsql",
        "Microsoft.AspNetCore"
    ];

    [Fact]
    public void Domain_ShouldNotDependOnOtherLayersOrFrameworks() =>
        ShouldNotReference(
            s_domain,
            [
                SimpleName(s_application),
                SimpleName(s_infrastructure),
                SimpleName(s_api),
                "MediatR",
                "FluentValidation",
                .. s_persistenceAndWeb
            ]);

    [Fact]
    public void Application_ShouldDependOnDomainOnly() =>
        ShouldNotReference(
            s_application,
            [SimpleName(s_infrastructure), SimpleName(s_api), .. s_persistenceAndWeb]);

    [Fact]
    public void Infrastructure_ShouldNotDependOnApi() =>
        ShouldNotReference(s_infrastructure, [SimpleName(s_api)]);

    [Fact]
    public void DomainTypes_ShouldNotExposeMutableCollections()
    {
        string[] offenders = s_domain
            .GetTypes()
            .SelectMany(type => type
                .GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly)
                .Where(property => IsMutableCollection(property.PropertyType))
                .Select(property => $"{type.FullName}.{property.Name}"))
            .ToArray();

        offenders.ShouldBeEmpty(
            "Expose collections as IReadOnlyCollection<T> or IReadOnlyList<T> backed by a private field.");
    }

    private static void ShouldNotReference(Assembly assembly, string[] forbiddenPrefixes)
    {
        string[] violations = assembly
            .GetReferencedAssemblies()
            .Select(reference => reference.Name ?? string.Empty)
            .Where(name => forbiddenPrefixes.Any(prefix => name.StartsWith(prefix, StringComparison.Ordinal)))
            .ToArray();

        violations.ShouldBeEmpty($"{SimpleName(assembly)} must not reference these assemblies.");
    }

    private static string SimpleName(Assembly assembly) => assembly.GetName().Name!;

    private static bool IsMutableCollection(Type type)
    {
        // byte[] is allowed: it is the usual type for binary values such as a row version.
        if (type.IsArray)
        {
            return type != typeof(byte[]);
        }

        if (!type.IsGenericType)
        {
            return false;
        }

        Type definition = type.GetGenericTypeDefinition();

        return definition == typeof(List<>)
               || definition == typeof(IList<>)
               || definition == typeof(ICollection<>)
               || definition == typeof(HashSet<>)
               || definition == typeof(ISet<>)
               || definition == typeof(Dictionary<,>)
               || definition == typeof(IDictionary<,>);
    }
}
