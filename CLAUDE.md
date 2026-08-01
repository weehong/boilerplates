# CLAUDE.md

This repository is a monorepo for reusable boilerplates.

## Repository Layout

- `apps/vite-react-boilerplate`: imported from `https://github.com/weehong/vite-react-boilerplate`
- `apps/nextjs-boilerplate`: imported from `https://github.com/weehong/nextjs-boilerplate`
- `apps/express-api-boilerplate`: scaffolded fresh in-repo as a production Express 5 + TypeScript API (Prisma/PostgreSQL, pino, zod, OpenAPI)
- `apps/dotnet-api-boilerplate`: adapted from `https://github.com/upmatches/api_v2/commit/7b18471d65c8526311a144a44cb4ab61d1eaab0b`
- `apps/spring-boot-boilerplate`: adapted from the local `upmatches-dev/upmatches` Spring Boot project
- `apps/swiftui-boilerplate`: authored in-repo as a modern iOS app template (SwiftUI, Swift Concurrency, Swift Testing); scaffold plan in `apps/swiftui-boilerplate/docs/scaffold-checklist.md`

## Maintenance Rules

- Keep each boilerplate self-contained inside its app directory.
- Preserve each boilerplate's local package manager files, README, scaffold notes, and configuration.
- Update the root `README.md` whenever a boilerplate is added, removed, renamed, or materially changed.
- Prefer import commit messages that match the intent of the original boilerplate commit.

## Git Remote

```sh
git remote add origin git@github.com:weehong/boilerplates.git
```

## Agent skills

### Issue tracker

Issues live in Linear, driven through the GraphQL API with `curl`; no CLI or MCP server is wired up. Numbering is per team, so the target team is chosen by repo path — `apps/spring-boot-boilerplate` → `SBB`. See `docs/agents/issue-tracker.md`.

### Triage labels

The five canonical triage roles, each label string equal to its name. See `docs/agents/triage-labels.md`.

### Domain docs

Multi-context — a root `CONTEXT-MAP.md` pointing at one `CONTEXT.md` per boilerplate under `apps/`. See `docs/agents/domain.md`.
