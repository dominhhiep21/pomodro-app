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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun TaskBottomBar(
    modifier: Modifier = Modifier,
    sessionTasks: List<SessionTask>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    // TaskSection props
    newTaskText: String,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onNewTaskTextChange: (String) -> Unit,
    taskValidationError: String? = null,
) {
    var currentTaskIndex by remember { mutableIntStateOf(0) }

    // Cycle tasks when collapsed
    LaunchedEffect(sessionTasks.size, isExpanded) {
        if (!isExpanded && sessionTasks.isNotEmpty()) {
            while (true) {
                delay(4000) // 4 seconds per task
                currentTaskIndex = (currentTaskIndex + 1) % sessionTasks.size
            }
        }
    }

    // Reset index if it goes out of bounds (e.g. task deleted)
    LaunchedEffect(sessionTasks.size) {
        if (currentTaskIndex >= sessionTasks.size) {
            currentTaskIndex = 0
        }
    }

    // Shared animation spec for synchronized motion
    val animationSpec = spring<Dp>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 400.dp else 72.dp,
        animationSpec = animationSpec,
        label = "TaskBarHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .padding(bottom = 16.dp)
            .graphicsLayer {
                // Offload clipping and transformations to GPU
                clip = true
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
            }
    ) {
        GlassBox(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onToggleExpand() },
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
            backgroundColor = AuraColors.BottomBarBackground
        ) {
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing)) togetherWith
                    fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing))
                },
                label = "TaskBarContentTransition",
                modifier = Modifier.fillMaxSize()
            ) { expanded ->
                if (expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Danh sách công việc",
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
                            validationError = taskValidationError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Tasks",
                                    tint = AuraColors.WorkMode,
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                val incompleteCount = sessionTasks.count { !it.isCompleted }
                                if (incompleteCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 10.dp, y = (-10).dp)
                                            .size(20.dp)
                                            .background(Color.Red, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = incompleteCount.toString(),
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))

                            AnimatedContent(
                                targetState = if (sessionTasks.isEmpty()) null else sessionTasks[currentTaskIndex],
                                transitionSpec = {
                                    (slideInVertically { it } + fadeIn()) togetherWith
                                            (slideOutVertically { -it } + fadeOut())
                                },
                                label = "TaskCycling"
                            ) { task ->
                                if (task != null) {
                                    Text(
                                        text = task.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else {
                                    Text(
                                        text = "Chưa có công việc nào",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = "Expand",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
