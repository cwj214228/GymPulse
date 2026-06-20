package com.gympulse.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 今日训练部位选择的持久化 — 对应 HTML 原型中的 ts_today_* localStorage key。
 * 即使用户未点"保存记录"，关闭 app 后选择仍保留。
 */
class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("gympulse_today", Context.MODE_PRIVATE)

    /**
     * 保存今日部位选择 (逗号分隔的部位名称)
     */
    fun saveTodaySelection(date: String, parts: Set<String>) {
        prefs.edit()
            .putString("date", date)
            .putString("parts", parts.joinToString(","))
            .apply()
    }

    /**
     * 读取今日部位选择
     * @return Pair<date, parts> 如果日期匹配，否则 null
     */
    fun getTodaySelection(todayDate: String): Set<String>? {
        val savedDate = prefs.getString("date", null) ?: return null
        if (savedDate != todayDate) return null
        val partsStr = prefs.getString("parts", "") ?: ""
        return partsStr.split(",").filter { it.isNotBlank() }.toSet()
    }

    /**
     * 清除今日选择
     */
    fun clearTodaySelection() {
        prefs.edit().clear().apply()
    }
}
