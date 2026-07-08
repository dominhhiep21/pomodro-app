package thong.kotlin.pomodoro.core.notification.toast

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppToastType {
    SUCCESS, ERROR, WARNING, INFO
}

data class AppToastMessage(
    val message: String,
    val type: AppToastType = AppToastType.INFO,
    val durationMillis: Long = 2_000L
)

class AppToastHostState {
    private val _toast = MutableStateFlow<AppToastMessage?>(null)
    val toast: StateFlow<AppToastMessage?> = _toast

    suspend fun showToast(
        message: String,
        type: AppToastType = AppToastType.INFO,
        durationMillis: Long = 2_000L
    ) {
        _toast.value = AppToastMessage(message, type, durationMillis)
        delay(durationMillis)
        _toast.value = null
    }

    fun dismiss() {
        _toast.value = null
    }
}