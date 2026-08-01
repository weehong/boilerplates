# SwiftUI Boilerplate

A reusable starting point for a modern iOS app: SwiftUI, Swift Concurrency, `@Observable`, and Swift Testing — with **zero third-party dependencies** and all non-UI logic in a local SPM package that builds and tests on Linux.

Vocabulary lives in [`CONTEXT.md`](./CONTEXT.md); the two structural decisions in [`docs/adr/`](./docs/adr/); the scaffold plan in [`docs/scaffold-checklist.md`](./docs/scaffold-checklist.md).

## Requirements

- **Mac**: Xcode 26+, [XcodeGen](https://github.com/yonaskolb/XcodeGen) (`brew install xcodegen`). Deployment target is iOS 26 — lowering it is your job (the `@Observable` floor is iOS 17).
- **Linux** (Core package only): a Swift 6.3+ toolchain from [swift.org](https://www.swift.org/install/linux/). On distros shipping `libxml2.so.16` (Ubuntu 26.04+), shim the toolchain's `libxml2.so.2` expectation: `ln -s /usr/lib/x86_64-linux-gnu/libxml2.so.16 <dir>/libxml2.so.2` and run with `LD_LIBRARY_PATH=<dir>`.

## Quickstart

```sh
# Mac — generate and open the Xcode project (no .xcodeproj is committed)
xcodegen generate
open SwiftUIBoilerplate.xcodeproj

# Any platform — run the Core test suite
cd Core && swift test
```

The app runs against `jsonplaceholder.typicode.com` out of the box; previews and tests run offline against `StubNetworkService`.

## Layout

| Path | What |
| --- | --- |
| `Core/` | SPM package: navigation state, networking, ViewState, StubNetworkService, example view model — everything that compiles on Linux. Never imports SwiftUI. |
| `App/` | Entry point and composition root (`AppDependencies`, route→view factory in `RootView`). |
| `Components/` | Reusable SwiftUI: `StatefulView`, `actionErrorAlert`. |
| `DesignSystem/` | Semantic tokens (colors, typography, spacing). |
| `Features/` | Example screens. |
| `Configs/` | One xcconfig per build configuration (Debug / Staging / Release). |
| `project.yml` | XcodeGen manifest — the source of truth for the Xcode project. |

## Architecture in five sentences

1. **Navigation**: `Coordinator` (`@Observable`) owns a typed `[AppRoute]` push stack plus optional `SheetRoute`/`CoverRoute` modals; views bind them to `NavigationStack(path:)` / `.sheet(item:)` / `.fullScreenCover(item:)` (ADR-0002).
2. **DI**: dependencies are built once in `AppDependencies.live()` and passed into screens explicitly through initializers at the composition root; the SwiftUI environment carries only the Coordinator.
3. **Networking**: `NetworkClient` builds requests from `APIEndpoint`s, runs them through `RequestInterceptor`s in order, and maps failures into `NetworkError`; view models consume the `NetworkServiceProtocol` abstraction — the codebase's single test seam.
4. **Screen state**: complex screens drive `StatefulView` with the `ViewState` machine (`PostListView`); trivial screens use plain `@State` + `.task` (`PostDetailView`) — pick per screen.
5. **Errors**: loading errors render inline via `StatefulView` with retry; action errors present a local `.alert` via `actionErrorAlert` — there is no global error broadcaster.

## Testing

Deterministic by construction — no `wait(for:timeout:)`, no polling, no clocks:

- Terminal states: `await viewModel.load()`, then assert `.loaded` / `.error`.
- The `.loading` intermediate state: `gate()` the stub, start `load()` in a `Task`, `await stub.waitUntilRequested()`, assert, `release()`.
- Navigation: `Coordinator` is plain state — push/pop/present/dismiss are asserted directly.

Suites live in `Core/Tests` and are the team's template (`@Suite` per unit under test).

## Environment switching

Three build configurations, each bound to an xcconfig that injects `APP_ENVIRONMENT` and `API_BASE_URL` through Info.plist; `AppConfiguration.load(from:)` reads them at launch. Note the `https:/$()/…` trick in the xcconfigs — a literal `//` would start a comment.

## Extension points (deliberately not built)

- **Token refresh**: interceptors are request-side only, by design. Refresh needs response-side handling — catch the 401 in `NetworkClient`, refresh, replay. **Warning**: make the refresh single-flight (one refresh task shared by concurrent 401s), or parallel requests will stampede your auth server.
- **Retry**: default behaviour is no retry. If you need it, wrap the perform step in `NetworkClient` with an attempt loop — and think through idempotency before retrying non-GETs.
- **Deeplinks**: parse the URL into an `AppRoute` (pure, Linux-testable), then have `onOpenURL` hand it to the Coordinator.

## Recommended libraries (add only when needed)

None are included. Commonly reached for: [Kingfisher](https://github.com/onevcat/Kingfisher) (image loading), [swift-dependencies](https://github.com/pointfreeco/swift-dependencies) (if AppDependencies outgrows manual wiring), [swift-snapshot-testing](https://github.com/pointfreeco/swift-snapshot-testing) (UI regression).
