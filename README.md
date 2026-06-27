# Boilerplates

Monorepo for reusable application boilerplates.

## Apps

| Boilerplate | Path | Source | Notes |
| --- | --- | --- | --- |
| Vite React Boilerplate | `apps/vite-react-boilerplate` | <https://github.com/weehong/vite-react-boilerplate> | Vite, React, TypeScript, Storybook, Playwright, Vitest, Tailwind CSS, and pnpm workspace setup. |
| Next.js Boilerplate | `apps/nextjs-boilerplate` | <https://github.com/weehong/nextjs-boilerplate> | Production Next.js boilerplate with TypeScript, Storybook, Playwright, Vitest, structured metadata, and npm lockfile. |
| Express API Boilerplate | `apps/express-api-boilerplate` | Authored in-repo | Production Express 5 + TypeScript API with Prisma/PostgreSQL, pino, zod, OpenAPI/Swagger, Vitest + supertest, Playwright, and Docker. |
| .NET API Boilerplate | `apps/dotnet-api-boilerplate` | <https://github.com/upmatches/api_v2/commit/7b18471d65c8526311a144a44cb4ab61d1eaab0b> | Lightweight .NET 10 Clean Architecture API with EF Core PostgreSQL, Result handling, OpenAPI, Docker, and tests. |
| Spring Boot Boilerplate | `apps/spring-boot-boilerplate` | Local `upmatches-dev/upmatches` source | Spring Boot 4 API with Java 25, WebMVC, security, JPA/PostgreSQL, Flyway, Redis cache support, OpenAPI, Docker, and tests. |

## Working With A Boilerplate

Each boilerplate remains self-contained in its own directory. Use the package manager and setup instructions from the boilerplate's local README.

```sh
cd apps/vite-react-boilerplate
pnpm install
pnpm dev
```

```sh
cd apps/nextjs-boilerplate
npm install
npm run dev
```

```sh
cd apps/express-api-boilerplate
npm install
npm run dev
```

```sh
cd apps/dotnet-api-boilerplate
dotnet restore DotnetApiBoilerplate.slnx
dotnet run --project src/DotnetApiBoilerplate.Api
```

```sh
cd apps/spring-boot-boilerplate
./mvnw verify
./mvnw spring-boot:run
```

## Import History

- `chore(monorepo): initialize repository` created the empty repository with the GitHub remote.
- `feat(vite-react): add boilerplate` imported `weehong/vite-react-boilerplate`.
- `feat(nextjs): add boilerplate` imported `weehong/nextjs-boilerplate`.
- `feat(express-api): add boilerplate` scaffolded a new production Express 5 + TypeScript API boilerplate in-repo.
- `feat(dotnet-api): add boilerplate` imported and trimmed `upmatches/api_v2` commit `7b18471d65c8526311a144a44cb4ab61d1eaab0b`.
- `feat(spring-boot): add boilerplate` imported and trimmed the local `upmatches-dev/upmatches` Spring Boot project.
