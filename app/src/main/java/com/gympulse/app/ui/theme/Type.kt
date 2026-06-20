package com.gympulse.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 铁壳训练 · 字体系统
 *
 * 三族体系:
 * - Display: 标题、大数字 (SF Pro Display / system)
 * - Body:    正文、按钮 (SF Pro Text / system)
 * - Mono:    标签、元数据、数字 (SF Mono / monospace)
 */

// 系统字体族 (Android 上使用 system default 作为 Display/Body)
val DisplayFont = FontFamily.Default
val BodyFont = FontFamily.Default
val MonoFont = FontFamily.Monospace

val GymPulseTypography = Typography(
    // ── Display 风格 ──
    displayLarge = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = (-0.03).sp,
        lineHeight = 38.sp
    ),
    displayMedium = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = (-0.03).sp,
        lineHeight = 34.sp
    ),
    displaySmall = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = (-0.02).sp,
        lineHeight = 30.sp
    ),

    // ── 标题 ──
    headlineLarge = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        letterSpacing = (-0.03).sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = (-0.02).sp,
        lineHeight = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = (-0.01).sp,
        lineHeight = 20.sp
    ),

    // ── Body ──
    bodyLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // ── Mono 标签/数据 ──
    labelLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.02.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 0.04.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.08.sp,
        lineHeight = 14.sp
    ),

    // ── 数字专用 ──
    titleLarge = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.02).sp,
        lineHeight = 34.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        letterSpacing = (-0.02).sp,
        lineHeight = 28.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MonoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = (-0.01).sp,
        lineHeight = 22.sp
    ),
)
