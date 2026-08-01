import Core
import SwiftUI

@main
struct SwiftUIBoilerplateApp: App {
    private let dependencies = AppDependencies.live()
    @State private var coordinator = Coordinator()

    var body: some Scene {
        WindowGroup {
            RootView(dependencies: dependencies)
                .environment(coordinator)
        }
    }
}
