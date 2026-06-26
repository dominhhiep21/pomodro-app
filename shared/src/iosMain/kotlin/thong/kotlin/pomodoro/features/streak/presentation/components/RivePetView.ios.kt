package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import thong.kotlin.pomodoro.features.streak.presentation.PetState

// TODO: Implement with rive-ios Swift package via UIViewControllerRepresentable
// Setup: Add rive-ios SPM package to iosApp Xcode project
// Then bridge via UIKitView in Compose or expect/actual with Swift interop

@Composable
actual fun RivePetView(
    petState: PetState,
    onTap: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Transparent).clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Text("🐱 Pet (iOS Rive pending)", color = Color.White)
    }
}

@Composable
actual fun RiveFireIcon(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("🔥", color = Color.White)
    }
}

@Composable
actual fun RiveStarIcon(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("⭐", color = Color.White)
    }
}
