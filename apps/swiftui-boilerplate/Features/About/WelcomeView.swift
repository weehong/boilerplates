import Core
import SwiftUI

/// Example full-screen cover destination (`CoverRoute.welcome`).
struct WelcomeView: View {
    @Environment(Coordinator.self) private var coordinator

    var body: some View {
        VStack(spacing: DesignSystem.Spacing.l) {
            Spacer()
            Image(systemName: "hand.wave")
                .font(.system(size: 64))
                .foregroundStyle(DesignSystem.Colors.accent)
            Text("welcome.title")
                .font(DesignSystem.Typography.title)
            Text("welcome.body")
                .font(DesignSystem.Typography.body)
                .foregroundStyle(DesignSystem.Colors.secondaryText)
                .multilineTextAlignment(.center)
            Spacer()
            Button {
                coordinator.dismissCover()
            } label: {
                Text("welcome.dismiss")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
        }
        .padding(DesignSystem.Spacing.l)
    }
}

#Preview {
    WelcomeView()
        .environment(Coordinator())
}
