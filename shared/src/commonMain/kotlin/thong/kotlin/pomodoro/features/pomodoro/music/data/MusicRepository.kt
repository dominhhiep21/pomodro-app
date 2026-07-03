package thong.kotlin.pomodoro.features.pomodoro.music.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import thong.kotlin.pomodoro.features.pomodoro.music.domain.MusicTrack

object MusicRepository {
    /**
     * Danh sách tất cả các bài nhạc có sẵn trong ứng dụng.
     * Để thêm nhạc mới, bạn chỉ cần thêm một item MusicTrack vào danh sách này.
     * ID phải trùng với tên file trong composeResources/files/audio (không bao gồm đuôi .mp3)
     */
    val availableTracks: List<MusicTrack> = listOf(
        MusicTrack("aura_audio", "Aura Focus", Icons.Default.MusicNote),
        MusicTrack("lofi_hiphop_pomodoro_drift", "Lofi Hiphop Pomodoro Drift", Icons.Default.MusicNote),
        MusicTrack("lofi_hiphop_grateful", "Lofi Hiphop Grateful", Icons.Default.MusicNote),
        MusicTrack("aura_bon_apetit", "Smile Sisters Sadistic Surprise Service", Icons.Default.MusicNote),
//        MusicTrack("rain", "Tiếng mưa", Icons.Default.WaterDrop),
//        MusicTrack("forest", "Rừng đêm", Icons.Default.NightsStay),
//        MusicTrack("white_noise", "Tiếng ồn trắng", Icons.Default.Air)
    )

    const val DEFAULT_TRACK_ID: String = "aura_audio"

    fun getNameById(id: String?): String {
        return availableTracks.find { it.id == id }?.name ?: "Không có nhạc"
    }
}