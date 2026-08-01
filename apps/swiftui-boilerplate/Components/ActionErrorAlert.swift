import Core
import SwiftUI

extension View {
    /// Presents action errors (submit, delete, …) as a local alert owned by the
    /// triggering screen — the action half of the error split rule. There is
    /// deliberately no global error broadcaster.
    func actionErrorAlert(_ error: Binding<NetworkError?>) -> some View {
        alert(
            Text("error.action.title"),
            isPresented: Binding(
                get: { error.wrappedValue != nil },
                set: { if !$0 { error.wrappedValue = nil } }
            ),
            presenting: error.wrappedValue
        ) { _ in
            Button(role: .cancel) {} label: {
                Text("common.ok")
            }
        } message: { error in
            Text(error.userMessage)
        }
    }
}

extension NetworkError {
    /// User-facing copy stays in the app target so Core remains free of UI
    /// concerns and localization.
    var userMessage: String {
        switch self {
        case .invalidURL, .invalidResponse:
            String(localized: "error.message.invalid_response")
        case .decodingFailed:
            String(localized: "error.message.decoding")
        case .clientError:
            String(localized: "error.message.client")
        case .serverError:
            String(localized: "error.message.server")
        case .transport:
            String(localized: "error.message.transport")
        }
    }
}
