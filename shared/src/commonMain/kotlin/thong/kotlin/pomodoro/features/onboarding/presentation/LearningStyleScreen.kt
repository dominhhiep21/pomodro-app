package thong.kotlin.pomodoro.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.pomodoro.domain.model.LearningStyle

@Composable
fun LearningStyleScreen(
    onSelectionComplete: (LearningStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStyle by remember { mutableStateOf(LearningStyle.SOLO) }

    AuraBackground(
        blurRadius = 8f,
        overlayAlpha = 0.6f,
        landscapeImageRes = Res.drawable.landspace_startup_bg,
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Chọn phong cách học tập",
                        color = AuraColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aura sẽ điều chỉnh không gian phù hợp với bạn",
                        color = AuraColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Selection Cards
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectionCard(
                            title = "Cá nhân",
                            description = "Tập trung sâu một mình",
                            emoji = "🧘",
                            isSelected = selectedStyle == LearningStyle.SOLO,
                            onClick = { selectedStyle = LearningStyle.SOLO },
                            modifier = Modifier.weight(1f)
                        )
                        SelectionCard(
                            title = "Nhóm",
                            description = "Học cùng bạn bè khắp nơi",
                            emoji = "👥",
                            isSelected = selectedStyle == LearningStyle.GROUP,
                            onClick = { selectedStyle = LearningStyle.GROUP },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SelectionCard(
                            title = "Học tập Cá nhân",
                            description = "Không gian yên tĩnh để bạn tập trung tối đa",
                            emoji = "🧘",
                            isSelected = selectedStyle == LearningStyle.SOLO,
                            onClick = { selectedStyle = LearningStyle.SOLO },
                            modifier = Modifier.fillMaxWidth()
                        )
                        SelectionCard(
                            title = "Học tập theo Nhóm",
                            description = "Tham gia phòng học ảo và cùng nhau tiến bộ",
                            emoji = "👥",
                            isSelected = selectedStyle == LearningStyle.GROUP,
                            onClick = { selectedStyle = LearningStyle.GROUP },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Action Button
                AuraButton(
                    onClick = { onSelectionComplete(selectedStyle) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tiếp tục",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)

    GlassBox(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = AuraColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = AuraColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // Selection Indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(AuraColors.WorkMode, CircleShape)
                        .padding(4.dp)
                ) {
                    // Could add a checkmark icon here
                }
            }
        }
    }
}
