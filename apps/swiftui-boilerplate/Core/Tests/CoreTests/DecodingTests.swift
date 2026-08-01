import Foundation
import Testing
@testable import Core

@Suite("Post decoding")
struct DecodingTests {
    @Test("decodes a well-formed payload")
    func decodesWellFormedPayload() throws {
        let json = """
        [{"userId": 1, "id": 10, "title": "Hello", "body": "World"}]
        """
        let posts = try JSONDecoder().decode([Post].self, from: Data(json.utf8))
        #expect(posts == [Post(id: 10, userId: 1, title: "Hello", body: "World")])
    }

    @Test("ignores unknown fields")
    func ignoresUnknownFields() throws {
        let json = """
        {"userId": 1, "id": 10, "title": "Hello", "body": "World", "extra": true}
        """
        let post = try JSONDecoder().decode(Post.self, from: Data(json.utf8))
        #expect(post.id == 10)
    }

    @Test("fails on a missing required field")
    func failsOnMissingField() {
        let json = """
        {"userId": 1, "id": 10, "title": "Hello"}
        """
        #expect(throws: DecodingError.self) {
            try JSONDecoder().decode(Post.self, from: Data(json.utf8))
        }
    }

    @Test("empty JSON object decodes as EmptyResponse")
    func emptyObjectDecodes() throws {
        _ = try JSONDecoder().decode(EmptyResponse.self, from: Data("{}".utf8))
    }
}
