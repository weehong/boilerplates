using Microsoft.AspNetCore.Mvc;

namespace DotnetApiBoilerplate.Api.Controllers;

/// <summary>
///     Base class for all API controllers. Carries the shared presentation conventions —
///     <see cref="ApiControllerAttribute" /> behavior (automatic model validation, binding-source
///     inference) and the versioned <c>/api/v1/{controller}</c> route — so derived controllers stay
///     thin and only translate HTTP ⇄ use cases.
///     <para>
///         No <c>[Produces]</c> is applied: success responses serialize as <c>application/json</c>
///         by default, while failures keep the RFC 7807 <c>application/problem+json</c> content type
///         set by <see cref="Extensions.ResultExtensions.ToProblem" /> (a <c>[Produces]</c> filter
///         would override it).
///     </para>
/// </summary>
[ApiController]
[Route("api/v1/[controller]")]
public abstract class ApiControllerBase : ControllerBase;
