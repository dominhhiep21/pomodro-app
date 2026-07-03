package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import pomodrokotlin.shared.generated.resources.startup_bg

@Composable
fun AuraBackground(
    modifier: Modifier = Modifier,
    imageRes: DrawableResource = Res.drawable.startup_bg,
    landscapeImageRes: DrawableResource? = null,
    blurRadius: Float = 2f,
    haveBackground: Boolean = true,
    backgroundColor: Color = Color.Black,
    overlayAlpha: Float = 0.4f,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(backgroundColor)
    ) {
        val isLandscape = maxWidth > maxHeight
        val currentImage = if (isLandscape && landscapeImageRes != null) {
            landscapeImageRes
        } else {
            imageRes
        }

        if (haveBackground) {
            Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
                Image(
                    painter = painterResource(currentImage),
                    contentDescription = "Application Background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(blurRadius.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = overlayAlpha))
        )

        content()
    }
}