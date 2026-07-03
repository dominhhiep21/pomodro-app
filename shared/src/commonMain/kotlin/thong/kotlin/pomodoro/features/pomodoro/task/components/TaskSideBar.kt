package thong.kotlin.pomodoro.features.pomodoro.task.components

import thong.kotlin.pomodoro.features.pomodoro.task.domain.model.SessionTask

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

/**
 * A compact task entry point that looks like a circular button when collapsed 
 * and a full task list when expanded. Positioned at bottom-right in landscape.
 */
@Composable
fun TaskSideBar(
    sessionTasks: List<SessionTask>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    newTaskText: String,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Shared animation spec for synchronized motion
    val animationSpec = spring<Dp>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val animatedWidth by animateDpAsState(
        targetValue = if (isExpanded) 350.dp else 48.dp,
        animationSpec = animationSpec,
        label = "TaskBarWidth"
    )
    
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 400.dp else 48.dp,
        animationSpec = animationSpec,
        label = "TaskBarHeight"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 24.dp else 32.dp,
        animationSpec = animationSpec,
        label = "TaskBarCornerRadius"
    )

    Box(
        modifier = modifier
            .width(animatedWidth)
            .height(animatedHeight)
    ) {
        // 1. The main Sidebar Content (Clipped)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Offload clipping and transformations to GPU
                    clip = true
                    shape = RoundedCornerShape(cornerRadius)
                }
        ) {
            GlassBox(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onToggleExpand() },
                shape = RoundedCornerShape(cornerRadius),
                backgroundColor = AuraColors.BottomBarBackground
            ) {
                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing))
                    },
                    label = "TaskSideBarContentTransition",
                    modifier = Modifier.fillMaxSize()
                ) { expanded ->
                    if (expanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Công việc",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Collapse",
                                    tint = Color.White
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            TaskSection(
                                sessionTasks = sessionTasks,
                                newTaskText = newTaskText,
                                onAddTask = onAddTask,
                                onDeleteTask = onDeleteTask,
                                onToggleTask = onToggleTask,
                                onNewTaskTextChange = onNewTaskTextChange,
                                useLazyColumn = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Tasks",
                                tint = AuraColors.WorkMode,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. The Floating Notification Badge (Overlay - Unclipped)
        if (!isExpanded) {
            val incompleteCount = sessionTasks.count { !it.isCompleted }
            if (incompleteCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                        .clickable { onToggleExpand() }, // Also clickable
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (incompleteCount > 9) "9+" else incompleteCount.toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
