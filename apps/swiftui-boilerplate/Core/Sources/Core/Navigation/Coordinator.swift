import Observation

/// The single owner of all navigation state: the typed route stack plus the
/// active modal routes. The view layer binds `path` to `NavigationStack(path:)`
/// and the modal optionals to `.sheet(item:)` / `.fullScreenCover(item:)`.
@MainActor
@Observable
public final class Coordinator {
    public var path: [AppRoute] = []
    public var sheet: SheetRoute?
    public var cover: CoverRoute?

    public init() {}

    public func push(_ route: AppRoute) {
        path.append(route)
    }

    public func pop() {
        _ = path.popLast()
    }

    public func popToRoot() {
        path.removeAll()
    }

    public func present(_ route: SheetRoute) {
        sheet = route
    }

    public func present(_ route: CoverRoute) {
        cover = route
    }

    public func dismissSheet() {
        sheet = nil
    }

    public func dismissCover() {
        cover = nil
    }
}
