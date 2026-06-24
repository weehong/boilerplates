using DotnetApiBoilerplate.Api.Extensions;
using DotnetApiBoilerplate.Application;
using DotnetApiBoilerplate.Infrastructure;
using Scalar.AspNetCore;

WebApplicationBuilder builder = WebApplication.CreateBuilder(args);

// Compose the layers. Each AddXxx lives next to the code it registers.
builder.Services.AddApplication();
builder.Services.AddInfrastructure(builder.Configuration);
builder.Services.AddApi();

WebApplication app = builder.Build();

// Exception handling first so it wraps everything downstream.
app.UseExceptionHandler();

if (app.Environment.IsDevelopment())
{
    // OpenAPI document at /openapi/v1.json, interactive UI at /scalar/v1.
    app.MapOpenApi();
    app.MapScalarApiReference();
}

app.UseHttpsRedirection();

// Liveness/readiness probe. The DbContext health check is registered in Infrastructure.
app.MapHealthChecks("/health");

app.MapControllers();

await app.RunAsync();

// The integration test project reaches the generated Program entry point via InternalsVisibleTo
// (configured in the Api project file); .NET 10 no longer needs an explicit public partial marker.
