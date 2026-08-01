# SwiftUI Boilerplate

Reusable starting point for a modern iOS app: SwiftUI, Swift Concurrency, `@Observable`, and Swift Testing, with all non-UI logic in a local SPM package that builds and tests on Linux.

## Navigation

**AppRoute**:
A push destination on the navigation stack. Contains only destinations that are pushed — never modals.
_Avoid_: screen, page, deeplink

**SheetRoute**:
A modal destination presented as a sheet. Held as an optional, independent of the push stack.

**CoverRoute**:
A modal destination presented as a full-screen cover. Held as an optional, independent of the push stack.

**Coordinator**:
The single observable owner of all navigation state: the route stack plus the active modal routes.
_Avoid_: router, navigator

## Networking

**RequestInterceptor**:
An adapter that mutates an outbound request before it is sent (headers, logging). It cannot observe responses.
_Avoid_: middleware

**StubNetworkService**:
The one fake network implementation, configured with preset responses. Shared by tests and previews. It only returns what it is told — it records nothing and asserts nothing, hence *stub*, not *mock*.
_Avoid_: mock, MockNetworkClient

## Screen state

**ViewState**:
The state machine a data-backed screen moves through: idle, loading, loaded, or error. Used by screens with non-trivial loading behaviour; simple screens may skip it.
_Avoid_: phase, UI state

**StatefulView**:
The wrapper view that renders a ViewState — spinner while loading, content when loaded, inline error with retry on failure.

**Loading error**:
A failure while fetching what a screen exists to show. Always rendered inline by StatefulView, never as an alert.

**Action error**:
A failure of a user-initiated operation (submit, delete). Always presented as a local alert owned by the screen that triggered it. There is no global error broadcaster.

## Composition

**AppDependencies**:
The container holding every service the app needs, living only at the composition root.
_Avoid_: DIContainer, service locator

**Composition root**:
The app entry point where AppDependencies is built and routes are resolved into views. View models are constructed here and handed their dependencies explicitly; the SwiftUI environment carries only the Coordinator.

**AppEnvironment**:
The deployment environment (development, staging, production) the build is pointed at, selected by build configuration.
