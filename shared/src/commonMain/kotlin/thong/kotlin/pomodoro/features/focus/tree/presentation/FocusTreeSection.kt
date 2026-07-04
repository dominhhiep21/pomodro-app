package thong.kotlin.pomodoro.features.focus.tree.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord
import thong.kotlin.pomodoro.features.focus.tree.domain.TreeGrowthStage

class FocusTreeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var focusTree by remember { mutableStateOf(FocusTreeRecord()) }
        val focusTreeRepository = remember { DependencyRegistry.focusTreeRepository }

        LaunchedEffect(Unit) {
            focusTreeRepository.getFocusTree().let {
                focusTree = it.copy()
            }
        }

        FocusTreeSection(focusTree, onBack = {
            navigator.pop()
        })

    }
}

@Composable
fun FocusTreeSection(
    focusTreeRecord: FocusTreeRecord,
    onBack: () -> Unit = {}
) {
    Column {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Quay lại",
            tint = Color.White,
            modifier = Modifier.size(24.dp).clickable { onBack() }
        )
        Text("Focus Garden")
        Spacer(modifier = Modifier.height(8.dp))
        Text(getTreeEmoji(focusTreeRecord.growthStage))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Trạng thái: ${focusTreeRecord.growthStage}")
        Text("Growth Point: ${focusTreeRecord.growthPoint}")
        Text("Tổng Pomodoro: ${focusTreeRecord.totalCompletedWorkRounds}")
        Text("Tổng thời gian focus: ${focusTreeRecord.totalFocusSeconds / 60} phút")
        Text("Focus Score trung bình: ${focusTreeRecord.averageFocusScore}")
    }
}

private fun getTreeEmoji(stage: TreeGrowthStage): String {
    return when (stage) {
        TreeGrowthStage.SEED -> "🌰"
        TreeGrowthStage.SPROUT -> "🌱"
        TreeGrowthStage.SMALL_TREE -> "🌿"
        TreeGrowthStage.BIG_TREE -> "🌳"
        TreeGrowthStage.BLOOMING_TREE -> "🌸"
    }
}