package com.gympulse.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 训练记录实体 — 对应设计中一条 localStorage 记录。
 *
 * parts 存储为逗号分隔的部位名称，避免 JSON 序列化的复杂度。
 * 例如: "胸部,肱三头"
 */
@Entity(tableName = "training_logs")
data class TrainingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 日期字符串 YYYY-MM-DD */
    @ColumnInfo(name = "date")
    val date: String,

    /** 逗号分隔的训练部位名称，如 "胸部,肱三头" */
    @ColumnInfo(name = "parts")
    val parts: String,

    /** 训练时间戳 (epoch millis) */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    /** 拆分为部位名称列表 */
    fun partsList(): List<String> =
        if (parts.isBlank()) emptyList() else parts.split(",").map { it.trim() }

    companion object {
        fun fromPartsList(date: String, parts: List<String>, timestamp: Long = System.currentTimeMillis()): TrainingLog =
            TrainingLog(date = date, parts = parts.joinToString(","), timestamp = timestamp)
    }
}
