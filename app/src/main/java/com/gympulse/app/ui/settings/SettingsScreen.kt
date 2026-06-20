package com.gympulse.app.ui.settings

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gympulse.app.data.DataManager
import com.gympulse.app.ui.common.SectionLabel
import com.gympulse.app.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsScreen(
    dataManager: DataManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var lastResult by remember { mutableStateOf<DataManager.Result?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val result = dataManager.importFromUri(uri)
                lastResult = result
                val msg = when (result) {
                    is DataManager.Result.Imported -> "已导入 ${result.count} 条记录"
                    is DataManager.Result.Error -> result.message
                    else -> ""
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        containerColor = ForgeBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部 — 返回 + 标题
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForgeMuted,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ForgeFg
                )
            }

            // 数据管理
            SectionLabel(text = "数据管理")
            DataManagerCard(
                onExport = {
                    coroutineScope.launch {
                        val result = dataManager.exportToDownloads()
                        lastResult = result
                        when (result) {
                            is DataManager.Result.Success -> {
                                val uri = dataManager.getShareUri(result.file)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(Intent.EXTRA_SUBJECT, "铁壳训练 - 训练数据备份")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "保存备份文件")
                                )
                            }
                            is DataManager.Result.Error -> {
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
                    }
                },
                onImport = {
                    importLauncher.launch("application/json")
                }
            )

            // 提示
            SectionLabel(text = "关于")
            InfoCard(
                items = listOf(
                    "• 导出文件保存到 Download 目录,文件名 gympulse_backup_*.json",
                    "• 卸载重装后,在 app 内点击「导入」,选之前的 JSON 文件即可恢复",
                    "• 建议每月导出一次并保存到网盘/电脑"
                )
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DataManagerCard(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            ActionRow(
                icon = "↓",
                title = "导出训练记录",
                subtitle = "保存到 Download 目录,可在文件管理器中找到",
                onClick = onExport
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionRow(
                icon = "↑",
                title = "导入训练记录",
                subtitle = "选择之前的 JSON 备份文件,会替换当前数据",
                onClick = onImport
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = ForgeSurface2,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 图标方块
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentOnBg10)
                    .border(1.dp, AccentOnBg18, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForgeAccent
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ForgeFg
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ForgeMuted
                )
            }
            Text(
                text = "→",
                style = MaterialTheme.typography.labelLarge,
                color = ForgeMuted
            )
        }
    }
}

@Composable
private fun InfoCard(items: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ForgeSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ForgeMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
