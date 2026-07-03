package thong.kotlin.pomodoro.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import thong.kotlin.pomodoro.core.designsystem.theme.AuraColors

@Composable
fun AuraDialog(
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // SỬ DỤNG: Khối GlassBox từ core làm khung cho hộp thoại
        GlassBox(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp) // Bo góc sâu cao cấp cho cửa sổ Pop-up
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Tiêu đề hộp thoại
                Text(
                    text = title,
                    color = AuraColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Nội dung giải thích
                Text(
                    text = description,
                    color = AuraColors.TextSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 3. Thanh chứa các nút bấm hành động (Hủy / Xác nhận)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nút Hủy (Chỉ vẽ nếu có truyền dismissButtonText)
                    if (dismissButtonText != null) {
                        AuraButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = dismissButtonText, color = AuraColors.TextSecondary, fontSize = 14.sp)
                        }
                    }

                    // Nút Xác nhận hành động
                    AuraButton(
                        onClick = {
                            onConfirm()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = confirmButtonText,
                            color = AuraColors.WorkMode, // Làm nổi bật màu cảnh báo
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}