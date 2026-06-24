# CLAUDE.md

This repository is a monorepo for reusable boilerplates.

## Repository Layout

- `apps/vite-react-boilerplate`: imported from `https://github.com/weehong/vite-react-boilerplate`
- `apps/nextjs-boilerplate`: imported from `https://github.com/weehong/nextjs-boilerplate`
- `apps/dotnet-api-boilerplate`: adapted from `https://github.com/upmatches/api_v2/commit/7b18471d65c8526311a144a44cb4ab61d1eaab0b`

## Maintenance Rules

- Keep each boilerplate self-contained inside its app directory.
- Preserve each boilerplate's local package manager files, README, scaffold notes, and configuration.
- Update the root `README.md` whenever a boilerplate is added, removed, renamed, or materially changed.
- Prefer import commit messages that match the intent of the original boilerplate commit.

## Git Remote

```sh
git remote add origin git@github.com:weehong/boilerplates.git
```
