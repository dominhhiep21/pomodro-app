package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun RivePetView(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
)

@Composable
expect fun RiveFireIcon(modifier: Modifier = Modifier)

@Composable
expect fun RiveStarIcon(modifier: Modifier = Modifier)
