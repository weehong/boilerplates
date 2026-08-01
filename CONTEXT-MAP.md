# Context Map

Each boilerplate under `apps/` is its own bounded context with its own stack, vocabulary, and decisions. Contexts are independent — no runtime or type-level relationships exist between them. Glossaries are created lazily; contexts without one yet are listed for completeness.

## Contexts

- [SwiftUI Boilerplate](./apps/swiftui-boilerplate/CONTEXT.md) — modern iOS app template (SwiftUI, Swift Concurrency, Swift Testing)
- Vite React Boilerplate (`apps/vite-react-boilerplate`) — no CONTEXT.md yet
- Next.js Boilerplate (`apps/nextjs-boilerplate`) — no CONTEXT.md yet
- Express API Boilerplate (`apps/express-api-boilerplate`) — no CONTEXT.md yet
- .NET API Boilerplate (`apps/dotnet-api-boilerplate`) — no CONTEXT.md yet
- Spring Boot Boilerplate (`apps/spring-boot-boilerplate`) — no CONTEXT.md yet

## Relationships

None. A change to one boilerplate must not reach into another; repo-wide concerns are recorded as root ADRs in `docs/adr/`.
