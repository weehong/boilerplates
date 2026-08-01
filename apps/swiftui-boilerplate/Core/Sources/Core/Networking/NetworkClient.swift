import Foundation
#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

/// The real `NetworkServiceProtocol` implementation: builds a `URLRequest` from
/// an `APIEndpoint`, runs it through the interceptors in order, performs it on
/// a `URLSession`, and maps failures into `NetworkError`.
public struct NetworkClient: NetworkServiceProtocol {
    private let baseURL: URL
    private let session: URLSession
    private let interceptors: [any RequestInterceptor]
    private let decoder: JSONDecoder

    public init(
        baseURL: URL,
        session: URLSession = .shared,
        interceptors: [any RequestInterceptor] = [],
        decoder: JSONDecoder = JSONDecoder()
    ) {
        self.baseURL = baseURL
        self.session = session
        self.interceptors = interceptors
        self.decoder = decoder
    }

    public func request<Response: Decodable & Sendable>(
        _ endpoint: any APIEndpoint,
        as type: Response.Type
    ) async throws -> Response {
        let request = try await adaptedRequest(for: endpoint)

        let data: Data
        let response: URLResponse
        do {
            (data, response) = try await session.data(for: request)
        } catch {
            throw NetworkError.transport(description: String(describing: error))
        }

        guard let http = response as? HTTPURLResponse else {
            throw NetworkError.invalidResponse
        }
        try Self.validate(statusCode: http.statusCode)

        do {
            return try decoder.decode(Response.self, from: data)
        } catch {
            throw NetworkError.decodingFailed(description: String(describing: error))
        }
    }

    /// Builds the endpoint's request and applies the interceptors in order.
    /// Internal so tests can assert adaptation without touching the network.
    func adaptedRequest(for endpoint: any APIEndpoint) async throws -> URLRequest {
        guard let url = URL(string: endpoint.path, relativeTo: baseURL) else {
            throw NetworkError.invalidURL
        }
        var request = URLRequest(url: url)
        request.httpMethod = endpoint.method.rawValue
        request.httpBody = endpoint.body
        for (field, value) in endpoint.headers {
            request.setValue(value, forHTTPHeaderField: field)
        }
        for interceptor in interceptors {
            request = try await interceptor.adapt(request)
        }
        return request
    }

    static func validate(statusCode: Int) throws {
        switch statusCode {
        case 200..<300:
            return
        case 400..<500:
            throw NetworkError.clientError(statusCode: statusCode)
        case 500..<600:
            throw NetworkError.serverError(statusCode: statusCode)
        default:
            throw NetworkError.invalidResponse
        }
    }
}
