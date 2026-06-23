package thong.kotlin.pomodoro.features.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.AuraButton
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.media.SoundManager
import thong.kotlin.pomodoro.features.learning.mode.components.LearningStyleScreen

class OnboardingScreen(
    private val soundManager: SoundManager?
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Gọi hàm UI bên dưới, khi finish thì ra lệnh cho voyager replace màn hình mới
        OnboardingScreenUI(
            onFinish = {
                navigator.replace(LearningStyleScreen(soundManager))
            }
        )
    }

}

// 1. Định nghĩa cấu trúc dữ liệu cho từng bước hướng dẫn
private data class OnboardingStep(
    val emoji: String,
    val title: String,
    val description: String,
    val highlightColor: Color
)

@Composable
private fun OnboardingScreenUI(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = remember {
        listOf(
            OnboardingStep(
                emoji = "🎯",
                title = "Chào mừng đến với Aura Pomo",
                description = "Phương pháp Pomodoro giúp bạn làm việc sâu và hiệu quả hơn bằng cách chia nhỏ thời gian thành các khoảng tập trung cao độ.",
                highlightColor = AuraColors.WorkMode
            ),
            OnboardingStep(
                emoji = "⚡",
                title = "Tập trung & Nghỉ ngơi",
                description = "25 phút tập trung hết mình cho công việc, sau đó thưởng cho bản thân 5 phút nghỉ ngơi để não bộ tái tạo năng lượng.",
                highlightColor = AuraColors.ShortBreakMode
            ),
            OnboardingStep(
                emoji = "✨",
                title = "Quản lý công việc dễ dàng",
                description = "Theo dõi và gạch bỏ những mục tiêu cần hoàn thành trong ngày ngay tại phòng học để luôn làm chủ tiến độ của bản thân.",
                highlightColor = AuraColors.MainAppMode
            )
        )
    }

    var currentStep by rememberSaveable { mutableStateOf(0) }
    val currentData = steps[currentStep]

    AuraBackground(
        blurRadius = 8f,
        overlayAlpha = 0.6f,
        landscapeImageRes = Res.drawable.landspace_startup_bg,
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                LandscapeOnboardingContent(
                    currentStep = currentStep,
                    steps = steps,
                    currentData = currentData,
                    onNext = { if (currentStep < steps.size - 1) currentStep++ else onFinish() },
                    onSkip = onFinish
                )
            } else {
                PortraitOnboardingContent(
                    currentStep = currentStep,
                    steps = steps,
                    currentData = currentData,
                    onNext = { if (currentStep < steps.size - 1) currentStep++ else onFinish() },
                    onSkip = onFinish
                )
            }
        }
    }
}

@Composable
private fun PortraitOnboardingContent(
    currentStep: Int,
    steps: List<OnboardingStep>,
    currentData: OnboardingStep,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SkipButton(onSkip = onSkip)

        AnimatedContent(
            targetState = currentData,
            transitionSpec = {
                fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                        fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            },
            label = "OnboardingStepTransition",
            modifier = Modifier.weight(1f).wrapContentHeight(Alignment.CenterVertically)
        ) { data ->
            OnboardingInfoColumn(data = data)
        }

        OnboardingControls(
            currentStep = currentStep,
            steps = steps,
            currentData = currentData,
            onNext = onNext
        )
    }
}

@Composable
private fun LandscapeOnboardingContent(
    currentStep: Int,
    steps: List<OnboardingStep>,
    currentData: OnboardingStep,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Visual (Emoji)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlassBox(
                shape = CircleShape,
                modifier = Modifier.size(140.dp)
            ) {
                Text(text = currentData.emoji, fontSize = 60.sp)
            }
        }

        // Right side: Info and Controls
        Column(
            modifier = Modifier.weight(1.2f).padding(start = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            SkipButton(onSkip = onSkip, modifier = Modifier.align(Alignment.End))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentData.title,
                color = AuraColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentData.description,
                color = AuraColors.TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            OnboardingControls(
                currentStep = currentStep,
                steps = steps,
                currentData = currentData,
                onNext = onNext
            )
        }
    }
}

@Composable
private fun OnboardingInfoColumn(data: OnboardingStep) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        GlassBox(
            shape = CircleShape,
            modifier = Modifier.size(110.dp)
        ) {
            Text(text = data.emoji, fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = data.title,
            color = AuraColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            color = AuraColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun SkipButton(onSkip: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "Bỏ qua",
            color = AuraColors.TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onSkip() }
        )
    }
}

@Composable
private fun OnboardingControls(
    currentStep: Int,
    steps: List<OnboardingStep>,
    currentData: OnboardingStep,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            steps.forEachIndexed { index, step ->
                val isSelected = index == currentStep
                val width = if (isSelected) 24.dp else 8.dp
                val color = if (isSelected) step.highlightColor else Color.White.copy(alpha = 0.2f)

                Box(
                    modifier = Modifier
                        .size(height = 8.dp, width = width)
                        .background(color, RoundedCornerShape(50))
                        .animateContentSize()
                )
            }
        }

        AuraButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentStep == steps.size - 1) "Bắt đầu ngay" else "Tiếp tục",
                color = if (currentStep == steps.size - 1) currentData.highlightColor else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}