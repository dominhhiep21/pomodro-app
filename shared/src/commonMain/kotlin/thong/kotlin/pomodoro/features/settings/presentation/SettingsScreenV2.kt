package thong.kotlin.pomodoro.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
import thong.kotlin.pomodoro.core.designsystem.components.IconBox
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
import thong.kotlin.pomodoro.features.background.data.BackgroundRepository
import thong.kotlin.pomodoro.features.background.presentation.components.CompactBackgroundSection
import thong.kotlin.pomodoro.features.pomodoro.music.data.MusicRepository
import thong.kotlin.pomodoro.features.pomodoro.music.presentation.CompactMusicSectionComponent
import thong.kotlin.pomodoro.features.settings.viewmodel.SettingsUiState
import thong.kotlin.pomodoro.features.settings.viewmodel.SettingsViewModelV2

class SettingsScreenV2 : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SettingsViewModelV2 = viewModel { SettingsViewModelV2() }
        val state by viewModel.uiState.collectAsState()

        SettingsUI(
            state = state,
            onBack = { navigator.pop() },
            viewModel = viewModel
        )
    }
}

@Composable
private fun SettingsUI(
    state: SettingsUiState,
    onBack: () -> Unit,
    viewModel: SettingsViewModelV2
) {
    var showBackgroundSelection by remember { mutableStateOf(false) }
    var showMusicSelection by remember { mutableStateOf(false) }

    if (showBackgroundSelection) {
        Dialog(onDismissRequest = { showBackgroundSelection = false }) {
            GlassBox(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.95f)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    CompactBackgroundSection(
                        availableBackgrounds = BackgroundRepository.availableBackgrounds,
                        selectedBackgroundId = state.userSettings.personalSelectedBackgroundId,
                        onSelectBackground = {
                            viewModel.updateSelectedBackground(it)
                            showBackgroundSelection = false
                        },
                        modifier = Modifier.fillMaxSize(),
                        compact = true
                    )
                }
            }
        }
    }

    if (showMusicSelection) {
        Dialog(onDismissRequest = { showMusicSelection = false }) {
            GlassBox(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.95f)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    CompactMusicSectionComponent(
                        availableTracks = MusicRepository.availableTracks,
                        selectedTrackId = state.userSettings.personalLastSelectedMusicId,
                        isMusicPlaying = false, // Preview not needed in settings
                        onToggleMusic = {},
                        onSelectTrack = {
                            viewModel.updateSelectedMusic(it)
                            showMusicSelection = false
                        },
                        modifier = Modifier.fillMaxSize(),
                        compact = true
                    )
                }
            }
        }
    }

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        val horizontalPadding = if (isLandscape) 64.dp else 20.dp

        AuraBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    SettingsHeader(onBack = onBack)
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding, vertical = 24.dp)
                ) {
                    // TIMER SECTION
                    SettingsSectionCard("TIMER") {
                        TimerSettingItem("WORK", "Thời gian tập trung", state.userSettings.personalWorkMinutes, Icons.Default.Eco, AuraColors.ShortBreakMode) { viewModel.updateWorkMinutes(it) }
                        TimerSettingItem("BREAK", "Nghỉ giải lao ngắn", state.userSettings.personalBreakMinutes, Icons.Default.Coffee, AuraColors.WorkMode) { viewModel.updateBreakMinutes(it) }
                        TimerSettingItem("LONG BREAK", "Nghỉ dài giữa các chu kỳ", state.userSettings.personalLongBreakMinutes, Icons.Default.FilterHdr, AuraColors.LongBreakMode) { viewModel.updateLongBreakMinutes(it) }
                    }

                    // GOALS SECTION
                    SettingsSectionCard("MỤC TIÊU") {
                        GoalSliderItem(state.userSettings.dailyTargetMinutes) { viewModel.updateDailyTarget(it) }
                    }

                    // EXPERIENCE SECTION
                    SettingsSectionCard("TRẢI NGHIỆM") {
                        NavigationSettingItem(
                            "Background mặc định",
                            BackgroundRepository.getNameById(state.userSettings.personalSelectedBackgroundId),
                            Icons.Default.Image,
                            Color(0xFF00CED1)
                        ) { showBackgroundSelection = true }
                        
                        NavigationSettingItem(
                            "Music mặc định",
                            MusicRepository.getNameById(state.userSettings.personalLastSelectedMusicId),
                            Icons.Default.MusicNote,
                            Color(0xFF9370DB)
                        ) { showMusicSelection = true }
                    }

                    // NOTIFICATIONS SECTION
                    SettingsSectionCard("THÔNG BÁO & ÂM THANH") {
                        ToggleSettingItem("Bật thông báo", "Nhận thông báo nhắc nhở", state.userSettings.isNotificationEnabled, Icons.Default.Notifications, Color(0xFFDA70D6)) { viewModel.toggleNotification(it) }
                        ToggleSettingItem("Âm thanh hoàn thành", "Phát âm thanh khi kết thúc", state.userSettings.isSoundEnabled, Icons.Default.CheckCircle, Color(0xFF32CD32)) { viewModel.toggleSound(it) }
                        ToggleSettingItem("Tự động bắt đầu break", "Tự động sau khi kết thúc work", state.userSettings.autoStartBreak, Icons.Default.BakeryDining, Color(0xFFFF8C00)) { viewModel.toggleAutoStartBreak(it) }
                        ToggleSettingItem("Tự động bắt đầu work", "Tự động sau khi kết thúc break", state.userSettings.autoStartWork, Icons.Default.SelfImprovement, Color(0xFF6495ED)) { viewModel.toggleAutoStartWork(it) }
                        ToggleSettingItem("Rung khi hết giờ", "Rung khi phiên kết thúc", state.userSettings.isVibrationEnabled, Icons.Default.Vibration, Color(0xFFFFD700)) { viewModel.toggleVibration(it) }
                    }

                    // APP SECTION
                    SettingsSectionCard("ĐỒNG BỘ & ỨNG DỤNG") {
                        NavigationSettingItem("Đồng bộ Cloud", "Chưa đồng bộ", Icons.Default.CloudDone, Color(0xFF00BFFF)) {}
//                        ToggleSettingItem("Chế độ tối", "Giảm ánh sáng, bảo vệ mắt", state.userSettings.isDarkMode, Icons.Default.DarkMode, Color(0xFF483D8B)) { viewModel.toggleDarkMode(it) }
//                        NavigationSettingItem("Ngôn ngữ", "Tiếng Việt", Icons.Default.Language, Color(0xFF9932CC)) {}
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.resetToDefault() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("Khôi phục cài đặt gốc", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cài đặt", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Tùy chỉnh trải nghiệm của bạn", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }

        // Avatar Placeholder
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = AuraColors.ShortBreakMode,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun TimerSettingItem(
    title: String,
    subtitle: String,
    value: Int,
    icon: ImageVector,
    iconColor: Color,
    onValueChange: (Int) -> Unit
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon, iconColor)
        
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > 1) onValueChange(value - 1) }) {
                Icon(Icons.Default.Remove, null, tint = Color.White.copy(alpha = 0.6f))
            }
            
            Row(
                modifier = Modifier
                    .widthIn(min = 70.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BasicTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            textValue = newValue
                            newValue.toIntOrNull()?.let { num ->
                                if (num in 1..999) onValueChange(num)
                            }
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(IntrinsicSize.Min).widthIn(min = 20.dp)
                )
                Text(
                    text = " phút",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = { onValueChange(value + 1) }) {
                Icon(Icons.Default.Add, null, tint = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun GoalSliderItem(
    currentMinutes: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBox(Icons.Default.TrackChanges, Color(0xFF00BFA5))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Mục tiêu mỗi ngày", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Tổng thời gian tập trung", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                }
            }
            Text(
                "$currentMinutes phút (${currentMinutes / 60} giờ)",
                color = AuraColors.ShortBreakMode,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = currentMinutes.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 60f..600f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = AuraColors.ShortBreakMode,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("60 phút", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
            Text("600 phút", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun NavigationSettingItem(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon, iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(value, color = AuraColors.ShortBreakMode, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White.copy(alpha = 0.2f))
    }
}

@Composable
private fun ToggleSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    icon: ImageVector,
    iconColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon, iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AuraColors.ShortBreakMode,
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        SettingsSectionTitle(title)
        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = AuraColors.BottomBarBackground.copy(alpha = 0.7f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}