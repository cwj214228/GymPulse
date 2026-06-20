package com.gympulse.app.ui.log

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import com.gympulse.app.ui.common.StatusBar
import com.gympulse.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogScreen(
    viewModel: LogViewModel = viewModel()
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

    // 删除确认对话框
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = ForgeSurface,
            title = { Text("删除记录", color = ForgeFg) },
            text = { Text("确定要删除 ${deleteTarget} 的训练记录吗？此操作不可恢复。", color = ForgeMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteByDate(deleteTarget!!)
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeDanger)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deleteTarget = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
                ) {
                    Text("取消", color = ForgeMuted)
                }
            }
        )
    }

    Scaffold(
        containerColor = ForgeBlack,
        topBar = {}
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "训练记录",
                style = MaterialTheme.typography.headlineLarge,
                color = ForgeFg,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // 月份筛选
            MonthFilter(
                months = uiState.availableMonths,
                selectedMonth = uiState.selectedMonth,
                onSelect = { viewModel.selectMonth(it) }
            )

            // 统计摘要
            SummaryRow(
                totalDays = uiState.totalDays,
                totalParts = uiState.totalParts,
                topPart = uiState.topPart
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("加载中…", style = MaterialTheme.typography.labelMedium, color = ForgeMuted)
                }
            } else if (uiState.groupedLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无记录", style = MaterialTheme.typography.labelMedium, color = ForgeMuted)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.groupedLogs.forEach { (date, parts) ->
                        LogListItem(
                            date = date,
                            parts = parts,
                            onLongClick = { deleteTarget = date }
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthFilter(
    months: List<String>,
    selectedMonth: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        months.forEach { month ->
            val label = "${month.toIntOrNull() ?: month}月"
            val isActive = month == selectedMonth
            Surface(
                modifier = Modifier.clickable { onSelect(month) },
                shape = RoundedCornerShape(100.dp),
                color = if (isActive) ForgeFg else ForgeSurface,
                border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder) else null
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = if (isActive) ForgeBlack else ForgeMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }
        }
        val allActive = selectedMonth == "all"
        Surface(
            modifier = Modifier.clickable { onSelect("all") },
            shape = RoundedCornerShape(100.dp),
            color = if (allActive) ForgeFg else ForgeSurface,
            border = if (!allActive) androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder) else null
        ) {
            Text(
                text = "全部",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = if (allActive) ForgeBlack else ForgeMuted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun SummaryRow(totalDays: Int, totalParts: Int, topPart: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryStat(value = if (totalDays >= 0) "$totalDays" else "—", label = "总天数", modifier = Modifier.weight(1f))
        SummaryStat(value = if (totalParts >= 0) "$totalParts" else "—", label = "总次数", modifier = Modifier.weight(1f))
        SummaryStat(value = topPart, label = "最爱练", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SummaryStat(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = ForgeFg, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = ForgeMuted)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LogListItem(date: String, parts: List<String>, onLongClick: () -> Unit) {
    val weekdays = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
    val dateDisplay = remember(date) {
        try {
            val ld = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
            "${ld.monthValue}月${ld.dayOfMonth}日 ${weekdays[ld.dayOfWeek.value % 7]}"
        } catch (e: Exception) { date }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = dateDisplay,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = ForgeMuted,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(76.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                parts.forEach { part ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = AccentOnBg10,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOnBg20)
                    ) {
                        Text(
                            text = part,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            color = ForgeAccent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
