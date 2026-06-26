package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import app.rive.runtime.kotlin.compose.Rive
import app.rive.runtime.kotlin.compose.rememberRiveFile
import app.rive.runtime.kotlin.compose.rememberRiveWorker
import app.rive.runtime.kotlin.core.RiveFileSource
import app.rive.runtime.kotlin.core.Result
import thong.kotlin.pomodoro.features.streak.presentation.PetState
import thong.kotlin.pomodoro.shared.R

@Composable
actual fun RivePetView(
    petState: PetState,
    onTap: () -> Unit,
    modifier: Modifier
) {
    val worker = rememberRiveWorker()
    val riveFile = rememberRiveFile(RiveFileSource.RawRes.from(R.raw.cat_pomodoro), worker)

    when (riveFile) {
        is Result.Success -> {
            // State machine handles all interactions internally via built-in listeners
            // Touch events are automatically forwarded by the Rive composable
            Rive(
                riveFile = riveFile.value,
                stateMachineName = "State Machine 1",
                modifier = modifier.fillMaxSize()
            )
        }
        else -> {}
    }
}

@Composable
actual fun RiveFireIcon(modifier: Modifier) {
    val worker = rememberRiveWorker()
    val riveFile = rememberRiveFile(RiveFileSource.RawRes.from(R.raw.fire), worker)

    when (riveFile) {
        is Result.Success -> {
            Rive(
                riveFile = riveFile.value,
                animationName = "Fire9",
                modifier = modifier
            )
        }
        else -> {}
    }
}

@Composable
actual fun RiveStarIcon(modifier: Modifier) {
    val worker = rememberRiveWorker()
    val riveFile = rememberRiveFile(RiveFileSource.RawRes.from(R.raw.star), worker)

    when (riveFile) {
        is Result.Success -> {
            Rive(
                riveFile = riveFile.value,
                stateMachineName = "State Machine 1",
                modifier = modifier
            )
        }
        else -> {}
    }
}
