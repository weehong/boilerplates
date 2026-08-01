import Core
import SwiftUI

/// The ViewState-machine pattern: a data-backed screen driven by an
/// `@Observable` view model through `StatefulView`. Compare with
/// `PostDetailView` for the lightweight alternative.
struct PostListView: View {
    @State private var viewModel: PostListViewModel
    @Environment(Coordinator.self) private var coordinator

    init(viewModel: PostListViewModel) {
        _viewModel = State(initialValue: viewModel)
    }

    var body: some View {
        @Bindable var viewModel = viewModel
        StatefulView(state: viewModel.state) { posts in
            List {
                ForEach(posts) { post in
                    Button {
                        coordinator.push(.postDetail(postID: post.id))
                    } label: {
                        row(for: post)
                    }
                    .buttonStyle(.plain)
                    .swipeActions {
                        Button(role: .destructive) {
                            Task { await viewModel.delete(post) }
                        } label: {
                            Text("common.delete")
                        }
                    }
                }
            }
        } retry: {
            await viewModel.load()
        }
        .task {
            if case .idle = viewModel.state {
                await viewModel.load()
            }
        }
        .navigationTitle(Text("post_list.title"))
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    coordinator.present(SheetRoute.about)
                } label: {
                    Image(systemName: "info.circle")
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    coordinator.present(CoverRoute.welcome)
                } label: {
                    Image(systemName: "sparkles")
                }
            }
        }
        .actionErrorAlert($viewModel.actionError)
    }

    private func row(for post: Post) -> some View {
        VStack(alignment: .leading, spacing: DesignSystem.Spacing.xs) {
            Text(post.title)
                .font(DesignSystem.Typography.headline)
            Text(post.body)
                .font(DesignSystem.Typography.caption)
                .foregroundStyle(DesignSystem.Colors.secondaryText)
                .lineLimit(2)
        }
        .padding(.vertical, DesignSystem.Spacing.xs)
    }
}

#Preview {
    NavigationStack {
        PostListView(viewModel: PostListViewModel(network: AppDependencies.preview().network))
    }
    .environment(Coordinator())
}
