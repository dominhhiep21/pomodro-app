package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import thong.kotlin.pomodoro.shared.R

@Composable
actual fun RivePetView(onTap: () -> Unit, modifier: Modifier) {
    AndroidView(
        factory = { ctx ->
            RiveAnimationView(ctx).apply {
                setRiveResource(
                    resId = R.raw.muza_cat,
                    stateMachineName = "State Machine 1",
                    fit = Fit.CONTAIN
                )
                setOnClickListener {
                    onTap()
                    try {
                        fireState("State Machine 1", "Forehead click")
                    } catch (e: Exception) {
                        // Safe fallback if trigger not found or state machine not ready
                    }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
actual fun RiveFireIcon(modifier: Modifier) {
    AndroidView(
        factory = { ctx ->
            RiveAnimationView(ctx).apply {
                setRiveResource(resId = R.raw.fire, animationName = "Fire", fit = Fit.CONTAIN)
            }
        },
        modifier = modifier
    )
}

@Composable
actual fun RiveStarIcon(modifier: Modifier) {
    AndroidView(
        factory = { ctx ->
            RiveAnimationView(ctx).apply {
                setRiveResource(resId = R.raw.star, stateMachineName = "State Machine 1", fit = Fit.CONTAIN)
            }
        },
        modifier = modifier
    )
}
