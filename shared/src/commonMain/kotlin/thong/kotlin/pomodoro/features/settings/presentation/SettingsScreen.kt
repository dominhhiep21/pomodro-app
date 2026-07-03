//package thong.kotlin.pomodoro.features.settings.presentation
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import org.jetbrains.compose.resources.painterResource
//import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors
//import thong.kotlin.pomodoro.core.designsystem.theme.AuraGradients
//import thong.kotlin.pomodoro.core.designsystem.components.AuraBackground
//import thong.kotlin.pomodoro.core.designsystem.components.AuraDialog
//import thong.kotlin.pomodoro.core.designsystem.components.AuraHeader
//import thong.kotlin.pomodoro.core.designsystem.components.GlassBox
//import thong.kotlin.pomodoro.features.settings.domain.AuraBackgroundTheme
//
//@Composable
//fun SettingsScreen(
//    viewModel: SettingsViewModel,
//    onNavigateBack: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    AuraBackground(
////        backgroundImage = uiState.selectedTheme.imageRes,
//        blurRadius = 8f,
//        overlayAlpha = 0.5f
//    ) {
//        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
//            AuraHeader(
//                title = "Aura Store",
//                subtitle = "Khám phá không gian học tập mới",
//                modifier = Modifier.padding(top = 16.dp),
//                actionButton = { /* Nút Back */ }
//            )
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                modifier = Modifier.fillMaxSize().padding(top = 16.dp)
//            ) {
//                items(uiState.availableThemes) { theme ->
//                    val isUnlocked = uiState.unlockedThemeIds.contains(theme.id)
//
//                    BackgroundItemCard(
//                        theme = theme,
//                        isSelected = uiState.selectedTheme == theme,
//                        isUnlocked = isUnlocked,
//                        onClick = { viewModel.handleThemeClick(theme) }
//                    )
//                }
//            }
//        }
//
//        // ==========================================
//        // HỘP THOẠI XÁC NHẬN MUA HÀNG (SỬ DỤNG AURA DIALOG CỐT LÕI)
//        // ==========================================
//        uiState.themeToBuy?.let { theme ->
//            AuraDialog(
//                onDismissRequest = { viewModel.cancelPurchase() },
//                title = "Mở khóa Không gian",
//                description = "Bạn có muốn dùng ${theme.price} Pomo Coins để mở khóa không gian '${theme.title}' vĩnh viễn không?",
//                dismissButtonText = "Để sau",
//                confirmButtonText = "Mở khóa ngay",
//                onConfirm = { viewModel.confirmPurchase(theme) }
//            )
//        }
//    }
//}
//
//@Composable
//private fun BackgroundItemCard(
//    theme: AuraBackgroundTheme,
//    isSelected: Boolean,
//    isUnlocked: Boolean, // Cờ kiểm tra trạng thái khóa
//    onClick: () -> Unit
//) {
//    val borderBrush = if (isSelected) AuraGradients.WorkFlow else AuraGradients.GlassBorder
//
//    GlassBox(
//        shape = RoundedCornerShape(16.dp),
//        borderBrush = borderBrush,
//        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clickable { onClick() }
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Image(
//                painter = painterResource(theme.imageRes),
//                contentDescription = theme.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
//            )
//
//            // NẾU CHƯA MỞ KHÓA: Phủ một lớp đen dày hơn để làm tối ảnh đi
//            if (!isUnlocked) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.6f))
//                )
//            }
//
//            // Lớp phủ đen Gradient đáy cho chữ
//            Box(
//                modifier = Modifier.fillMaxSize().background(
//                    androidx.compose.ui.graphics.Brush.verticalGradient(
//                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 100f
//                    )
//                )
//            )
//
//            // VẼ ICON Ổ KHÓA HOẶC GIÁ TIỀN Ở GÓC TRÊN CÙNG BÊN PHẢI
//            if (!isUnlocked) {
//                GlassBox(
//                    shape = RoundedCornerShape(12.dp),
//                    backgroundColor = Color.Black.copy(alpha = 0.4f),
//                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).padding(horizontal = 8.dp, vertical = 4.dp)
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(12.dp))
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("${theme.price}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
//                    }
//                }
//            }
//
//            Text(
//                text = theme.title,
//                color = if (isSelected) AuraColors.WorkMode else Color.White,
//                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
//                fontSize = 14.sp,
//                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
//            )
//        }
//    }
//}