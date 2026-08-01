import Core
import SwiftUI

/// The lightweight pattern: plain `@State` + `.task`, no view model and no
/// ViewState machine — the sanctioned choice for simple screens. Compare with
/// `PostListView` for the full pattern.
struct PostDetailView: View {
    let postID: Int
    let network: any NetworkServiceProtocol

    @State private var post: Post?
    @State private var loadFailed = false

    var body: some View {
        Group {
            if let post {
                ScrollView {
                    VStack(alignment: .leading, spacing: DesignSystem.Spacing.m) {
                        Text(post.title)
                            .font(DesignSystem.Typography.title)
                        Text(post.body)
                            .font(DesignSystem.Typography.body)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(DesignSystem.Spacing.m)
                }
            } else if loadFailed {
                ContentUnavailableView {
                    Label {
                        Text("error.loading.title")
                    } icon: {
                        Image(systemName: "wifi.exclamationmark")
                    }
                } actions: {
                    Button {
                        Task { await load() }
                    } label: {
                        Text("common.retry")
                    }
                    .buttonStyle(.borderedProminent)
                }
            } else {
                ProgressView()
            }
        }
        .navigationTitle(Text("post_detail.title"))
        .navigationBarTitleDisplayMode(.inline)
        .task { await load() }
    }

    private func load() async {
        loadFailed = false
        do {
            post = try await network.request(PostsEndpoint.detail(id: postID), as: Post.self)
        } catch {
            loadFailed = true
        }
    }
}

#Preview {
    NavigationStack {
        PostDetailView(postID: 1, network: AppDependencies.preview().network)
    }
}
