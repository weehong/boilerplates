import Foundation
import Testing
@testable import Core

@Suite("PostListViewModel")
@MainActor
struct PostListViewModelTests {
    @Test("load reaches .loaded on success")
    func loadSucceeds() async throws {
        let stub = StubNetworkService()
        try await stub.stub("posts", returning: Post.samples)
        let viewModel = PostListViewModel(network: stub)

        await viewModel.load()

        #expect(viewModel.state == .loaded(Post.samples))
    }

    @Test("load reaches .error on failure")
    func loadFails() async {
        let stub = StubNetworkService()
        await stub.stub("posts", with: .failure(.serverError(statusCode: 503)))
        let viewModel = PostListViewModel(network: stub)

        await viewModel.load()

        #expect(viewModel.state == .error(.serverError(statusCode: 503)))
    }

    @Test(".loading is observable while the response is gated")
    func loadingVisibleWhileGated() async throws {
        let stub = StubNetworkService()
        try await stub.stub("posts", returning: Post.samples)
        await stub.gate()
        let viewModel = PostListViewModel(network: stub)

        let load = Task { await viewModel.load() }
        await stub.waitUntilRequested()
        #expect(viewModel.state.isLoading)

        await stub.release()
        await load.value
        #expect(viewModel.state == .loaded(Post.samples))
    }

    @Test("delete removes the post on success")
    func deleteSucceeds() async throws {
        let stub = StubNetworkService()
        try await stub.stub("posts", returning: Post.samples)
        await stub.stub("posts/1", with: .success(Data("{}".utf8)))
        let viewModel = PostListViewModel(network: stub)
        await viewModel.load()

        await viewModel.delete(Post.samples[0])

        #expect(viewModel.state.value?.map(\.id) == [2, 3])
        #expect(viewModel.actionError == nil)
    }

    @Test("delete failure sets actionError and keeps the list intact")
    func deleteFails() async throws {
        let stub = StubNetworkService()
        try await stub.stub("posts", returning: Post.samples)
        await stub.stub("posts/1", with: .failure(.clientError(statusCode: 403)))
        let viewModel = PostListViewModel(network: stub)
        await viewModel.load()

        await viewModel.delete(Post.samples[0])

        #expect(viewModel.actionError == .clientError(statusCode: 403))
        #expect(viewModel.state == .loaded(Post.samples))
    }
}
