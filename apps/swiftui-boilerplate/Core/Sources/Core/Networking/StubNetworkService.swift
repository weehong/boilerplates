import Foundation

/// The one fake network: preset responses per endpoint path, optional latency,
/// and a gated mode that suspends every response until the test releases it —
/// the deterministic way to observe `.loading` without a clock. Shared by tests
/// and SwiftUI previews; it records nothing and asserts nothing.
public actor StubNetworkService: NetworkServiceProtocol {
    public enum Stub: Sendable {
        case success(Data)
        case failure(NetworkError)
    }

    private var stubs: [String: Stub] = [:]
    private var latency: Duration?
    private var isGated = false
    private var gateWaiters: [CheckedContinuation<Void, Never>] = []
    private var requestWaiters: [CheckedContinuation<Void, Never>] = []
    private var requestsInFlight = 0

    public init() {}

    /// Synchronous configuration for SwiftUI previews, where awaiting the
    /// actor's setters isn't practical.
    public init(stubs: [String: Stub]) {
        self.stubs = stubs
    }

    public func stub(_ path: String, with stub: Stub) {
        stubs[path] = stub
    }

    public func stub(_ path: String, returning value: some Encodable) throws {
        stubs[path] = .success(try JSONEncoder().encode(value))
    }

    public func setLatency(_ latency: Duration) {
        self.latency = latency
    }

    /// Suspend every subsequent response until `release()` is called.
    public func gate() {
        isGated = true
    }

    /// Resume all responses suspended by `gate()`.
    public func release() {
        isGated = false
        let waiters = gateWaiters
        gateWaiters = []
        for waiter in waiters {
            waiter.resume()
        }
    }

    /// Returns once at least one request has reached the stub — the
    /// synchronization point for asserting `.loading` while gated.
    public func waitUntilRequested() async {
        guard requestsInFlight == 0 else { return }
        await withCheckedContinuation { requestWaiters.append($0) }
    }

    public func request<Response: Decodable & Sendable>(
        _ endpoint: any APIEndpoint,
        as type: Response.Type
    ) async throws -> Response {
        requestsInFlight += 1
        let waiters = requestWaiters
        requestWaiters = []
        for waiter in waiters {
            waiter.resume()
        }
        defer { requestsInFlight -= 1 }

        if let latency {
            try? await Task.sleep(for: latency)
        }
        if isGated {
            await withCheckedContinuation { gateWaiters.append($0) }
        }

        guard let stub = stubs[endpoint.path] else {
            throw NetworkError.transport(description: "No stub registered for path '\(endpoint.path)'")
        }
        switch stub {
        case .failure(let error):
            throw error
        case .success(let data):
            do {
                return try JSONDecoder().decode(Response.self, from: data)
            } catch {
                throw NetworkError.decodingFailed(description: String(describing: error))
            }
        }
    }
}
