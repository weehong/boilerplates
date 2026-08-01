public enum NetworkError: Error, Equatable, Sendable {
    case invalidURL
    case invalidResponse
    case decodingFailed(description: String)
    case clientError(statusCode: Int)
    case serverError(statusCode: Int)
    case transport(description: String)
}
