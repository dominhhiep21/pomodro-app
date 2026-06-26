package thong.kotlin.pomodoro.features.streak.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import thong.kotlin.pomodoro.features.streak.presentation.PetState

@Composable
expect fun RivePetView(
    petState: PetState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
)

@Composable
expect fun RiveFireIcon(modifier: Modifier = Modifier)

@Composable
expect fun RiveStarIcon(modifier: Modifier = Modifier)
