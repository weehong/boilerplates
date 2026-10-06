# Boilerplates

Starter templates for web frontends and backend APIs, kept in one repository.

Each boilerplate under `apps/` is a complete, standalone project with its own
toolchain, lockfile, Dockerfile, tests, and README. Nothing is shared at the
root: there is no root `package.json`, workspace, or build. Pick one, copy it
out, and build your project on it.

## Boilerplates

| Boilerplate | What you get | Requires | Dev URL |
| --- | --- | --- | --- |
| [`vite-react-boilerplate`](apps/vite-react-boilerplate) | React 19 single-page app on Vite 7 with TanStack Router, Query, and Table, Tailwind CSS 4, Zustand, React Hook Form + Zod, i18next, Nivo charts, and Storybook | Node.js 20.19+, pnpm | <http://localhost:5173> |
| [`nextjs-boilerplate`](apps/nextjs-boilerplate) | Next.js 16 App Router app with React 19, Tailwind CSS 4, TanStack Query, Storybook, and SEO defaults (metadata routes, sitemap, robots, JSON-LD, social images) | Node.js 22+, npm | <http://localhost:3000> |
| [`express-api-boilerplate`](apps/express-api-boilerplate) | Express 5 + TypeScript API with Prisma/PostgreSQL, zod validation, pino logging, helmet/CORS/rate limiting, and OpenAPI with Swagger UI | Node.js 22+, npm, Docker | <http://localhost:3000> |
| [`dotnet-api-boilerplate`](apps/dotnet-api-boilerplate) | .NET 10 Clean Architecture API with CQRS (MediatR), FluentValidation, EF Core/PostgreSQL, Result-based errors, ProblemDetails, and OpenAPI with Scalar | .NET SDK 10.0.300+, Docker | <http://localhost:5080> |
| [`spring-boot-boilerplate`](apps/spring-boot-boilerplate) | Spring Boot 4 API on Java 25 with WebMVC, stateless Spring Security, JPA/PostgreSQL, Flyway, optional Redis cache, Actuator, tracing, and springdoc OpenAPI | Docker (JDK 25 for local builds; Maven wrapper included) | <http://localhost:8080> |

Every boilerplate is domain-neutral. The sample code (health checks in the APIs,
an isolated example demo in Vite React) shows how the pieces connect and is
meant to be replaced by your first real feature.

## Start a New Project

Copy the boilerplate you want out of this repository and give it its own Git
history:

```sh
git clone --depth 1 git@github.com:weehong/boilerplates.git
cp -r boilerplates/apps/nextjs-boilerplate my-app
cd my-app
git init
```

Then follow the boilerplate's own README. The Node.js boilerplates have a
`setup` script that installs the Git hooks and Playwright browsers.

Git hooks (Husky) and the bundled GitHub Actions workflows (`.github/` in the
.NET and Spring Boot boilerplates) only take effect when the boilerplate sits at
the root of its own repository. Inside this monorepo they are inactive.

## Run a Boilerplate in Place

To try a boilerplate without copying it, run it from its directory. The
boilerplate's README covers environment variables, tests, Docker builds, and
deployment.

**Vite React**

```sh
cd apps/vite-react-boilerplate
pnpm install
pnpm dev
```

**Next.js**

```sh
cd apps/nextjs-boilerplate
npm install
npm run dev
```

**Express API**: needs PostgreSQL, which `docker compose` provides.

```sh
cd apps/express-api-boilerplate
npm install
cp .env.example .env
docker compose up -d db
npm run db:migrate
npm run dev              # Swagger UI at /docs
```

**.NET API**: needs PostgreSQL, which `docker compose` provides.

```sh
cd apps/dotnet-api-boilerplate
docker compose up -d db
export ConnectionStrings__Default="Host=localhost;Port=5432;Database=dotnet_api_boilerplate;Username=dotnet_api_boilerplate;Password=dotnet_api_boilerplate"
dotnet run --project src/DotnetApiBoilerplate.Api   # Scalar UI at /scalar/v1
```

**Spring Boot**: tests run on in-memory H2. `make up` starts the API with
PostgreSQL and Redis in Docker.

```sh
cd apps/spring-boot-boilerplate
./mvnw verify
make up                  # actuator health at /actuator/health
```

## Shared Conventions

The boilerplates differ by ecosystem but follow the same standards where they
overlap:

- **Strict by default.** Strict TypeScript and ESLint in the Node.js
  boilerplates, and warnings as errors with code-style enforcement in the .NET
  build.
- **Formatting.** Prettier with tabs in the Node.js boilerplates;
  `dotnet format` and `.editorconfig` in .NET; Checkstyle and `.editorconfig` in
  Spring Boot.
- **Conventional Commits.** The Node.js boilerplates enforce them with Husky,
  Commitlint, and Commitizen.
- **Tests at every level.** Vitest and Playwright for the Node.js boilerplates,
  plus supertest for the Express API. xUnit with Testcontainers for .NET. JUnit
  and Spring Boot Test for Spring Boot.
- **Containers.** Every boilerplate has a Dockerfile. The APIs also ship a
  Compose file with PostgreSQL (and Redis for Spring Boot).
- **Reproducible.** The Node.js boilerplates include a `SCAFFOLD.md` prompt
  that lets an AI coding agent recreate the boilerplate from scratch.

## Sources

| Boilerplate | Origin |
| --- | --- |
| `vite-react-boilerplate` | Imported from [weehong/vite-react-boilerplate](https://github.com/weehong/vite-react-boilerplate) |
| `nextjs-boilerplate` | Imported from [weehong/nextjs-boilerplate](https://github.com/weehong/nextjs-boilerplate) |
| `express-api-boilerplate` | Written in this repository |
| `dotnet-api-boilerplate` | Adapted from [upmatches/api_v2@7b18471](https://github.com/upmatches/api_v2/commit/7b18471d65c8526311a144a44cb4ab61d1eaab0b), then renamed and trimmed |
| `spring-boot-boilerplate` | Adapted from the local `upmatches-dev/upmatches` Spring Boot project, then renamed and trimmed |

## Contributing

When you add, remove, rename, or materially change a boilerplate:

1. Keep it self-contained in `apps/<name>-boilerplate`, with its own package
   manager files, README, scaffold notes, and configuration.
2. Update the [Boilerplates](#boilerplates) and [Sources](#sources) tables in
   this README.
3. Commit with a Conventional Commit scoped to the boilerplate, such as
   `feat(nextjs): add boilerplate`.
