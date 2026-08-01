import Testing
@testable import Core

@Suite("Coordinator navigation")
@MainActor
struct CoordinatorTests {
    @Test("push appends to the route stack")
    func pushAppends() {
        let coordinator = Coordinator()
        coordinator.push(.postDetail(postID: 1))
        coordinator.push(.postDetail(postID: 2))
        #expect(coordinator.path == [.postDetail(postID: 1), .postDetail(postID: 2)])
    }

    @Test("pop removes the top route and is safe on an empty stack")
    func popRemovesTop() {
        let coordinator = Coordinator()
        coordinator.push(.postDetail(postID: 1))
        coordinator.pop()
        #expect(coordinator.path.isEmpty)
        coordinator.pop()
        #expect(coordinator.path.isEmpty)
    }

    @Test("popToRoot clears the stack")
    func popToRootClears() {
        let coordinator = Coordinator()
        coordinator.push(.postDetail(postID: 1))
        coordinator.push(.postDetail(postID: 2))
        coordinator.popToRoot()
        #expect(coordinator.path.isEmpty)
    }

    @Test("presenting a sheet does not touch the push stack")
    func sheetIsIndependent() {
        let coordinator = Coordinator()
        coordinator.push(.postDetail(postID: 1))
        coordinator.present(SheetRoute.about)
        #expect(coordinator.sheet == .about)
        #expect(coordinator.path == [.postDetail(postID: 1)])
        coordinator.dismissSheet()
        #expect(coordinator.sheet == nil)
    }

    @Test("presenting and dismissing a cover")
    func coverPresentsAndDismisses() {
        let coordinator = Coordinator()
        coordinator.present(CoverRoute.welcome)
        #expect(coordinator.cover == .welcome)
        coordinator.dismissCover()
        #expect(coordinator.cover == nil)
    }
}
