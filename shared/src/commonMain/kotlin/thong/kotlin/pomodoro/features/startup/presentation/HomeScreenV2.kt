package thong.kotlin.pomodoro.features.startup.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.core.utils.secondsToHourMinuteText
import thong.kotlin.pomodoro.core.utils.toDateTimeText
import thong.kotlin.pomodoro.features.focus.tree.presentation.FocusTreeScreen
import thong.kotlin.pomodoro.features.focus.tree.presentation.FocusTreeSection
import thong.kotlin.pomodoro.features.learning.mode.components.LearningStyleScreen
import thong.kotlin.pomodoro.features.pomodoro._base.domain.StatCardType
import thong.kotlin.pomodoro.features.session.presentation.SessionHistoryScreen
import thong.kotlin.pomodoro.features.settings.presentation.SettingsScreenV2
import thong.kotlin.pomodoro.features.startup.viewmodel.HomeUiViewModel
import thong.kotlin.pomodoro.features.startup.viewmodel.buildTrendUiState
import kotlin.time.Clock

class HomeScreenV2 : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        HomeScreenV2UI(
            onStartNew = { navigator.push(LearningStyleScreen("home")) },
            onViewHistory = { navigator.push(SessionHistoryScreen()) },
            onViewSettings = { navigator.push(SettingsScreenV2()) },
            onViewFocusTree = { navigator.push(FocusTreeScreen()) }
        )
    }
}

