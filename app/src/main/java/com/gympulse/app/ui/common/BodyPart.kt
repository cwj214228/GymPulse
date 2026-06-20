package com.gympulse.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 训练部位定义 — 8 个可选部位。
 * 与 HTML 原型中 bodyParts 数组一一对应。
 */
data class BodyPart(
    val id: String,
    val name: String,
    val icon: ImageVector
)

val BodyParts = listOf(
    BodyPart("chest",    "胸部",   Icons.Default.FitnessCenter),
    BodyPart("back",     "背部",   Icons.Default.Shield),
    BodyPart("shoulder", "肩部",   Icons.Default.AccessibilityNew),
    BodyPart("legs",     "腿部",   Icons.Default.DirectionsWalk),
    BodyPart("biceps",   "肱二头", Icons.Default.SportsMartialArts),
    BodyPart("triceps",  "肱三头", Icons.Default.PanTool),
    BodyPart("abs",      "腹部",   Icons.Default.ViewList),
    BodyPart("cardio",   "有氧",   Icons.Default.FavoriteBorder),
)

fun bodyPartById(id: String): BodyPart? = BodyParts.find { it.id == id }
fun bodyPartByName(name: String): BodyPart? = BodyParts.find { it.name == name }
