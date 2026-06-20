package com.gympulse.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 铁壳训练 · 设计 Token — 工业锻造方向 (Industrial Forged)
 *
 * 所有 sRGB 值由浏览器 Canvas API 从 OKLCH 精确转换而来，
 * 确保与 HTML 原型颜色完全一致。
 */

// ═══════════════════════ 中性色 (暗色暖调) ═══════════════════════
val ForgeBlack    = Color(0xFF070504)  // --bg:       oklch(12% 0.005 60)
val ForgeSurface  = Color(0xFF110F0D)  // --surface:  oklch(17% 0.005 60)
val ForgeSurface2 = Color(0xFF1C1A18)  // --surface2: oklch(22% 0.005 60)
val ForgeFg       = Color(0xFFECEBEA)  // --fg:       oklch(94% 0.002 70)
val ForgeMuted    = Color(0xFF777471)  // --muted:    oklch(56% 0.006 70)
val ForgeBorder   = Color(0xFF262421)  // --border:   oklch(26% 0.005 70)

// ═══════════════════════ 功能色 ═══════════════════════
val ForgeAccent   = Color(0xFFD33D2C)  // --accent:    oklch(58% 0.19 30)  锻造之火
val ForgeAccent2  = Color(0xFF007E6C)  // --accent2:   oklch(52% 0.12 180)  冷钢
val ForgeSuccess  = Color(0xFF009342)  // --success:   oklch(58% 0.16 150)
val ForgeWarning  = Color(0xFFC18100)  // --warning:   oklch(65% 0.16 80)
val ForgeDanger   = Color(0xFFBA0329)  // --danger:    oklch(50% 0.20 22)

// ═══════════════════════ 衍生色 ═══════════════════════
val ForgeOnAccent    = Color(0xFFF8F8F8)  // --on-accent: oklch(98% 0 0)
val ForgeElevation3  = Color(0xFF262322)  // oklch(26% 0.005 60) 对话框/模态
val ForgeNavBg       = Color(0xFF0B0907)  // oklch(14% 0.005 60) 导航栏底色

// ═══════════════════════ 半透明叠加色 (已合成到 bg=#070504 上) ═══════════════════════
val AccentOnBg06     = Color(0xFF130806)  // oklch(58% 0.19 30 / 0.06) on bg  — picker card selected
val AccentOnBg10     = Color(0xFF1C0A07)  // oklch(58% 0.19 30 / 0.10) on bg
val AccentOnBg18     = Color(0xFF2C0F0B)  // oklch(58% 0.19 30 / 0.18) on bg  — picker card border
val AccentOnBg20     = Color(0xFF2F100C)  // oklch(58% 0.19 30 / 0.20) on bg
val AccentOnBg25     = Color(0xFF3A120E)  // oklch(58% 0.19 30 / 0.25) on bg
val SuccessOnBg15    = Color(0xFF061A0D)  // oklch(58% 0.16 150 / 0.15) on bg

// ═══════════════════════ 状态叠加色 ═══════════════════════
val StateHover       = Color(0xFF161413)  // oklch(100% 0 0 / 0.06) on bg
val StateActive      = Color(0xFF201E1D)  // oklch(100% 0 0 / 0.10) on bg

// ═══════════════════════ 导航栏半透明 ═══════════════════════
// bottom-nav: oklch(14% 0.005 60 / 0.94) → composited with 94% opacity
val ForgeNavBgGlass  = Color(0xEF0B0907)  // #0B0907 with 0.94 alpha
