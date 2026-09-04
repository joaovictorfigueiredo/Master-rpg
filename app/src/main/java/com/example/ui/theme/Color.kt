package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// "Sophisticated Dark" Design Palette
val DarkBg = Color(0xFF111318)              // #111318 Deep canvas
val DarkSurface = Color(0xFF1A1C1E)         // #1A1C1E Primary card / surface
val DarkSurfaceVariant = Color(0xFF2D2F31)  // #2D2F31 Secondary container / chip / input
val DarkBorder = Color(0xFF333537)          // #333537 Card structural border
val DarkOutline = Color(0xFF44474E)         // #44474E Element / input / pill outline
val DarkCard = Color(0xFF202226)            // Card background

val VioletPrimary = Color(0xFFD0BCFF)       // #D0BCFF Primary lavender accent
val VioletContainer = Color(0xFF381E72)     // #381E72 Deep royal violet container
val VioletBorder = Color(0xFF4F378B)        // #4F378B Container border
val VioletLight = Color(0xFFE8DEF8)         // #E8DEF8 Active highlight

val CoralHp = Color(0xFFFFB4AB)             // #FFB4AB Warm coral for HP / damage alert
val CoralRed = Color(0xFFFF5252)            // Bright Red for Combat & Damage
val EmeraldSuccess = Color(0xFFB4FFB4)      // #B4FFB4 Soft mint for heal / success
val EmeraldGreen = Color(0xFF4ADE80)        // Bright Emerald for Healing & Success
val GreenOnline = Color(0xFF22C55E)         // green-500 online indicator

val AmberGold = Color(0xFFFFD54F)           // Golden Amber for Weapons & Criticals
val CyanHighlight = Color(0xFF38BDF8)       // Vivid Cyan for Mana & Intellect

val TextPrimary = Color(0xFFE2E2E6)         // #E2E2E6 High contrast text
val TextSecondary = Color(0xFFC6C6CA)       // #C6C6CA Muted subtitle text
val TextMuted = Color(0xFF7A7D85)           // Subtle labels

// Theme Compatibility Aliases
val GoldPrimary = AmberGold
val GoldLight = VioletLight
val GoldDark = VioletContainer
val CrimsonAccent = CoralRed
val ArcaneViolet = VioletPrimary
val EmeraldRestore = EmeraldGreen
val SlateDark900 = DarkBg
val SlateDark800 = DarkSurface
val SlateDark700 = DarkSurfaceVariant
val SlateDark600 = DarkBorder
