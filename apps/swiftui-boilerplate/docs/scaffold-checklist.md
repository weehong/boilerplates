# Scaffold Checklist

The agreed execution plan for scaffolding this boilerplate, settled in a grilling session on 2026-07-31. Supersedes all earlier drafts. Vocabulary follows [`CONTEXT.md`](../CONTEXT.md); the two structural decisions are recorded in [`docs/adr/`](./adr/).

> **Status (2026-07-31)**: all items scaffolded. The Core package is verified on Linux — 17 tests in 4 suites pass under Swift 6.3.3. The SwiftUI layer (app target) and `xcodegen generate` are authored but **not yet verified on a Mac**.

## Settled decisions (summary)

| # | Decision |
| --- | --- |
| 1 | Lives at `apps/swiftui-boilerplate` in this monorepo. |
| 2 | XcodeGen `project.yml`, no committed `.xcodeproj`; non-UI logic in a local SPM Core package verified with `swift test` on Linux (ADR-0001). |
| 3 | Deployment target **iOS 26**, built with the latest SDK. Lowering the target is the cloner's job. |
| 4 | `AppRoute` is push-only; modals use separate `SheetRoute`/`CoverRoute` optionals (ADR-0002). |
| 5 | Coordinator holds `[AppRoute]`, not `NavigationPath`; lives in Core, tested on Linux. |
| 6 | View models are built at the composition root by the route→view factory, dependencies passed via `init`. The SwiftUI environment carries only the Coordinator. |
| 7 | `RequestInterceptor` is a single-method request adapter (`adapt`). Token refresh is **out of scope** — README notes the extension point and warns about single-flight. |
| 8 | One fake network: `StubNetworkService` in the Core main target, shared by tests and previews. No separate mock. |
| 9 | Cut entirely: `RetryPolicy` placeholder, `DeeplinkHandler` placeholder, global `ErrorAlertHandler`, Combine, `wait(for:timeout:)` test helper. No empty protocols — extension points live in the README as prose. |
| 10 | Error split rule: loading errors render inline via `StatefulView` (with retry); action errors present a local `.alert` owned by the screen. |
| 11 | Two example screens on one navigation chain: list (ViewState machine) → detail (`@State` + `.task`). Data from `jsonplaceholder.typicode.com`; previews and tests use `StubNetworkService`. |
| 12 | Environment switching: Debug/Staging/Release build configurations, one xcconfig each, `APP_ENVIRONMENT` injected via Info.plist, read into `AppEnvironment` at launch. |
| 13 | Tests are deterministic: `await load()` then assert the terminal state; a gated (continuation-suspended) `StubNetworkService` mode asserts `.loading`. No time-based polling anywhere. |
| 14 | Localization via String Catalog (`.xcstrings`) with `String(localized:)`; design tokens as semantic constants in `DesignSystem/` plus asset-catalog colors. |
| 15 | SwiftUI components (`StatefulView`, alert modifier) live in the app target; the Core package never imports SwiftUI. |

## Phase 1 — Project shell

- [x] `project.yml` (XcodeGen): app target, iOS 26 deployment target, Debug/Staging/Release configurations each bound to an xcconfig.
- [x] Three xcconfig files injecting `APP_ENVIRONMENT` (and `API_BASE_URL`) through Info.plist.
- [x] Local SPM package `Core/` with `Package.swift` (zero dependencies), products `Core` + test target.
- [x] App folders: `App/` (entry, composition root), `Components/`, `DesignSystem/`, `Features/`.
- [x] `Localizable.xcstrings` with example keys; asset catalog with semantic colors; `DesignSystem` token constants.
- [x] README: how to `xcodegen generate` on a Mac, how to `swift test` on Linux, recommended libraries list, extension-point notes (token refresh + single-flight warning, retry, deeplinks).

## Phase 2 — Navigation (Core)

- [x] `AppRoute` enum, push destinations only, `Hashable`.
- [x] `SheetRoute` / `CoverRoute` enums, `Identifiable`.
- [x] `Coordinator` (`@Observable`): `path: [AppRoute]`, `sheet: SheetRoute?`, `cover: CoverRoute?`, with push/pop/popToRoot/present/dismiss.
- [x] App target: `NavigationStack(path:)` binding, `navigationDestination`, `.sheet(item:)`, `.fullScreenCover(item:)`; Coordinator injected via environment.

## Phase 3 — Networking (Core)

- [x] `APIEndpoint` protocol (path, method, headers, body).
- [x] `NetworkError` enum (invalidResponse, decodingFailed, clientError, serverError, transport).
- [x] `RequestInterceptor` protocol: `adapt(URLRequest) async throws -> URLRequest`; example header + logging interceptors.
- [x] Generic `NetworkClient` over `URLSession`, applying interceptors in order.
- [x] `NetworkServiceProtocol` abstraction consumed by view models.
- [x] `StubNetworkService`: preset `Result` responses per endpoint, optional latency, gated mode (continuation-suspended until released).
- [x] `AppEnvironment` enum reading `APP_ENVIRONMENT` from Info.plist, exposing `baseURL`.

## Phase 4 — Example features (app target)

- [x] Decodable models for jsonplaceholder resources.
- [x] `ViewState<T>` enum in Core: `.idle / .loading / .loaded(T) / .error`.
- [x] `StatefulView` wrapper rendering a `ViewState` (spinner / content / inline error with retry).
- [x] List screen: `@Observable` view model with `load()`, ViewState machine, StatefulView, row tap → `coordinator.push`.
- [x] Detail screen: lightweight `@State` + `.task` pattern, no ViewState.
- [x] One action-error example presenting a local `.alert`.
- [x] Composition root: `AppDependencies`, route→view factory constructing view models.

## Phase 5 — Tests (Swift Testing, run on Linux)

- [x] Decoding suite over sample JSON payloads.
- [x] View-model suite: `await load()` → assert `.loaded` / `.error`; gated stub → assert `.loading` mid-flight.
- [x] Coordinator suite: push/pop/popToRoot/present/dismiss mutate the expected state.
- [x] NetworkClient suite: interceptors applied in order; error mapping per status code.
- [x] `@Suite` organization as the team template.
