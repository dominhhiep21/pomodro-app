# API Documentation

## Network API (KtorPomodoroMiniClient)

Client kết nối tới Pomodoro Mini Server.

### Endpoints

| Method | Path | Purpose |
|--------|------|---------|
| POST | /api/users/register | Đăng ký user mới |
| POST | /api/users/login | Đăng nhập |
| GET | /api/users/profile | Lấy profile user |
| GET | /api/users/logout | Đăng xuất |
| GET | /api/settings | Lấy user settings |
| PUT | /api/settings | Cập nhật settings |
| GET | /api/tasks | Lấy danh sách tasks |
| POST | /api/pomodoros/log | Lưu pomodoro log |

### Data Models
- **UserRegisterRequest**: Thông tin đăng ký
- **UserLoginRequest**: Thông tin đăng nhập
- **UserResponse**: Response từ register
- **SettingsRequest**: Cập nhật settings
- **PomodoroLogRequest**: Log phiên pomodoro
- **ErrorResponse**: Lỗi từ server

## Internal APIs (Repositories)

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

### StreakRepository
```kotlin
interface StreakRepository {
    fun getHistoryFlow(): Flow<List<DailyRecord>>
    suspend fun getHistory(): List<DailyRecord>
    suspend fun recordDay(date: String, sessionsCompleted: Int)
    suspend fun incrementToday()
}
```

### LearningSessionRepository
```kotlin
interface LearningSessionRepository {
    fun getCurrentSession(): LearningSessionState
    fun saveCurrentSession(session: LearningSessionState)
    fun clearCurrentSession()
    fun saveCompletedSession(session: LearningSessionState)
    fun getAllLearningSessionRecords(): List<LearningSessionRecord>
    fun getSessionById(sessionId: String): LearningSessionRecord?
    fun getTotalFocusSeconds(): Long
    fun insertSession(session: LearningSessionRecord)
    fun updateSession(session: LearningSessionRecord)
    fun deleteSessionById(sessionId: String)
    fun insertEvent(event: LearningSessionEvent)
    fun getAllTasksBySessionId(sessionId: String): List<SessionTask>
    fun insertTask(task: SessionTask, sessionId: String)
    fun updateTask(task: SessionTask)
    fun deleteTask(taskId: String, sessionId: String)
    fun clearAllSessionsData()
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
expect class NotificationManager {
    fun showNotification(title: String, message: String)
}
```

### PomodoroMiniClient
```kotlin
interface PomodoroMiniClient {
    suspend fun register(userRegisterRequest: UserRegisterRequest): UserResponse
    suspend fun login(userLoginRequest: UserLoginRequest)
    suspend fun profile(token: String)
    suspend fun logout(token: String)
    suspend fun settings(token: String)
    suspend fun updateSettings(token: String, request: SettingsRequest)
    suspend fun tasks(token: String)
    suspend fun saveLog(token: String, logRequest: PomodoroLogRequest)
}
```
