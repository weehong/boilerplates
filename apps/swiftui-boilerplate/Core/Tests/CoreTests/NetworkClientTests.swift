import Foundation
import Testing
@testable import Core
#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

private struct AppendingInterceptor: RequestInterceptor {
    let value: String

    func adapt(_ request: URLRequest) async throws -> URLRequest {
        var request = request
        let existing = request.value(forHTTPHeaderField: "X-Trace") ?? ""
        request.setValue(existing + value, forHTTPHeaderField: "X-Trace")
        return request
    }
}

@Suite("NetworkClient")
struct NetworkClientTests {
    private let client = NetworkClient(
        baseURL: URL(string: "https://example.test/")!,
        interceptors: [AppendingInterceptor(value: "a"), AppendingInterceptor(value: "b")]
    )

    @Test("builds the request from the endpoint")
    func buildsRequest() async throws {
        let request = try await client.adaptedRequest(for: PostsEndpoint.delete(id: 7))
        #expect(request.url?.absoluteString == "https://example.test/posts/7")
        #expect(request.httpMethod == "DELETE")
    }

    @Test("applies interceptors in array order")
    func interceptorOrder() async throws {
        let request = try await client.adaptedRequest(for: PostsEndpoint.list)
        #expect(request.value(forHTTPHeaderField: "X-Trace") == "ab")
    }

    @Test("maps status codes to NetworkError", arguments: [
        (204, nil),
        (404, NetworkError.clientError(statusCode: 404)),
        (500, NetworkError.serverError(statusCode: 500)),
        (399, NetworkError.invalidResponse),
    ] as [(Int, NetworkError?)])
    func statusCodeMapping(statusCode: Int, expected: NetworkError?) {
        do {
            try NetworkClient.validate(statusCode: statusCode)
            #expect(expected == nil)
        } catch let error as NetworkError {
            #expect(error == expected)
        } catch {
            Issue.record("unexpected error type: \(error)")
        }
    }
}
