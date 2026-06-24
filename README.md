# Boilerplates

Monorepo for reusable application boilerplates.

## Apps

| Boilerplate | Path | Source | Notes |
| --- | --- | --- | --- |
| Vite React Boilerplate | `apps/vite-react-boilerplate` | <https://github.com/weehong/vite-react-boilerplate> | Vite, React, TypeScript, Storybook, Playwright, Vitest, Tailwind CSS, and pnpm workspace setup. |
| Next.js Boilerplate | `apps/nextjs-boilerplate` | <https://github.com/weehong/nextjs-boilerplate> | Production Next.js boilerplate with TypeScript, Storybook, Playwright, Vitest, structured metadata, and npm lockfile. |

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

## Import History

- `chore: initialize repository` created the empty repository with the GitHub remote.
- `feat: add vite react app scaffold` imported `weehong/vite-react-boilerplate`.
- `feat: add production nextjs boilerplate` imported `weehong/nextjs-boilerplate`.
