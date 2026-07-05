package thong.kotlin.pomodoro.features.focus.tree.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.painterResource
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.focus_tree_big_tree
import pomodrokotlin.shared.generated.resources.focus_tree_blooming_tree
import pomodrokotlin.shared.generated.resources.focus_tree_seed
import pomodrokotlin.shared.generated.resources.focus_tree_small_tree
import pomodrokotlin.shared.generated.resources.focus_tree_sprout
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord
import thong.kotlin.pomodoro.features.focus.tree.domain.TreeGrowthStage
import thong.kotlin.pomodoro.features.focus.tree.presentation.animation.FocusTreeAnimationEvent

@Composable
fun AnimatedFocusTree(
    modifier: Modifier = Modifier,
    focusTree: FocusTreeRecord,
    animationEvent: FocusTreeAnimationEvent?
) {
    var isGrowing by remember { mutableStateOf(false) }

    val treeScale by animateFloatAsState(
        targetValue = if (isGrowing) 1.12f else 1f,
        animationSpec = tween(durationMillis = 450),
        label = "treeScale"
    )

    val growComposition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/animations/tree_grow.json").decodeToString()
        )
    }

    val growProgress by animateLottieCompositionAsState(
        composition = growComposition,
        iterations = 1,
        isPlaying = isGrowing,
        restartOnPlay = true
    )

    LaunchedEffect(animationEvent?.id) {
        if (animationEvent != null) {
            isGrowing = true
            kotlinx.coroutines.delay(450)
            isGrowing = false
        }
    }

    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(treeDrawable(focusTree.growthStage)),
            contentDescription = "Focus Tree",
            modifier = Modifier
                .size(140.dp)
                .scale(treeScale)
        )

        if (isGrowing && growComposition != null) {
            Image(
                painter = rememberLottiePainter(
                    composition = growComposition,
                    progress = { growProgress }
                ),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
        }

        if (animationEvent != null && isGrowing) {
            Text(
                text = "+${animationEvent.addedGrowthPoint}",
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

private fun treeDrawable(stage: TreeGrowthStage) = when (stage) {
    TreeGrowthStage.SEED -> Res.drawable.focus_tree_seed
    TreeGrowthStage.SPROUT -> Res.drawable.focus_tree_sprout
    TreeGrowthStage.SMALL_TREE -> Res.drawable.focus_tree_small_tree
    TreeGrowthStage.BIG_TREE -> Res.drawable.focus_tree_big_tree
    TreeGrowthStage.BLOOMING_TREE -> Res.drawable.focus_tree_blooming_tree
}