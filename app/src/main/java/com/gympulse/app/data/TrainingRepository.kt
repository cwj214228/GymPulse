package com.gympulse.app.data

import com.gympulse.app.data.dao.DatePartsProjection
import com.gympulse.app.data.dao.TrainingLogDao
import com.gympulse.app.data.entity.TrainingLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrainingRepository(private val dao: TrainingLogDao) {

    /** 所有记录 Flow */
    val allLogs: Flow<List<TrainingLog>> = dao.getAllLogs()

    /** 获取所有记录 (一次性) */
    suspend fun getAllLogsOnce(): List<TrainingLog> = dao.getAllLogsOnce()

    /** 按月份获取记录 */
    fun getLogsByMonth(month: String): Flow<List<TrainingLog>> = dao.getLogsByMonth(month)

    /** 保存某天的训练记录 (先删后插，实现替换语义) */
    suspend fun saveDayLog(date: String, parts: List<String>) {
        dao.deleteByDate(date)
        if (parts.isNotEmpty()) {
            dao.insert(TrainingLog.fromPartsList(date, parts))
        }
    }

    /** 获取某天的部位列表 */
    suspend fun getPartsForDate(date: String): List<String> {
        return dao.getLogsByDate(date).flatMap { it.partsList() }
    }

    /** 获取有记录的所有月份 (YYYY-MM) */
    suspend fun getDistinctMonths(): List<String> = dao.getDistinctMonths()

    /** 获取日期 → 部位数的映射 */
    suspend fun getDatePartCountMap(): Map<String, Int> {
        return dao.getAllDatesAndParts()
            .associate { it.date to it.parts.split(",").filter { p -> p.isNotBlank() }.size }
    }

    /** 删除某天的所有训练记录 */
    suspend fun deleteDayLog(date: String) {
        dao.deleteByDate(date)
    }

    /** 批量替换所有记录 (用于导入) */
    suspend fun replaceAll(logs: List<TrainingLog>) {
        val existing = dao.getAllLogsOnce()
        existing.forEach { dao.deleteByDate(it.date) }
        logs.forEach { dao.insert(it) }
    }
}
