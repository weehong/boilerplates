# XcodeGen project plus a Linux-testable Core package

This boilerplate is developed and verified on a Linux machine with no Xcode. We therefore commit an XcodeGen `project.yml` instead of a `.xcodeproj` (hand-authoring pbxproj blind is unmaintainable; a Mac regenerates the project with one command), and we split all non-UI logic — networking, routes, Coordinator, ViewState, StubNetworkService — into a local SPM package whose tests run with `swift test` on Linux. SwiftUI code lives only in the app target and is verified on a Mac.

## Consequences

- The `Package.swift` is the Core package's real manifest, not a placeholder; the package keeps zero third-party dependencies.
- Nothing in the Core package may `import SwiftUI` (or any Apple-only framework); reusable SwiftUI components belong to the app target.
- The dividing line for "where does this type go" is: can it compile on Linux?

## Considered options

- Hand-written `.xcodeproj` — rejected: unwritable and unreviewable without Xcode.
- Tuist — rejected: heavier tooling than a zero-dependency template warrants.
- Pure SPM package with no app shell — rejected: the deliverable is a runnable app template, matching the other boilerplates in this repo.
