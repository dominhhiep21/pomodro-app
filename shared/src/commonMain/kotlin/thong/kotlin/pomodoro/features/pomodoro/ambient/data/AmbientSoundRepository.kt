package thong.kotlin.pomodoro.features.pomodoro.ambient.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import thong.kotlin.pomodoro.features.pomodoro.ambient.domain.AmbientSound

object AmbientSoundRepository {
    val availableSounds: List<AmbientSound> = listOf(
        AmbientSound("effect_rain", "Tiếng mưa", Icons.Default.WaterDrop),
        AmbientSound("effect_storm", "Bão tố", Icons.Default.Thunderstorm),
        AmbientSound("effect_campfire", "Lửa trại", Icons.Default.Fireplace),
        AmbientSound("effect_library", "Thư viện", Icons.AutoMirrored.Filled.MenuBook),
        AmbientSound("effect_white_noise", "Tiếng ồn trắng", Icons.Default.Air)
    )

    fun getNameById(id: String?): String {
        return availableSounds.find { it.id == id }?.name ?: "Không có âm thanh"
    }

    fun getNameById(ids: List<String>): List<String> = ids.map { getNameById(it) }
}
