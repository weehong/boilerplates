/// The state machine a data-backed screen moves through. Screens with
/// non-trivial loading behaviour drive a `StatefulView` with this; trivial
/// screens may use plain `@State` + `.task` instead (both patterns are
/// demonstrated in the example features).
public enum ViewState<Value: Sendable>: Sendable {
    case idle
    case loading
    case loaded(Value)
    case error(NetworkError)
}

public extension ViewState {
    var value: Value? {
        if case .loaded(let value) = self { value } else { nil }
    }

    var isLoading: Bool {
        if case .loading = self { true } else { false }
    }

    var error: NetworkError? {
        if case .error(let error) = self { error } else { nil }
    }
}

extension ViewState: Equatable where Value: Equatable {}
