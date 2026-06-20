package com.gympulse.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gympulse.app.data.PreferenceManager
import com.gympulse.app.data.TrainingRepository
import com.gympulse.app.data.entity.TrainingLog
import com.gympulse.app.ui.common.BodyParts
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val greeting: String = "下午好",
    /** 当前选中的部位设置 (key = 部位名称) */
    val selectedParts: Map<String, Boolean> = emptyMap(),
    /** 今天是否已经保存记录 */
    val todayLogged: Boolean = false,
    /** 今日已保存的部位名称列表 */
    val todayParts: List<String> = emptyList(),
    /** 最近 3 条记录 (用于首页展示) */
    val recentLogs: List<TrainingLog> = emptyList(),
    /** 是否有任何训练记录 */
    val hasAnyLogs: Boolean = false,
    /** 保存成功 (用于触发绿色闪动) */
    val saveSuccess: Boolean = false,
    /** 按钮文案 */
    val buttonText: String = "选择部位后记录",
    /** 按钮是否可用 */
    val buttonEnabled: Boolean = false,
    /** 是否有选中部位 */
    val hasSelection: Boolean = false,
    /** 标题文字 */
    val headingText: String = "今天练什么？",
    /** 卡片标签 */
    val cardLabel: String = "今日训练部位",
    /** 标题是否有完成勾 */
    val showCheckmark: Boolean = false
)

class HomeViewModel(
    private val repository: TrainingRepository,
    private val prefs: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val todayDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        val greeting = when (LocalTime.now().hour) {
            in 0..11 -> "早上好"
            in 12..17 -> "下午好"
            else -> "晚上好"
        }
        _uiState.update { it.copy(greeting = greeting) }

        viewModelScope.launch {
            // 1. 检查今日是否已保存
            val todayParts = repository.getPartsForDate(todayDate)
            val todayLogged = todayParts.isNotEmpty()

            // 2. 恢复未保存的今日选择 (对应 localStorage ts_today_*)
            val savedSelection = prefs.getTodaySelection(todayDate)

            val initialParts = if (todayLogged) {
                // 已保存：显示已保存的部位
                todayParts.associate { it to true }
            } else if (savedSelection != null && savedSelection.isNotEmpty()) {
                // 未保存但有之前的选择：恢复
                savedSelection.associate { it to true }
            } else {
                emptyMap()
            }

            _uiState.update { it.copy(
                todayLogged = todayLogged,
                todayParts = todayParts,
                selectedParts = initialParts,
            )}
            updateDerivedState()

            // 3. 加载最近记录 (包括今日)
            repository.allLogs.collect { logs ->
                val recent = logs.take(3)
                _uiState.update { it.copy(
                    recentLogs = recent,
                    hasAnyLogs = logs.isNotEmpty(),
                    todayLogged = logs.any { it.date == todayDate },
                    todayParts = repository.getPartsForDate(todayDate)
                ) }
                updateDerivedState()
            }
        }
    }

    fun togglePart(partName: String) {
        _uiState.update { state ->
            val newSelection = state.selectedParts.toMutableMap()
            if (newSelection[partName] == true) {
                newSelection.remove(partName)
            } else {
                newSelection[partName] = true
            }
            state.copy(selectedParts = newSelection)
        }
        // 持久化未保存的选择
        persistSelection()
        updateDerivedState()
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedParts = emptyMap()) }
        prefs.clearTodaySelection()
        updateDerivedState()
    }

    fun saveTodayLog() {
        val parts = _uiState.value.selectedParts.filter { it.value }.keys.toList()
        if (parts.isEmpty()) return

        viewModelScope.launch {
            repository.saveDayLog(todayDate, parts)
            _uiState.update { it.copy(
                todayLogged = true,
                todayParts = parts,
                saveSuccess = true
            )}
            // 持久化当前选择
            prefs.saveTodaySelection(todayDate, parts.toSet())
            updateDerivedState()

            // 2 秒后重置 success 标志 + 重新启用按钮
            delay(2000)
            _uiState.update { it.copy(saveSuccess = false) }
            updateDerivedState()
        }
    }

    fun refresh() {
        val greeting = when (LocalTime.now().hour) {
            in 0..11 -> "早上好"
            in 12..17 -> "下午好"
            else -> "晚上好"
        }

        viewModelScope.launch {
            val todayParts = repository.getPartsForDate(todayDate)
            val todayLogged = todayParts.isNotEmpty()
            val savedSelection = prefs.getTodaySelection(todayDate)

            val parts = if (todayLogged) {
                todayParts.associate { it to true }
            } else if (savedSelection != null && savedSelection.isNotEmpty()) {
                savedSelection.associate { it to true }
            } else {
                emptyMap()
            }

            _uiState.update { it.copy(
                greeting = greeting,
                todayLogged = todayLogged,
                todayParts = todayParts,
                selectedParts = parts
            )}
            updateDerivedState()
        }
    }

    /** 获取选中的部位名称列表，用于跳转到确认页 */
    fun selectedPartsNames(): List<String> {
        return _uiState.value.selectedParts.filter { it.value }.keys.toList()
    }

    private fun persistSelection() {
        val parts = _uiState.value.selectedParts.filter { it.value }.keys
        if (parts.isEmpty()) {
            prefs.clearTodaySelection()
        } else {
            prefs.saveTodaySelection(todayDate, parts)
        }
    }

    private fun updateDerivedState() {
        val state = _uiState.value
        val hasSelection = state.selectedParts.any { it.value }
        val count = state.selectedParts.count { it.value }

        val headingText = if (state.todayLogged) "今日训练"
        else "今天练什么？"

        val showCheckmark = state.todayLogged

        val cardLabel = if (state.todayLogged) "已记录部位" else "今日训练部位"

        val buttonText = when {
            state.saveSuccess -> "✓ 已记录"
            !hasSelection && state.todayLogged -> "选择部位后记录"
            !hasSelection -> "选择部位后记录"
            state.todayLogged -> "修改记录"
            else -> "保存记录"
        }

        val buttonEnabled = hasSelection && !state.saveSuccess

        _uiState.update { it.copy(
            hasSelection = hasSelection,
            headingText = headingText,
            showCheckmark = showCheckmark,
            cardLabel = cardLabel,
            buttonText = buttonText,
            buttonEnabled = buttonEnabled
        ) }
    }

    class Factory(
        private val repository: TrainingRepository,
        private val prefs: PreferenceManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository, prefs) as T
        }
    }
}
