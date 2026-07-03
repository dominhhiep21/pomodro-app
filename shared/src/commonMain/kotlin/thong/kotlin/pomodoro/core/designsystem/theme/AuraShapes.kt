package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AuraShapes = Shapes(
    // Dành cho các component nhỏ như thẻ trạng thái, icon badge
    small = RoundedCornerShape(8.dp),

    // Dành cho các ô nhập liệu (InputField) hoặc thẻ danh sách Task
    medium = RoundedCornerShape(16.dp),

    // Dành cho các nút bấm lớn (AuraButton) hoặc các khối Drawer chọn Mode dưới đáy
    large = RoundedCornerShape(24.dp)
)