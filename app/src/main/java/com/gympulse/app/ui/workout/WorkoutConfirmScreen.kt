package com.gympulse.app.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gympulse.app.ui.common.BigCheckCircle
import com.gympulse.app.ui.common.StatusBar
import com.gympulse.app.ui.theme.*

/**
 * 训练确认页 (对应 03-workout.html)
 * 保存训练记录后的确认反馈页面。
 * 点击任意位置即可关闭返回首页。
 */
@Composable
fun WorkoutConfirmScreen(
    savedParts: List<String>,
    onBackToHome: () -> Unit
) {
    Scaffold(
        containerColor = ForgeBlack,
        topBar = {}
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackToHome() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BigCheckCircle()

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "今日已记录",
                    style = MaterialTheme.typography.displaySmall,
                    color = ForgeFg
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (savedParts.isNotEmpty())
                        "${savedParts.size} 个部位"
                    else
                        "返回首页选择部位",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Default
                    ),
                    color = ForgeMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (savedParts.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        savedParts.forEach { partName ->
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = AccentOnBg10,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    AccentOnBg20
                                )
                            ) {
                                Text(
                                    text = partName,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = ForgeAccent,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "点击任意位置返回首页",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = ForgeMuted.copy(alpha = 0.6f)
                )
            }
        }
    }
}
