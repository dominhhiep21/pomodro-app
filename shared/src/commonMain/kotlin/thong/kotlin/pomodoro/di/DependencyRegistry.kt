package thong.kotlin.pomodoro.di

import com.russhwolf.settings.Settings
import thong.kotlin.pomodoro.features.pomodoro._base.data.local.LocalSettingsDataSource
import thong.kotlin.pomodoro.features.pomodoro._base.data.repository.UserAppStateRepositoryImpl
import thong.kotlin.pomodoro.features.pomodoro.domain.repository.UserAppStateRepository

object DependencyRegistry {
    private val localSettings by lazy { LocalSettingsDataSource(Settings()) }

    val userAppStateRepository: UserAppStateRepository by lazy {
        UserAppStateRepositoryImpl(localSettings)
    }
}
