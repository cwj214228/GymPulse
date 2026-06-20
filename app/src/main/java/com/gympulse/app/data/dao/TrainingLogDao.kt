package com.gympulse.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gympulse.app.data.entity.TrainingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingLogDao {

    /** 按日期倒序获取所有记录 */
    @Query("SELECT * FROM training_logs ORDER BY date DESC, timestamp DESC")
    fun getAllLogs(): Flow<List<TrainingLog>>

    /** 一次性获取所有记录 (用于统计计算) */
    @Query("SELECT * FROM training_logs ORDER BY date DESC, timestamp DESC")
    suspend fun getAllLogsOnce(): List<TrainingLog>

    /** 获取某个月份的记录 (月份格式 MM，如 "06") */
    @Query("SELECT * FROM training_logs WHERE substr(date, 6, 2) = :month ORDER BY date DESC, timestamp DESC")
    fun getLogsByMonth(month: String): Flow<List<TrainingLog>>

    /** 获取某日的记录 */
    @Query("SELECT * FROM training_logs WHERE date = :date")
    suspend fun getLogsByDate(date: String): List<TrainingLog>

    /** 插入记录 (如已有同日不同时间的记录，保留两者；如同日同parts则替换) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: TrainingLog): Long

    /** 删除某日的所有记录 */
    @Query("DELETE FROM training_logs WHERE date = :date")
    suspend fun deleteByDate(date: String)

    /** 获取包含训练记录的所有月份 (去重，倒序) */
    @Query("SELECT DISTINCT substr(date, 1, 7) AS month FROM training_logs ORDER BY month DESC")
    suspend fun getDistinctMonths(): List<String>

    /** 获取所有日期及该日的部位数 */
    @Query("SELECT date, parts FROM training_logs ORDER BY date DESC")
    suspend fun getAllDatesAndParts(): List<DatePartsProjection>
}

/** 投影 — 只取 date + parts 两列，避免加载整条记录 */
data class DatePartsProjection(
    val date: String,
    val parts: String
)