@Composable
private fun HomeScreenV2UI(
    onStartNew: () -> Unit,
    onViewHistory: () -> Unit,
    onViewSettings: () -> Unit,
    onViewFocusTree: () -> Unit
) {
    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        val horizontalPadding = if (isLandscape) 48.dp else 20.dp

        AuraBackground {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (!isLandscape) {
                        HomeBottomBar(
                            onSettingsView = onViewSettings,
                            onViewHistory = onViewHistory,
                            onViewFocusTree = onViewFocusTree
                        )
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding, vertical = 24.dp)
                ) {
                    HomeHeader(isLandscape, onViewSettings)

                    Spacer(modifier = Modifier.height(24.dp))

                    TodayStatsSection(isLandscape)

                    Spacer(modifier = Modifier.height(28.dp))

                    QuickStartCard(onStartNew)

                    if (isLandscape) {
                        Spacer(modifier = Modifier.height(32.dp))
                        ExploreSection(onViewHistory)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    RecentActivitySection(isLandscape)

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    isLandscape: Boolean,
    onViewSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Chào mừng trở lại 👋",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sẵn sàng cho một phiên học hiệu quả hôm nay?",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }

        if (isLandscape) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onViewSettings,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayStatsSection(
    isLandscape: Boolean
) {
    val homeUiViewModel = viewModel { HomeUiViewModel() }
    LaunchedEffect(Unit) {
        homeUiViewModel.loadDataStatsSection()
    }

    val homeUiState by homeUiViewModel.uiState.collectAsState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Hôm nay",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                Clock.System.now().toEpochMilliseconds().toDateTimeText(),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val cardModifier = Modifier.weight(1f)
            StatCard(
                cardModifier,
                secondsToHourMinuteText(homeUiState.homeUiStats.todayFocusSeconds.toLong()),
                if (isLandscape) "Tổng thời gian tập trung" else "Tập trung",
                Icons.Default.Schedule,
                AuraColors.ShortBreakMode,
                homeUiState.homeUiStats.focusSecondsDiff.toString(),
                type = StatCardType.TOTAL_FOCUS_TIME,
                isLandscape = isLandscape
            )
            StatCard(
                cardModifier,
                homeUiState.homeUiStats.todayCompletedPomodoros.toString(),
                if (isLandscape) "Pomodoro hoàn thành" else "Pomodoro",
                Icons.Default.RadioButtonChecked,
                AuraColors.WorkMode,
                homeUiState.homeUiStats.completedPomodorosDiff.toString(),
                type = StatCardType.COMPLETED_POMODOROS,
                isLandscape = isLandscape
            )
            StatCard(
                cardModifier,
                homeUiState.homeUiStats.bestStreakDays.toString(),
                if (isLandscape) "Chuỗi hiện tại" else "Chuỗi",
                Icons.Default.LocalFireDepartment,
                Color(0xFFFFA500),
                homeUiState.homeUiStats.currentStreakDays.toString(),
                type = StatCardType.BEST_STREAK,
                isLandscape = isLandscape
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    trend: String? = null,
    type: StatCardType,
    isLandscape: Boolean
) {
    GlassBox(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color.White.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    value,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    label,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            trend
                ?.let { buildTrendUiState(it, type, isCompact = !isLandscape) }
                ?.let { trendUiState ->
                    Text(
                        text = trendUiState.text,
                        color = trendUiState.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(
                                trendUiState.color.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
        }
    }
}

@Composable
private fun QuickStartCard(onStartNew: () -> Unit) {
    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onStartNew),
        shape = RoundedCornerShape(24.dp),
        backgroundBrush = Brush.horizontalGradient(
            colors = listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.02f))
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Progress Circle Placeholder
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.White.copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                AuraColors.ShortBreakMode,
                                AuraColors.LongBreakMode
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AuraColors.ShortBreakMode,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Bắt đầu phiên học mới",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tập trung tối đa với Pomodoro và đạt mục tiêu của bạn.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                AuraColors.LongBreakMode,
                                AuraColors.LongBreakMode.copy(alpha = 0.6f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ExploreSection(onViewHistory: () -> Unit) {
    Column {
        Text(
            "Khám phá",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExploreCard(
                Modifier.weight(1f),
                "Dashboard",
                "Tổng quan tiến độ",
                Icons.Default.GridView,
                Color(0xFF00CED1)
            ) {}
            ExploreCard(
                Modifier.weight(1f),
                "Lịch sử",
                "Xem các phiên học",
                Icons.Default.History,
                Color(0xFF9370DB),
                onViewHistory
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExploreCard(
                Modifier.weight(1f),
                "Thống kê",
                "Phân tích hiệu suất",
                Icons.Default.BarChart,
                Color(0xFF4682B4)
            ) {}
            ExploreCard(
                Modifier.weight(1f),
                "Nhiệm vụ",
                "Quản lý mục tiêu",
                Icons.Default.Checklist,
                Color(0xFF1E90FF)
            ) {}
        }
    }
}

@Composable
private fun ExploreCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    GlassBox(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White.copy(alpha = 0.04f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconBgColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RecentActivitySection(isLandscape: Boolean) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "Hoạt động gần đây",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Xem tất cả >",
                color = AuraColors.ShortBreakMode,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White.copy(alpha = 0.04f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Recent Session Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(AuraColors.ShortBreakMode.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = AuraColors.ShortBreakMode
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (isLandscape) "Focus Session" else "Focus",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isLandscape) "Hoàn thành" else "Xong",
                                color = AuraColors.ShortBreakMode,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .background(
                                        AuraColors.ShortBreakMode.copy(alpha = 0.1f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            if (isLandscape) "20/05/2024 • 09:30 - 10:05" else "Hôm nay • 09:30",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        "25:00",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weekly Progress Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tiến độ tuần này",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "4h 30m / 10h mục tiêu",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Progress Chart
                WeeklyProgressChart()
            }
        }
    }
}

@Composable
private fun WeeklyProgressChart() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = 32.dp.toPx()
            val spacing = (size.width - (barWidth * 7)) / 6
            val targetHeight = size.height * 0.7f

            // Draw target line
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(0f, size.height - targetHeight),
                end = Offset(size.width, size.height - targetHeight),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(
                        10f,
                        10f
                    ), 0f
                )
            )

            val barHeights = listOf(0.4f, 0.6f, 0.9f, 0.3f, 0.7f, 0.5f, 0.2f)

            barHeights.forEachIndexed { index, h ->
                val left = index * (barWidth + spacing)
                val currentBarHeight = size.height * 0.8f * h

                drawRoundRect(
                    color = if (index == 2) AuraColors.ShortBreakMode else Color.White.copy(alpha = 0.1f),
                    topLeft = Offset(left, size.height - currentBarHeight),
                    size = Size(barWidth, currentBarHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            labels.forEach { label ->
                Text(label, color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    onSettingsView: () -> Unit,
    onViewHistory: () -> Unit,
    onViewFocusTree: () -> Unit,
) {
    GlassBox(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Trang chủ", Icons.Default.Home, true) {}
            BottomNavItem("Lịch sử", Icons.Default.History, false, onViewHistory)
            BottomNavItem("Focus Tree", Icons.Default.Nature, false, onViewFocusTree)
            BottomNavItem("Thống kê", Icons.Default.BarChart, false) {}
            BottomNavItem("Cài đặt", Icons.Default.Settings, false, onSettingsView)
            BottomNavItem("Cá nhân", Icons.Default.Person, false) {}
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) AuraColors.ShortBreakMode else Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = if (isSelected) AuraColors.ShortBreakMode else Color.White.copy(alpha = 0.3f),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}