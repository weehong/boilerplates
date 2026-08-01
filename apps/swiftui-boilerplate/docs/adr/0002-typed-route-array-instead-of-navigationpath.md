# Typed route array instead of NavigationPath

The Coordinator holds a typed `[AppRoute]` array (plus optional `SheetRoute`/`CoverRoute` for modals) rather than SwiftUI's `NavigationPath`, and `AppRoute` contains push destinations only. `NavigationStack(path:)` accepts a typed collection directly, so nothing is lost at the view layer.

Two reasons. First, `NavigationPath` is a SwiftUI type, which would drag the Coordinator out of the Linux-testable Core package (see ADR-0001) and make navigation tests Mac-only; it is also type-erased, so tests could assert little beyond `count`. Second, push and modal presentation are separate SwiftUI mechanisms — a single enum mixing push, sheet, and cover cases would let the type system express meaningless states (a sheet case pushed onto the stack). Separate types make those states unrepresentable.

## Consequences

- Navigation logic (push/pop/present/dismiss) is plain observable state, fully unit-tested on Linux.
- Adding a new modal means adding a case to `SheetRoute`/`CoverRoute`, not `AppRoute`.
