import Foundation

/// The deployment environment a build is pointed at, selected by build
/// configuration (Debug → development, Staging → staging, Release → production)
/// and injected through Info.plist by the matching xcconfig.
public enum AppEnvironment: String, Sendable, CaseIterable {
    case development
    case staging
    case production
}

public struct AppConfiguration: Sendable {
    public enum LoadError: Error, Equatable {
        case missingOrInvalidEnvironment
        case missingOrInvalidBaseURL
    }

    public let environment: AppEnvironment
    public let baseURL: URL

    public init(environment: AppEnvironment, baseURL: URL) {
        self.environment = environment
        self.baseURL = baseURL
    }

    /// Builds the configuration from an Info.plist dictionary containing the
    /// xcconfig-injected `APP_ENVIRONMENT` and `API_BASE_URL` keys.
    public static func load(from info: [String: Any]) throws -> AppConfiguration {
        guard
            let rawEnvironment = info["APP_ENVIRONMENT"] as? String,
            let environment = AppEnvironment(rawValue: rawEnvironment)
        else {
            throw LoadError.missingOrInvalidEnvironment
        }
        guard
            let rawURL = info["API_BASE_URL"] as? String,
            let baseURL = URL(string: rawURL),
            baseURL.scheme != nil
        else {
            throw LoadError.missingOrInvalidBaseURL
        }
        return AppConfiguration(environment: environment, baseURL: baseURL)
    }
}
