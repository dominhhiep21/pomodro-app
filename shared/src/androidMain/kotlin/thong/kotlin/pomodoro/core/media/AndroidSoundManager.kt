package thong.kotlin.pomodoro.core.media

import android.content.Context
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.util.Log

class AndroidSoundManager private constructor(
    private val assets: AssetManager
) : SoundManager {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackId: String? = null
    private val ambientPlayers = mutableMapOf<String, MediaPlayer>()

    companion object {
        @Volatile
        private var instance: AndroidSoundManager? = null

        fun getInstance(context: Context): AndroidSoundManager {
            return instance ?: synchronized(this) {
                instance ?: AndroidSoundManager(
                    context.applicationContext.assets
                ).also { instance = it }
            }
        }
    }

    override fun playAlarmSound() {
        // Triển khai sau nếu cần
    }

    override fun playTickSound() {
        // Triển khai sau nếu cần
    }

    override fun playBackgroundMusic(trackId: String) {
        if (currentTrackId == trackId) {
            if (mediaPlayer != null) {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
                return
            }
        }

        stopBackgroundMusic()

        try {
            val assetPath =
                "composeResources/pomodrokotlin.shared.generated.resources/files/audio/$trackId.mp3"

            val player = MediaPlayer()

            assets.openFd(assetPath).use { assetDescriptor ->
                player.setDataSource(
                    assetDescriptor.fileDescriptor,
                    assetDescriptor.startOffset,
                    assetDescriptor.length
                )
            }

            player.apply {
                prepare()
                isLooping = true
                start()
            }

            mediaPlayer = player
            currentTrackId = trackId

        } catch (e: Exception) {
            Log.e("AndroidSoundManager", "Error playing music: $trackId", e)
            mediaPlayer?.release()
            mediaPlayer = null
            currentTrackId = null
        }
    }

    override fun resumeBackgroundMusic() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    override fun pauseBackgroundMusic() {
        mediaPlayer?.pause()
    }

    override fun playBeepSound() {
        playShortEffect("beep_effect")
    }

    override fun playChimeSound() {
        playShortEffect("time_over_effect")
    }

    private fun playShortEffect(fileName: String) {
        try {
            val assetPath =
                "composeResources/pomodrokotlin.shared.generated.resources/files/audio/$fileName.wav"

            val player = MediaPlayer()

            assets.openFd(assetPath).use { assetDescriptor ->
                player.setDataSource(
                    assetDescriptor.fileDescriptor,
                    assetDescriptor.startOffset,
                    assetDescriptor.length
                )
            }

            player.apply {
                prepare()
                setOnCompletionListener {
                    it.release()
                }
                start()
            }

        } catch (e: Exception) {
            Log.e("AndroidSoundManager", "Error playing effect: $fileName", e)
        }
    }

    override fun isBackgroundMusicPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentTrackId(): String? {
        return currentTrackId
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun stopAllSounds() {
        stopBackgroundMusic()

        ambientPlayers.forEach { (_, player) ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e("AndroidSoundManager", "Error stopping ambient player", e)
            }
        }

        ambientPlayers.clear()
    }

    override fun playAmbientSound(soundId: String, volume: Float) {
        if (ambientPlayers.containsKey(soundId)) return

        try {
            val assetPath =
                "composeResources/pomodrokotlin.shared.generated.resources/files/audio/$soundId.mp3"

            val player = MediaPlayer()

            assets.openFd(assetPath).use { assetDescriptor ->
                player.setDataSource(
                    assetDescriptor.fileDescriptor,
                    assetDescriptor.startOffset,
                    assetDescriptor.length
                )
            }

            player.apply {
                prepare()
                setVolume(volume, volume)
                isLooping = true
                start()
            }

            ambientPlayers[soundId] = player

        } catch (e: Exception) {
            Log.e("AndroidSoundManager", "Error playing ambient sound: $soundId", e)
        }
    }

    override fun stopAmbientSound(soundId: String) {
        ambientPlayers[soundId]?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e("AndroidSoundManager", "Error stopping ambient sound: $soundId", e)
            }

            ambientPlayers.remove(soundId)
        }
    }

    override fun isAmbientSoundPlaying(soundId: String): Boolean {
        return ambientPlayers[soundId]?.isPlaying ?: false
    }

    private fun stopBackgroundMusic() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e("AndroidSoundManager", "Error stopping background music", e)
            }
        }

        mediaPlayer = null
        currentTrackId = null
    }
}