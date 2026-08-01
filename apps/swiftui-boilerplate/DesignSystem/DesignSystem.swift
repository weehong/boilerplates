import SwiftUI

/// Semantic design tokens. Swap the values, keep the names — call sites never
/// reference raw colors, fonts, or magic numbers.
enum DesignSystem {
    enum Colors {
        static let accent = Color.accentColor
        static let danger = Color.red
        static let secondaryText = Color.secondary
    }

    enum Typography {
        static let title = Font.title2.weight(.semibold)
        static let headline = Font.headline
        static let body = Font.body
        static let caption = Font.caption
    }

    enum Spacing {
        static let xs: CGFloat = 4
        static let s: CGFloat = 8
        static let m: CGFloat = 16
        static let l: CGFloat = 24
    }
}
