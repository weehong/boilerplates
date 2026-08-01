import Foundation
#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

/// Adapts an outbound request before it is sent (headers, logging). Interceptors
/// run in array order and cannot observe responses — response-side concerns such
/// as token refresh are deliberately out of scope (see the README's extension
/// points).
public protocol RequestInterceptor: Sendable {
    func adapt(_ request: URLRequest) async throws -> URLRequest
}

/// Sets default headers on every request without overriding values an endpoint
/// has already set.
public struct DefaultHeadersInterceptor: RequestInterceptor {
    private let headers: [String: String]

    public init(headers: [String: String]) {
        self.headers = headers
    }

    public func adapt(_ request: URLRequest) async throws -> URLRequest {
        var request = request
        for (field, value) in headers where request.value(forHTTPHeaderField: field) == nil {
            request.setValue(value, forHTTPHeaderField: field)
        }
        return request
    }
}

/// Prints each outbound request. Replace with your logging framework of choice.
public struct LoggingInterceptor: RequestInterceptor {
    public init() {}

    public func adapt(_ request: URLRequest) async throws -> URLRequest {
        print("→ \(request.httpMethod ?? "GET") \(request.url?.absoluteString ?? "?")")
        return request
    }
}
