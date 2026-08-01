import Core
import SwiftUI

/// Binds the Coordinator's state to SwiftUI's navigation machinery and acts as
/// the route→view factory: every screen and its view model is constructed
/// here, with dependencies passed in explicitly.
struct RootView: View {
    let dependencies: AppDependencies
    @Environment(Coordinator.self) private var coordinator

    var body: some View {
        @Bindable var coordinator = coordinator
        NavigationStack(path: $coordinator.path) {
            PostListView(viewModel: PostListViewModel(network: dependencies.network))
                .navigationDestination(for: AppRoute.self) { route in
                    destination(for: route)
                }
        }
        .sheet(item: $coordinator.sheet) { route in
            sheetView(for: route)
        }
        .fullScreenCover(item: $coordinator.cover) { route in
            coverView(for: route)
        }
    }

    @ViewBuilder
    private func destination(for route: AppRoute) -> some View {
        switch route {
        case .postDetail(let postID):
            PostDetailView(postID: postID, network: dependencies.network)
        }
    }

    @ViewBuilder
    private func sheetView(for route: SheetRoute) -> some View {
        switch route {
        case .about:
            AboutView()
        }
    }

    @ViewBuilder
    private func coverView(for route: CoverRoute) -> some View {
        switch route {
        case .welcome:
            WelcomeView()
        }
    }
}

#Preview {
    RootView(dependencies: .preview())
        .environment(Coordinator())
}
