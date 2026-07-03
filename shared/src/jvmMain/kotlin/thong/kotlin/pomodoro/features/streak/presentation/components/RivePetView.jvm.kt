package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun RivePetView(onTap: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pet (Desktop - Rive not supported)", color = Color.White)
    }
}

@Composable
actual fun RiveFireIcon(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("*", color = Color(0xFFFF6B35))
    }
}

@Composable
actual fun RiveStarIcon(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("*", color = Color(0xFFFFD700))
    }
}
