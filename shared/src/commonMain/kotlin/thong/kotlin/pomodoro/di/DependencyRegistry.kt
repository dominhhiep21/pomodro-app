package thong.kotlin.pomodoro.di

import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.settings.Settings
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.core.notification.NotificationManager
import thong.kotlin.pomodoro.core.pomodoro.mini_client.KtorPomodoroMiniClient
import thong.kotlin.pomodoro.database.AuraDatabase
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.LocalSettingsDataSourceV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryImplV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.data.LearningSessionRepository
import thong.kotlin.pomodoro.features.session.data.LearningSessionRepositoryImpl
import thong.kotlin.pomodoro.features.streak.data.StreakRepositoryImpl
import thong.kotlin.pomodoro.features.streak.domain.StreakRepository

object DependencyRegistry {

    private var _database: AuraDatabase? = null

    fun initDatabase(driver: SqlDriver) {
        if (_database == null) {
            _database = AuraDatabase(driver)
        }
    }

    private var _soundManager: SoundManager? = null
    val soundManager: SoundManager?
        get() = _soundManager

    fun initSoundManager(soundManager: SoundManager?) {
        if (_soundManager == null) {
            _soundManager = soundManager
        }
    }

    private var _notificationManager: NotificationManager? = null
    val notificationManager: NotificationManager?
        get() = _notificationManager

    fun initNotificationManager(notificationManager: NotificationManager?) {
        if (_notificationManager == null) {
            _notificationManager = notificationManager
        }
    }

    private val settings: Settings by lazy {
        Settings()
    }

    private val localSettingsDataSource: LocalSettingsDataSourceV2 by lazy {
        LocalSettingsDataSourceV2(settings)
    }

    val userAppStateRepositoryV2: UserAppStateRepositoryV2 by lazy {
        UserAppStateRepositoryImplV2(localSettingsDataSource)
    }

    val streakRepository: StreakRepository by lazy {
        StreakRepositoryImpl(Settings())
    }

    val ktorPomodoroMiniClient: KtorPomodoroMiniClient by lazy {
        KtorPomodoroMiniClient(AppConfig.DEFAULT_POMODORO_MINI_SERVER_URL)
    }

    private val localLearningSessionDataSource by lazy {
        LocalSettingsDataSourceV2(settings)
    }

    val learningSessionRepository: LearningSessionRepository by lazy {
        LearningSessionRepositoryImpl(localLearningSessionDataSource, _database)
    }

    val learningSessionManager: LearningSessionManager by lazy {
        LearningSessionManager(learningSessionRepository)
    }
}
