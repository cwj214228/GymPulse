package com.gympulse.app.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gympulse.app.ui.theme.*

/**
 * 顶部状态栏 — 模拟 iOS 风格 status bar。
 */
@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
            color = ForgeFg
        )
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            // Signal bars
            Text("📶", fontSize = 12.sp)
            // Battery
            Text("🔋", fontSize = 12.sp)
        }
    }
}

/**
 * 区间标签 — MONO 大写标签，如 "今日训练部位"
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = ForgeMuted,
        modifier = modifier
    )
}

/**
 * 底部导航栏 — 3 个 tab: 首页 / 记录 / 统计
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF202021).copy(alpha = 0.94f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavItem(
                label = "首页",
                icon = Icons.Outlined.Home,
                isActive = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            NavItem(
                label = "记录",
                icon = Icons.Outlined.ViewList,
                isActive = currentRoute == "log",
                onClick = { onNavigate("log") }
            )
            NavItem(
                label = "统计",
                icon = Icons.Outlined.BarChart,
                isActive = currentRoute == "stats",
                onClick = { onNavigate("stats") }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) ForgeAccent else ForgeMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = if (isActive) ForgeAccent else ForgeMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * 部位选择芯片 — 2 列 grid 中的单个芯片。
 */
@Composable
fun PartChip(
    part: BodyPart,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.97f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    Surface(
        modifier = modifier
            .scale(if (isSelected) 1f else scaleAnim) // subtle press effect handled by ripple
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AccentOnBg10 else ForgeSurface2,
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(1.dp, ForgeAccent)
        else
            androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ForgeAccent else ForgeSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = part.icon,
                    contentDescription = null,
                    tint = if (isSelected) ForgeOnAccent else ForgeMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Name
            Text(
                text = part.name,
                style = MaterialTheme.typography.bodyLarge,
                color = ForgeFg,
                modifier = Modifier.weight(1f)
            )

            // Check indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) ForgeAccent
                        else Color.Transparent
                    )
                    .then(
                        if (!isSelected)
                            Modifier.border(2.dp, ForgeBorder, RoundedCornerShape(6.dp))
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text("✓", color = ForgeOnAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 大号完成勾 — 用于训练确认页。
 */
@Composable
fun BigCheckCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(SuccessOnBg15)
            .border(2.dp, ForgeSuccess, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("✓", color = ForgeSuccess, fontSize = 36.sp, fontWeight = FontWeight.Black)
    }
}

/**
 * 统计大数字
 */
@Composable
fun StatNumber(
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        color = ForgeFg,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
    )
}
