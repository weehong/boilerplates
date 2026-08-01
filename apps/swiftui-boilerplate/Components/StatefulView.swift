import Core
import SwiftUI

/// Renders a `ViewState`: spinner while loading, content when loaded, and an
/// inline error with retry on failure — the loading half of the error split
/// rule.
struct StatefulView<Value: Sendable, Content: View>: View {
    let state: ViewState<Value>
    @ViewBuilder let content: (Value) -> Content
    var retry: (() async -> Void)?

    init(
        state: ViewState<Value>,
        @ViewBuilder content: @escaping (Value) -> Content,
        retry: (() async -> Void)? = nil
    ) {
        self.state = state
        self.content = content
        self.retry = retry
    }

    var body: some View {
        switch state {
        case .idle:
            Color.clear
        case .loading:
            ProgressView()
                .controlSize(.large)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .loaded(let value):
            content(value)
        case .error(let error):
            ContentUnavailableView {
                Label {
                    Text("error.loading.title")
                } icon: {
                    Image(systemName: "wifi.exclamationmark")
                }
            } description: {
                Text(error.userMessage)
            } actions: {
                if let retry {
                    Button {
                        Task { await retry() }
                    } label: {
                        Text("common.retry")
                    }
                    .buttonStyle(.borderedProminent)
                }
            }
        }
    }
}
