package com.gympulse.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gympulse.app.data.TrainingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class StatsUiState(
    val totalDays: Int = 0,
    val totalParts: Int = 0,
    val topPart: String = "—",
    val topPartCount: Int = 0,
    val avgPartsPerDay: String = "—",
    /** 部位名称 → 次数 (用于条形图) */
    val partCounts: Map<String, Int> = emptyMap(),
    /** date → 部位数 (用于日历热力图) */
    val calendarData: Map<String, Int> = emptyMap(),
    val daysInMonth: Int = 30,
    val currentYearMonth: String = "",
    val isLoading: Boolean = true
)

class StatsViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val now = LocalDate.now()
            _uiState.update { it.copy(currentYearMonth = "${now.year}-${String.format("%02d", now.monthValue)}") }

            repository.allLogs.collect { logs ->
                computeStats(logs)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val logs = repository.getAllLogsOnce()
            computeStats(logs)
        }
    }

    private fun computeStats(allLogs: List<com.gympulse.app.data.entity.TrainingLog>) {
        val totalDays = allLogs.size

        // 部位频次统计
        val partCounts = mutableMapOf<String, Int>()
        val datePartCounts = mutableMapOf<String, Int>()

        allLogs.forEach { log ->
            val parts = log.partsList()
            datePartCounts[log.date] = (datePartCounts[log.date] ?: 0) + parts.size
            parts.forEach { p ->
                partCounts[p] = (partCounts[p] ?: 0) + 1
            }
        }

        val totalParts = partCounts.values.sum()
        val topEntry = partCounts.maxByOrNull { it.value }
        val topPart = topEntry?.key ?: "—"
        val topPartCount = topEntry?.value ?: 0
        val avg = if (totalDays > 0) "%.1f".format(totalParts.toFloat() / totalDays) else "—"

        // 日历热力图 (当月)
        val now = LocalDate.now()
        val yearMonth = YearMonth.of(now.year, now.month)
        val daysInMonth = yearMonth.lengthOfMonth()
        val calendarData = mutableMapOf<String, Int>()
        for (day in 1..daysInMonth) {
            val dateStr = "${now.year}-${String.format("%02d", now.monthValue)}-${String.format("%02d", day)}"
            calendarData[dateStr] = datePartCounts[dateStr] ?: 0
        }

        _uiState.update { it.copy(
            totalDays = totalDays,
            totalParts = totalParts,
            topPart = topPart,
            topPartCount = topPartCount,
            avgPartsPerDay = avg,
            partCounts = partCounts,
            calendarData = calendarData,
            daysInMonth = daysInMonth,
            currentYearMonth = "${now.year}-${String.format("%02d", now.monthValue)}",
            isLoading = false
        ) }
    }

    class Factory(private val repository: TrainingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StatsViewModel(repository) as T
        }
    }
}
