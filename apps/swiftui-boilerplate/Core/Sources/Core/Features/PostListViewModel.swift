import Observation

/// Example view model for the ViewState pattern. Loading failures go into
/// `state` (rendered inline by StatefulView); action failures go into
/// `actionError` (presented as a local alert) — the error split rule.
@MainActor
@Observable
public final class PostListViewModel {
    public private(set) var state: ViewState<[Post]> = .idle
    public var actionError: NetworkError?

    private let network: any NetworkServiceProtocol

    public init(network: any NetworkServiceProtocol) {
        self.network = network
    }

    public func load() async {
        state = .loading
        do {
            state = .loaded(try await network.request(PostsEndpoint.list, as: [Post].self))
        } catch {
            state = .error(Self.networkError(from: error))
        }
    }

    public func delete(_ post: Post) async {
        guard case .loaded(var posts) = state else { return }
        do {
            _ = try await network.request(PostsEndpoint.delete(id: post.id), as: EmptyResponse.self)
            posts.removeAll { $0.id == post.id }
            state = .loaded(posts)
        } catch {
            actionError = Self.networkError(from: error)
        }
    }

    private static func networkError(from error: any Error) -> NetworkError {
        error as? NetworkError ?? .transport(description: String(describing: error))
    }
}
