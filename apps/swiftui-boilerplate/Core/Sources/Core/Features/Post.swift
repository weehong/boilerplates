import Foundation

/// Example model backed by jsonplaceholder.typicode.com.
public struct Post: Codable, Hashable, Identifiable, Sendable {
    public let id: Int
    public let userId: Int
    public let title: String
    public let body: String

    public init(id: Int, userId: Int, title: String, body: String) {
        self.id = id
        self.userId = userId
        self.title = title
        self.body = body
    }
}

public extension Post {
    /// Fixture data for previews and tests.
    static let samples: [Post] = [
        Post(id: 1, userId: 1, title: "First sample post", body: "Body of the first sample post."),
        Post(id: 2, userId: 1, title: "Second sample post", body: "Body of the second sample post."),
        Post(id: 3, userId: 2, title: "Third sample post", body: "Body of the third sample post."),
    ]
}

public enum PostsEndpoint: APIEndpoint {
    case list
    case detail(id: Int)
    case delete(id: Int)

    public var path: String {
        switch self {
        case .list:
            "posts"
        case .detail(let id), .delete(let id):
            "posts/\(id)"
        }
    }

    public var method: HTTPMethod {
        switch self {
        case .list, .detail:
            .get
        case .delete:
            .delete
        }
    }
}
