package com.gympulse.app.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.gympulse.app.data.entity.TrainingLog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 训练数据导入/导出管理器
 *
 * 把所有训练记录序列化为 JSON 格式,写入用户可见的 Download 目录:
 *   /sdcard/Download/gympulse_backup_yyyyMMdd_HHmmss.json
 *
 * 重装 app 后,用户可以:
 * 1. 用文件管理器找到该文件
 * 2. 在 app 内「导入」选择该文件,数据即可恢复
 */
class DataManager(
    private val context: Context,
    private val repository: TrainingRepository
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class Backup(
        val version: Int = 1,
        val exportedAt: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
        val count: Int,
        val logs: List<BackupEntry>
    )

    data class BackupEntry(
        val date: String,
        val parts: String,
        val timestamp: Long
    )

    sealed class Result {
        data class Success(val file: File) : Result()
        data class Imported(val count: Int) : Result()
        data class Error(val message: String) : Result()
    }

    /**
     * 导出所有训练记录到 Download 目录,返回文件对象。
     */
    suspend fun exportToDownloads(): Result {
        return try {
            val logs = repository.getAllLogsOnce()
            val entries = logs.map { BackupEntry(it.date, it.parts, it.timestamp) }
            val backup = Backup(count = entries.size, logs = entries)

            val timestamp = java.text.SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                java.util.Locale.US
            ).format(java.util.Date())
            val fileName = "gympulse_backup_$timestamp.json"

            // 使用 MediaStore 兼容的 Download 目录 (Android 10+)
            val resolver = context.contentResolver
            val downloadDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            val file = File(downloadDir, fileName)
            FileWriter(file).use { writer ->
                gson.toJson(backup, writer)
            }
            Result.Success(file)
        } catch (e: Exception) {
            Result.Error("导出失败: ${e.message ?: e.javaClass.simpleName}")
        }
    }

    /**
     * 从 URI 导入 JSON 文件
     */
    suspend fun importFromUri(uri: Uri): Result {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: return Result.Error("无法读取文件")

            val backup = gson.fromJson(json, Backup::class.java)
                ?: return Result.Error("JSON 解析失败")

            val logs = backup.logs.map { entry ->
                TrainingLog(
                    date = entry.date,
                    parts = entry.parts,
                    timestamp = entry.timestamp
                )
            }
            repository.replaceAll(logs)
            Result.Imported(logs.size)
        } catch (e: Exception) {
            Result.Error("导入失败: ${e.message ?: e.javaClass.simpleName}")
        }
    }

    /**
     * 获取导出文件的分享 URI (用于系统的"分享"对话框)
     */
    fun getShareUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
