import Foundation

public enum HTTPMethod: String, Sendable {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case patch = "PATCH"
    case delete = "DELETE"
}

/// A declarative description of one API call. `path` is relative to the
/// client's base URL.
public protocol APIEndpoint: Sendable {
    var path: String { get }
    var method: HTTPMethod { get }
    var headers: [String: String] { get }
    var body: Data? { get }
}

public extension APIEndpoint {
    var method: HTTPMethod { .get }
    var headers: [String: String] { [:] }
    var body: Data? { nil }
}
