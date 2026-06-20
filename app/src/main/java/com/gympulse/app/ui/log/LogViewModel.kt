package com.gympulse.app.ui.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gympulse.app.data.TrainingRepository
import com.gympulse.app.data.entity.TrainingLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LogUiState(
    val allLogs: List<TrainingLog> = emptyList(),
    val availableMonths: List<String> = emptyList(),  // "MM" 格式
    val selectedMonth: String = "all",
    val filteredLogs: List<TrainingLog> = emptyList(),
    /** date → 合并后的部位列表 */
    val groupedLogs: Map<String, List<String>> = emptyMap(),
    val totalDays: Int = 0,
    val totalParts: Int = 0,
    val topPart: String = "—",
    val isLoading: Boolean = true
)

class LogViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // 获取可用月份
            val months = repository.getDistinctMonths()
                .map { it.substring(5, 7) }  // "YYYY-MM" → "MM"
            _uiState.update { it.copy(availableMonths = months) }

            // 默认选中当前月份或最近有数据的月份
            val defaultMonth = months.firstOrNull() ?: "all"
            _uiState.update { it.copy(selectedMonth = defaultMonth) }

            // 收集所有记录
            repository.allLogs.collect { logs ->
                applyFilter(logs, _uiState.value.selectedMonth)
            }
        }
    }

    fun selectMonth(month: String) {
        _uiState.update { it.copy(selectedMonth = month) }
        applyFilter(_uiState.value.allLogs, month)
    }

    fun refresh() {
        viewModelScope.launch {
            val logs = repository.getAllLogsOnce()
            val months = repository.getDistinctMonths()
                .map { it.substring(5, 7) }
            _uiState.update { it.copy(availableMonths = months) }
            applyFilter(logs, _uiState.value.selectedMonth)
        }
    }

    private fun applyFilter(allLogs: List<TrainingLog>, month: String) {
        val filtered = if (month == "all") allLogs
        else allLogs.filter { it.date.substring(5, 7) == month }

        // 按日期合并
        val grouped = linkedMapOf<String, MutableSet<String>>()
        filtered.forEach { log ->
            val date = log.date
            if (!grouped.containsKey(date)) grouped[date] = mutableSetOf()
            log.partsList().forEach { grouped[date]!!.add(it) }
        }

        // 排序 (日期倒序)
        val sortedGrouped = grouped.entries
            .sortedByDescending { it.key }
            .associate { it.key to it.value.toList() }

        // 统计
        val totalDays = sortedGrouped.size
        val partCounts = mutableMapOf<String, Int>()
        sortedGrouped.values.forEach { parts ->
            parts.forEach { p -> partCounts[p] = (partCounts[p] ?: 0) + 1 }
        }
        val totalParts = partCounts.values.sum()
        val topPart = partCounts.maxByOrNull { it.value }?.key ?: "—"

        _uiState.update { it.copy(
            allLogs = allLogs,
            filteredLogs = filtered,
            groupedLogs = sortedGrouped,
            totalDays = totalDays,
            totalParts = totalParts,
            topPart = topPart,
            isLoading = false
        ) }
    }

    fun deleteByDate(date: String) {
        viewModelScope.launch {
            repository.deleteDayLog(date)
            refresh()
        }
    }

    class Factory(private val repository: TrainingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LogViewModel(repository) as T
        }
    }
}
