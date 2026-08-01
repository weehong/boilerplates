/// The abstraction view models consume. This is the codebase's single test
/// seam: tests and previews substitute `StubNetworkService`, everything else is
/// exercised through its public API.
public protocol NetworkServiceProtocol: Sendable {
    func request<Response: Decodable & Sendable>(
        _ endpoint: any APIEndpoint,
        as type: Response.Type
    ) async throws -> Response
}

public extension NetworkServiceProtocol {
    func request<Response: Decodable & Sendable>(_ endpoint: any APIEndpoint) async throws -> Response {
        try await request(endpoint, as: Response.self)
    }
}

/// Decodes successfully from any JSON object — for endpoints whose response
/// body carries no information (e.g. DELETE).
public struct EmptyResponse: Decodable, Sendable {
    public init() {}
}
