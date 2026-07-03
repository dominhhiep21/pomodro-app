package thong.kotlin.pomodoro.features.background.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun StaticBackground(
    imageRes: DrawableResource?,
    modifier: Modifier = Modifier
) {
    if (imageRes != null) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
