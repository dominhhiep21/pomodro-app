package thong.kotlin.pomodoro.core.media

/**
 * Bản hợp đồng quản lý âm thanh dùng chung cho toàn bộ dự án KMP
 */
interface SoundManager {
    /**
     * Phát tiếng chuông báo thức khi hoàn thành phiên học/nghỉ
     */
    fun playAlarmSound()

    /**
     * Phát tiếng gõ tích tắc nhẹ nhàng kích thích tập trung (Metronome hiệu ứng)
     */
    fun playTickSound()

    /**
     * Phát nhạc nền theo ID bài hát
     */
    fun playBackgroundMusic(trackId: String)

    /**
     * Tiếp tục phát nhạc nền (nếu đang tạm dừng)
     */
    fun resumeBackgroundMusic()

    /**
     * Tạm dừng nhạc nền
     */
    fun pauseBackgroundMusic()

    /**
     * Phát tiếng bíp ngắn (dùng cho 5 giây cuối)
     */
    fun playBeepSound()

    /**
     * Phát tiếng chuông báo hoàn thành (chime)
     */
    fun playChimeSound()

    /**
     * Kiểm tra xem nhạc nền có đang phát không
     */
    fun isBackgroundMusicPlaying(): Boolean

    /**
     * Lấy ID bài nhạc đang phát
     */
    fun getCurrentTrackId(): String?

    /**
     * Lấy vị trí hiện tại của bài nhạc (theo miliseconds hoặc bytes)
     */
    fun getCurrentPosition(): Long

    /**
     * Dừng toàn bộ âm thanh đang phát
     */
    fun stopAllSounds()

    /**
     * Phát âm thanh môi trường (looping)
     */
    fun playAmbientSound(soundId: String, volume: Float = 0.5f)

    /**
     * Dừng âm thanh môi trường
     */
    fun stopAmbientSound(soundId: String)

    /**
     * Kiểm tra xem âm thanh môi trường có đang phát không
     */
    fun isAmbientSoundPlaying(soundId: String): Boolean
}