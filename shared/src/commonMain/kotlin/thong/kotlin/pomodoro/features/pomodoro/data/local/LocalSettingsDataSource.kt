package thong.kotlin.pomodoro.features.pomodoro.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import thong.kotlin.pomodoro.features.pomodoro.domain.model.LearningStyle
import thong.kotlin.pomodoro.features.pomodoro.domain.model.UserSettings
import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.Task
import thong.kotlin.pomodoro.features.pomodoro.domain.model.DailyStats
import kotlinx.serialization.json.Json

class LocalSettingsDataSource(private val settings: Settings) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_WORK_MINUTES = "work_minutes"
        private const val KEY_BREAK_MINUTES = "break_minutes"
        private const val KEY_LONG_BREAK_MINUTES = "long_break_minutes"
        private const val KEY_AUTO_START_BREAK = "auto_start_break"
        private const val KEY_AUTO_START_WORK = "auto_start_work"
        private const val KEY_SELECTED_BACKGROUND_ID = "selected_background_id"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_COMPACT_MODE = "compact_mode"
        private const val KEY_MINIMAL_MODE = "minimal_mode"
        private const val KEY_BATTERY_SAVER = "battery_saver"
        private const val KEY_MAX_GROUP_SIZE = "max_group_size"
        private const val KEY_LEARNING_STYLE = "learning_style"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        private const val KEY_TASKS = "tasks_json"
        private const val KEY_DAILY_STATS = "daily_stats_json"
    }

    fun getUserSettings(): UserSettings {
        return UserSettings(
            workMinutes = settings.getInt(KEY_WORK_MINUTES, 25),
            breakMinutes = settings.getInt(KEY_BREAK_MINUTES, 5),
            longBreakMinutes = settings.getInt(KEY_LONG_BREAK_MINUTES, 15),
            autoStartBreak = settings.getBoolean(KEY_AUTO_START_BREAK, false),
            autoStartWork = settings.getBoolean(KEY_AUTO_START_WORK, false),
            selectedBackgroundId = settings.getStringOrNull(KEY_SELECTED_BACKGROUND_ID),
            isNotificationEnabled = settings.getBoolean(KEY_NOTIFICATION_ENABLED, true),
            isSoundEnabled = settings.getBoolean(KEY_SOUND_ENABLED, true),
            isVibrationEnabled = settings.getBoolean(KEY_VIBRATION_ENABLED, true),
            isCompactMode = settings.getBoolean(KEY_COMPACT_MODE, false),
            isMinimalMode = settings.getBoolean(KEY_MINIMAL_MODE, false),
            isBatterySaverEnabled = settings.getBoolean(KEY_BATTERY_SAVER, false),
            maxGroupSize = settings.getInt(KEY_MAX_GROUP_SIZE, 4),
            learningStyle = LearningStyle.valueOf(settings.getString(KEY_LEARNING_STYLE, LearningStyle.SOLO.name)),
            hasCompletedOnboarding = settings.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
        )
    }

    fun saveUserSettings(userSettings: UserSettings) {
        settings[KEY_WORK_MINUTES] = userSettings.workMinutes
        settings[KEY_BREAK_MINUTES] = userSettings.breakMinutes
        settings[KEY_LONG_BREAK_MINUTES] = userSettings.longBreakMinutes
        settings[KEY_AUTO_START_BREAK] = userSettings.autoStartBreak
        settings[KEY_AUTO_START_WORK] = userSettings.autoStartWork
        settings[KEY_SELECTED_BACKGROUND_ID] = userSettings.selectedBackgroundId
        settings[KEY_NOTIFICATION_ENABLED] = userSettings.isNotificationEnabled
        settings[KEY_SOUND_ENABLED] = userSettings.isSoundEnabled
        settings[KEY_VIBRATION_ENABLED] = userSettings.isVibrationEnabled
        settings[KEY_COMPACT_MODE] = userSettings.isCompactMode
        settings[KEY_MINIMAL_MODE] = userSettings.isMinimalMode
        settings[KEY_BATTERY_SAVER] = userSettings.isBatterySaverEnabled
        settings[KEY_MAX_GROUP_SIZE] = userSettings.maxGroupSize
        settings[KEY_LEARNING_STYLE] = userSettings.learningStyle.name
        settings[KEY_HAS_COMPLETED_ONBOARDING] = userSettings.hasCompletedOnboarding
    }

    fun saveTasks(tasks: List<Task>) {
        settings[KEY_TASKS] = json.encodeToString(tasks)
    }

    fun getTasks(): List<Task> {
        val jsonString = settings.getStringOrNull(KEY_TASKS) ?: return emptyList()
        return try {
            json.decodeFromString(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveDailyStats(stats: DailyStats) {
        settings[KEY_DAILY_STATS] = json.encodeToString(stats)
    }

    fun getDailyStats(date: String): DailyStats? {
        val jsonString = settings.getStringOrNull(KEY_DAILY_STATS) ?: return null
        return try {
            val stats: DailyStats = json.decodeFromString(jsonString)
            if (stats.date == date) stats else null
        } catch (_: Exception) {
            null
        }
    }

    fun clear() {
        settings.clear()
    }

    fun getSettingsFlow(): Flow<UserSettings> {
        return kotlinx.coroutines.flow.flow {
            emit(getUserSettings())
        }
    }
}
