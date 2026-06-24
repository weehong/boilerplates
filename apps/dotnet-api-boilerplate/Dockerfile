# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

# Copy solution metadata first for better layer caching.
COPY global.json Directory.Build.props Directory.Packages.props ./
COPY DotnetApiBoilerplate.slnx ./
COPY src/ ./src/

RUN dotnet restore src/DotnetApiBoilerplate.Api/DotnetApiBoilerplate.Api.csproj
RUN dotnet publish src/DotnetApiBoilerplate.Api/DotnetApiBoilerplate.Api.csproj \
    -c Release -o /app/publish --no-restore /p:UseAppHost=false

# ---- Runtime stage ----
FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS final
WORKDIR /app

# curl is needed for the container HEALTHCHECK below; install it as root before
# dropping privileges.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Run as the non-root user shipped with the base image.
USER $APP_UID

COPY --from=build /app/publish ./
ENV ASPNETCORE_HTTP_PORTS=8080
EXPOSE 8080

# Liveness/readiness probe. HttpsRedirection is skipped when no HTTPS port is
# configured (the container is HTTP-only), so /health answers over plain HTTP.
HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=5 \
    CMD curl -fsS http://localhost:8080/health || exit 1

ENTRYPOINT ["dotnet", "DotnetApiBoilerplate.Api.dll"]
