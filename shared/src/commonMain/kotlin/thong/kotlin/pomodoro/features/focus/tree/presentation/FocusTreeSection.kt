package thong.kotlin.pomodoro.features.focus.tree.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.forest_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraCircularProgress
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.designsystem.theme.AuraGradients
import thong.kotlin.pomodoro.di.DependencyRegistry
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord
import thong.kotlin.pomodoro.features.focus.tree.domain.TreeGrowthStage
import thong.kotlin.pomodoro.features.focus.tree.presentation.components.AnimatedFocusTree

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
    AuraBackground(
        imageRes = Res.drawable.forest_bg,
        blurRadius = 10f,
        overlayAlpha = 0.6f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Vườn Tập Trung",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Tree Visualization
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Progress Circle
                val progress = when (focusTreeRecord.growthStage) {
                    TreeGrowthStage.SEED -> focusTreeRecord.growthPoint / 100f
                    TreeGrowthStage.SPROUT -> (focusTreeRecord.growthPoint - 100) / 200f
                    TreeGrowthStage.SMALL_TREE -> (focusTreeRecord.growthPoint - 300) / 400f
                    TreeGrowthStage.BIG_TREE -> (focusTreeRecord.growthPoint - 700) / 800f
                    TreeGrowthStage.BLOOMING_TREE -> 1f
                }.coerceIn(0f, 1f)

                AuraCircularProgress(
                    progress = progress,
                    progressBrush = AuraGradients.BreakFlow,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp
                )

                // The Animated Tree
                AnimatedFocusTree(
                    focusTree = focusTreeRecord,
                    animationEvent = null,
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stage Label
            val stageLabel = when (focusTreeRecord.growthStage) {
                TreeGrowthStage.SEED -> "Hạt giống"
                TreeGrowthStage.SPROUT -> "Mầm cây"
                TreeGrowthStage.SMALL_TREE -> "Cây nhỏ"
                TreeGrowthStage.BIG_TREE -> "Cây lớn"
                TreeGrowthStage.BLOOMING_TREE -> "Cây nở hoa"
            }

            Text(
                text = stageLabel,
                color = AuraColors.ShortBreakMode,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "${focusTreeRecord.growthPoint} điểm sinh trưởng",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "Tổng Pomodoro",
                    value = focusTreeRecord.totalCompletedWorkRounds.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Thời gian tập trung",
                    value = "${focusTreeRecord.totalFocusSeconds / 60} phút",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    label = "Score trung bình",
                    value = "${focusTreeRecord.averageFocusScore}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Chuỗi ngày",
                    value = "${focusTreeRecord.currentStreakDays} ngày",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassBox(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color.White.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

