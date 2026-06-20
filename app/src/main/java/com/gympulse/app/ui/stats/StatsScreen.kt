package com.gympulse.app.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gympulse.app.ui.common.SectionLabel
import com.gympulse.app.ui.common.StatusBar
import com.gympulse.app.ui.theme.*

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = ForgeBlack,
        topBar = {}
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "加载中…",
                    style = MaterialTheme.typography.labelMedium,
                    color = ForgeMuted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 标题 ──
                Text(
                    text = "统计分析",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ForgeFg
                )

                // ── 2x2 统计大数字 ──
                BigStatsGrid(
                    totalDays = uiState.totalDays,
                    totalParts = uiState.totalParts,
                    topPart = uiState.topPart,
                    topPartCount = uiState.topPartCount,
                    avgPartsPerDay = uiState.avgPartsPerDay
                )

                // ── 部位频次条形图 ──
                PartBarChart(partCounts = uiState.partCounts)

                // ── 本月训练日历热力图 ──
                CalendarHeatmap(
                    calendarData = uiState.calendarData,
                    daysInMonth = uiState.daysInMonth
                )

                Spacer(modifier = Modifier.height(100.dp)) // 底部导航占位
            }
        }
    }
}

@Composable
private fun BigStatsGrid(
    totalDays: Int,
    totalParts: Int,
    topPart: String,
    topPartCount: Int,
    avgPartsPerDay: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BigStatCard(
                label = "训练天数",
                value = "$totalDays",
                sub = "全部记录",
                modifier = Modifier.weight(1f)
            )
            BigStatCard(
                label = "总部位次",
                value = "$totalParts",
                sub = "累计",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BigStatCard(
                label = "最爱练",
                value = topPart,
                sub = "$topPartCount 次",
                modifier = Modifier.weight(1f)
            )
            BigStatCard(
                label = "每次平均",
                value = avgPartsPerDay,
                sub = "个部位",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BigStatCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ForgeMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = ForgeFg,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = ForgeSuccess
            )
        }
    }
}

@Composable
private fun PartBarChart(partCounts: Map<String, Int>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "部位训练频次",
                style = MaterialTheme.typography.headlineSmall,
                color = ForgeFg
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (partCounts.isEmpty()) {
                Text(
                    text = "暂无数据",
                    style = MaterialTheme.typography.labelMedium,
                    color = ForgeMuted,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                val maxCount = partCounts.maxOf { it.value }.coerceAtLeast(1)
                val sorted = partCounts.entries.sortedByDescending { it.value }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sorted.forEachIndexed { index, (part, count) ->
                        val fraction = count.toFloat() / maxCount
                        val barColor = when (index % 3) {
                            0 -> ForgeAccent
                            1 -> Color(0xFFD9733A)  // oklch(62% 0.20 35)
                            else -> Color(0xFFB85030)  // oklch(54% 0.16 40)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 标签
                            Text(
                                text = part,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = ForgeFg,
                                modifier = Modifier.width(52.dp)
                            )

                            // 条形图
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ForgeSurface2)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(barColor)
                                ) {
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 10.dp, top = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeatmap(
    calendarData: Map<String, Int>,
    daysInMonth: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "本月训练日历",
                style = MaterialTheme.typography.headlineSmall,
                color = ForgeFg
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 日期点阵
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // 用 FlowRow 模拟，每行约 14 个点
                val items = calendarData.entries.sortedBy { it.key }.toList()
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    items.chunked(14).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            row.forEach { (_, count) ->
                                val color = when {
                                    count >= 3 -> ForgeAccent          // l3
                                    count >= 2 -> AccentOnBg25        // l2
                                    count >= 1 -> AccentOnBg25.copy(alpha = 0.4f)  // l1
                                    else -> ForgeSurface2              // 未练
                                }
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 图例
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ForgeSurface2)
                )
                Text(
                    text = "未练",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForgeMuted
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AccentOnBg25.copy(alpha = 0.4f))
                )
                Text(
                    text = "1 个部位",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForgeMuted
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AccentOnBg25)
                )
                Text(
                    text = "2 个部位",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForgeMuted
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ForgeAccent)
                )
                Text(
                    text = "3+ 个部位",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForgeMuted
                )
            }
        }
    }
}
