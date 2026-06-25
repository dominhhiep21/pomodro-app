package thong.kotlin.pomodoro.di

import com.russhwolf.settings.Settings
import thong.kotlin.pomodoro.core.config.AppConfig
import thong.kotlin.pomodoro.core.pomodoro.mini_client.KtorPomodoroMiniClient
import thong.kotlin.pomodoro.features.pomodoro._base.data.local.LocalSettingsDataSource
import thong.kotlin.pomodoro.features.pomodoro._base.data.repository.UserAppStateRepositoryImpl
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepository

object DependencyRegistry {
    private val localSettings by lazy { LocalSettingsDataSource(Settings()) }

    val userAppStateRepository: UserAppStateRepository by lazy {
        UserAppStateRepositoryImpl(localSettings)
    }

    val ktorPomodoroMiniClient: KtorPomodoroMiniClient by lazy {
        KtorPomodoroMiniClient(AppConfig.DEFAULT_POMODORO_MINI_SERVER_URL)
    }
}
