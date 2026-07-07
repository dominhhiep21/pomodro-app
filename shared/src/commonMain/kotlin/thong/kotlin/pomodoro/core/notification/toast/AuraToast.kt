package thong.kotlin.pomodoro.core.notification.toast

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuraToast {
    private var hostState: AppToastHostState? = null

    fun attach(state: AppToastHostState) {
        hostState = state
    }

    fun detach() {
        hostState = null
    }

    fun show(
        message: String,
        type: AppToastType = AppToastType.INFO,
        durationMillis: Long = 2_000L
    ) {
        val state = hostState ?: return

        CoroutineScope(Dispatchers.Main).launch {
            state.showToast(
                message = message,
                type = type,
                durationMillis = durationMillis
            )
        }
    }

    fun showSuccess(message: String) {
        show(message, AppToastType.SUCCESS)
    }

    fun showError(message: String) {
        show(message, AppToastType.ERROR)
    }

    fun showWarning(message: String) {
        show(message, AppToastType.WARNING)
    }

    fun showInfo(message: String) {
        show(message, AppToastType.INFO)
    }
}