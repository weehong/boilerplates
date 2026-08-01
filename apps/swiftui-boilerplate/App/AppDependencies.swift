import Core
import Foundation

/// The dependency container, living only at the composition root. Views never
/// see it — screens receive what they need through their initializers, and the
/// SwiftUI environment carries only the Coordinator.
struct AppDependencies {
    let configuration: AppConfiguration
    let network: any NetworkServiceProtocol

    static func live() -> AppDependencies {
        guard let configuration = try? AppConfiguration.load(from: Bundle.main.infoDictionary ?? [:]) else {
            fatalError("Info.plist is missing APP_ENVIRONMENT / API_BASE_URL — check the xcconfig wiring.")
        }
        let network = NetworkClient(
            baseURL: configuration.baseURL,
            interceptors: [
                DefaultHeadersInterceptor(headers: ["Accept": "application/json"]),
                LoggingInterceptor(),
            ]
        )
        return AppDependencies(configuration: configuration, network: network)
    }

    static func preview() -> AppDependencies {
        AppDependencies(
            configuration: AppConfiguration(
                environment: .development,
                baseURL: URL(string: "https://preview.invalid")!
            ),
            network: StubNetworkService(stubs: [
                "posts": .success((try? JSONEncoder().encode(Post.samples)) ?? Data()),
                "posts/1": .success((try? JSONEncoder().encode(Post.samples[0])) ?? Data()),
            ])
        )
    }
}
