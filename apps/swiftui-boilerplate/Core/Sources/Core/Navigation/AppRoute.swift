/// A push destination on the navigation stack. Push destinations only — modals
/// live in `SheetRoute` and `CoverRoute` (see ADR-0002).
public enum AppRoute: Hashable, Sendable {
    case postDetail(postID: Int)
}

/// A modal destination presented as a sheet, held independently of the push stack.
public enum SheetRoute: Hashable, Identifiable, Sendable {
    case about

    public var id: Self { self }
}

/// A modal destination presented as a full-screen cover, held independently of the push stack.
public enum CoverRoute: Hashable, Identifiable, Sendable {
    case welcome

    public var id: Self { self }
}
