import Core
import SwiftUI

/// Example sheet destination (`SheetRoute.about`).
struct AboutView: View {
    @Environment(Coordinator.self) private var coordinator

    var body: some View {
        NavigationStack {
            VStack(spacing: DesignSystem.Spacing.m) {
                Image(systemName: "shippingbox")
                    .font(.system(size: 48))
                    .foregroundStyle(DesignSystem.Colors.accent)
                Text("about.title")
                    .font(DesignSystem.Typography.title)
                Text("about.body")
                    .font(DesignSystem.Typography.body)
                    .foregroundStyle(DesignSystem.Colors.secondaryText)
                    .multilineTextAlignment(.center)
            }
            .padding(DesignSystem.Spacing.l)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        coordinator.dismissSheet()
                    } label: {
                        Text("common.done")
                    }
                }
            }
        }
    }
}

#Preview {
    AboutView()
        .environment(Coordinator())
}
