# Domain Docs

How the engineering skills should consume this repo's domain documentation when exploring the codebase.

This repo is **multi-context**: each boilerplate under `apps/` is its own context with its own stack, vocabulary, and decisions.

## Before exploring, read these

- **`CONTEXT-MAP.md`** at the repo root — it points at one `CONTEXT.md` per context. Read each one relevant to the topic.
- **`apps/<name>/CONTEXT.md`** — the glossary for the boilerplate you're working in.
- **`docs/adr/`** at the repo root — decisions that apply across every boilerplate (repo layout, import conventions, shared tooling policy).
- **`apps/<name>/docs/adr/`** — decisions scoped to a single boilerplate.
- **`apps/<name>/CLAUDE.md`** — each boilerplate already ships its own instructions file; read it alongside the context docs.

If any of these files don't exist, **proceed silently**. Don't flag their absence; don't suggest creating them upfront. The `/domain-modeling` skill (reached via `/grill-with-docs` and `/improve-codebase-architecture`) creates them lazily when terms or decisions actually get resolved.

## File structure

```
/
├── CONTEXT-MAP.md                      ← points at each context below
├── CLAUDE.md
├── docs/
│   ├── agents/                         ← this file, issue-tracker.md, triage-labels.md
│   └── adr/                            ← repo-wide decisions
└── apps/
    ├── vite-react-boilerplate/
    │   ├── CONTEXT.md
    │   └── docs/adr/                   ← context-specific decisions
    ├── nextjs-boilerplate/
    │   ├── CONTEXT.md
    │   └── docs/adr/
    ├── express-api-boilerplate/
    │   ├── CONTEXT.md
    │   └── docs/adr/
    ├── dotnet-api-boilerplate/
    │   ├── CONTEXT.md
    │   └── docs/adr/
    └── spring-boot-boilerplate/
        ├── CONTEXT.md
        └── docs/adr/
```

Note the deviation from the skills' default multi-context template: contexts live under `apps/`, not `src/`, because each one is a self-contained boilerplate rather than a module of a single application.

## Stay inside one context

A change to one boilerplate should not reach into another. If work appears to span two boilerplates, that's either a genuinely repo-wide concern (record it as a root ADR) or a sign the change is scoped wrong — surface it rather than editing both silently.

## Use the glossary's vocabulary

When your output names a domain concept (in an issue title, a refactor proposal, a hypothesis, a test name), use the term as defined in that context's `CONTEXT.md`. Don't drift to synonyms the glossary explicitly avoids.

If the concept you need isn't in the glossary yet, that's a signal — either you're inventing language the project doesn't use (reconsider) or there's a real gap (note it for `/domain-modeling`).

## Flag ADR conflicts

If your output contradicts an existing ADR, surface it explicitly rather than silently overriding:

> _Contradicts ADR-0007 (event-sourced orders) — but worth reopening because…_
