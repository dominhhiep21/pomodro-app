package thong.kotlin.pomodoro.di

import com.russhwolf.settings.Settings
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.pomodoro.mini_client.KtorPomodoroMiniClient
import thong.kotlin.pomodoro.features.pomodoro._base.data.local.LocalSettingsDataSource
import thong.kotlin.pomodoro.features.pomodoro._base.data.repository.UserAppStateRepositoryImpl
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.LocalSettingsDataSourceV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepository
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryImplV2
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2

object DependencyRegistry {

    private val settings: Settings by lazy {
        Settings()
    }

    private val localSettingsDataSource: LocalSettingsDataSourceV2 by lazy {
        LocalSettingsDataSourceV2(settings)
    }

    private val localSettings by lazy { LocalSettingsDataSource(Settings()) }

    val userAppStateRepository: UserAppStateRepository by lazy {
        UserAppStateRepositoryImpl(localSettings)
    }

    val userAppStateRepositoryV2: UserAppStateRepositoryV2 by lazy {
        UserAppStateRepositoryImplV2(localSettingsDataSource)
    }

    val ktorPomodoroMiniClient: KtorPomodoroMiniClient by lazy {
        KtorPomodoroMiniClient(AppConfig.DEFAULT_POMODORO_MINI_SERVER_URL)
    }
}
