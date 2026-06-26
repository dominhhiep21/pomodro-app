# API Documentation

## Network API (KtorPomodoroMiniClient)

Client kết nối tới Pomodoro Mini Server.

### Endpoints

| Method | Path | Purpose |
|--------|------|---------|
| POST | /register | Đăng ký user mới |
| GET | /tasks | Lấy danh sách tasks |
| GET | /settings | Lấy user settings |
| PUT | /settings | Cập nhật settings |
| GET | /stats | Lấy daily statistics |
| POST | /stats/increment | Tăng daily stats |

### Data Models
- **UserRegisterRequest**: Thông tin đăng ký
- **ErrorResponse**: Lỗi từ server

## Internal APIs (Repositories)

### UserAppStateRepository (V1)
```kotlin
interface UserAppStateRepository {
    fun getUserSettings(): UserSettings
    fun saveUserSettings(settings: UserSettings)
    fun getSettingsFlow(): Flow<UserSettings>
    fun getAllTasks(): List<PomodoroTask>
    fun saveTask(task: PomodoroTask)
    fun deleteTask(taskId: String)
    fun updateTaskStatus(taskId: String, completed: Boolean)
    fun getAllSessions(): List<PomodoroSession>
    fun saveSession(session: PomodoroSession)
    fun getTodayStats(): DailyStats
    fun updateDailyStats(stats: DailyStats)
    fun incrementDailyStats(...)
    fun clearAllData()
}
```

### UserAppStateRepositoryV2
```kotlin
interface UserAppStateRepositoryV2 {
    fun getUserSettings(): UserSettingsV2
    fun getSettingsFlow(): Flow<UserSettingsV2>
    fun saveUserSettings(settings: UserSettingsV2)
    fun updateUserSettings(update: (UserSettingsV2) -> UserSettingsV2)
    fun markOnboardingCompleted()
    fun updateSelectedBackground(background: String)
    fun resetUserSettings()
}
```

### SoundManager (Platform Interface)
```kotlin
interface SoundManager {
    fun playSound(soundRes: String)
    fun stopSound()
    fun setVolume(volume: Float)
}
```

### NotificationManager (Platform Interface)
```kotlin
interface NotificationManager {
    fun showNotification(title: String, message: String)
}
```
