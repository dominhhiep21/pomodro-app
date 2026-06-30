package thong.kotlin.pomodoro.features.pomodoro.viewmodel

import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.pomodoro._base.domain.repository.UserAppStateRepositoryV2
import thong.kotlin.pomodoro.features.session.data.LearningSessionManager
import thong.kotlin.pomodoro.features.session.domain.LearningSessionRecord

class AppViewModel(
    private val soundManager: SoundManager? = DependencyRegistry.soundManager,
    private val repository: UserAppStateRepositoryV2 = DependencyRegistry.userAppStateRepositoryV2,
    private val learningSessionManager: LearningSessionManager = DependencyRegistry.learningSessionManager,
    private val currentSession: LearningSessionRecord
) {

}