package com.gympulse.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gympulse.app.data.entity.TrainingLog
import com.gympulse.app.ui.common.*
import com.gympulse.app.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToLog: () -> Unit,
    onOpenSettings: () -> Unit,
    onWorkoutSaved: () -> Unit,
    viewModel: HomeViewModel = viewModel()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 问候语 + 标题 ──
            GreetingHeader(
                greeting = uiState.greeting,
                headingText = uiState.headingText,
                showCheckmark = uiState.showCheckmark,
                onOpenSettings = onOpenSettings
            )

            // ── 部位选择卡片 ──
            BodyPartPickerCard(
                selectedParts = uiState.selectedParts,
                cardLabel = uiState.cardLabel,
                hasSelection = uiState.hasSelection,
                todayLogged = uiState.todayLogged,
                saveSuccess = uiState.saveSuccess,
                buttonText = uiState.buttonText,
                buttonEnabled = uiState.buttonEnabled,
                onTogglePart = { viewModel.togglePart(it) },
                onClear = { viewModel.clearSelection() },
                onSave = {
                    viewModel.saveTodayLog()
                    onWorkoutSaved()
                }
            )

            // ── 最近记录 ──
            SectionLabel(text = "最近记录")
            RecentLogsList(
                logs = uiState.recentLogs,
                hasAnyLogs = uiState.hasAnyLogs,
                onClick = onNavigateToLog
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun GreetingHeader(
    greeting: String,
    headingText: String,
    showCheckmark: Boolean,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    letterSpacing = 0.04.sp
                ),
                color = ForgeMuted,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (showCheckmark) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = headingText,
                        style = MaterialTheme.typography.displayMedium,
                        color = ForgeFg
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                        color = ForgeSuccess
                    )
                }
            } else {
                Text(
                    text = headingText,
                    style = MaterialTheme.typography.displayMedium,
                    color = ForgeFg
                )
            }
        }
        // 设置入口
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ForgeSurface)
                .border(1.dp, ForgeBorder, RoundedCornerShape(8.dp))
                .clickable { onOpenSettings() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚙",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                color = ForgeMuted
            )
        }
    }
}

@Composable
private fun BodyPartPickerCard(
    selectedParts: Map<String, Boolean>,
    cardLabel: String,
    hasSelection: Boolean,
    todayLogged: Boolean,
    saveSuccess: Boolean,
    buttonText: String,
    buttonEnabled: Boolean,
    onTogglePart: (String) -> Unit,
    onClear: () -> Unit,
    onSave: () -> Unit
) {
    val selectedCount = selectedParts.count { it.value }

    // 卡片背景色动画
    val cardBg = if (hasSelection) AccentOnBg06 else ForgeSurface
    val cardBorder = if (hasSelection) AccentOnBg18 else ForgeBorder
    val cardBgAnim by animateColorAsState(cardBg, label = "card_bg")
    val borderColorAnim by animateColorAsState(cardBorder, label = "card_border")

    // 保存成功绿色闪动
    var flashShadow by remember { mutableStateOf(false) }
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            flashShadow = true
            kotlinx.coroutines.delay(800)
            flashShadow = false
        }
    }
    val shadowColor by animateColorAsState(
        if (flashShadow) ForgeSuccess.copy(alpha = 0.25f) else Color.Transparent,
        label = "shadow"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = cardBgAnim,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColorAnim),
        shadowElevation = if (flashShadow) 0.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // 标签
            Text(
                text = cardLabel,
                style = MaterialTheme.typography.labelSmall,
                color = ForgeMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            // 标题
            if (hasSelection) {
                Text(
                    text = buildString {
                        append("$selectedCount")
                        append(" 个部位")
                        if (todayLogged) append(" · 已记录")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForgeFg
                )
            } else {
                Text(
                    text = if (todayLogged) "今日未记录" else "点击选择部位",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForgeFg
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2 列部位网格
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BodyParts.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { part ->
                            PartChip(
                                part = part,
                                isSelected = selectedParts[part.name] == true,
                                onClick = { onTogglePart(part.name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSave,
                    enabled = buttonEnabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForgeAccent,
                        contentColor = ForgeOnAccent,
                        disabledContainerColor = ForgeAccent.copy(alpha = 0.35f),
                        disabledContentColor = ForgeOnAccent.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(vertical = 13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // 清除按钮
                if (hasSelection) {
                    OutlinedButton(
                        onClick = onClear,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ForgeSurface,
                            contentColor = ForgeMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder),
                        contentPadding = PaddingValues(vertical = 13.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "清除",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentLogsList(
    logs: List<TrainingLog>,
    hasAnyLogs: Boolean,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (!hasAnyLogs || logs.isEmpty()) {
            Text(
                "还没有训练记录",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = ForgeMuted,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        } else {
            logs.forEach { log ->
                RecentLogItem(
                    log = log,
                    onClick = onClick
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentLogItem(
    log: TrainingLog,
    onClick: () -> Unit
) {
    val parts = log.partsList()
    val dateDisplay = remember(log.date) {
        try {
            val d = log.date.substring(5).replace("-", "/") // "06/10"
            d
        } catch (e: Exception) { log.date }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 部位标签 (中性 surface2 底色 + 边框)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                parts.forEach { part ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = ForgeSurface2,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
                    ) {
                        Text(
                            text = part,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = ForgeFg,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            // 日期
            Text(
                text = dateDisplay,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = ForgeMuted,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
