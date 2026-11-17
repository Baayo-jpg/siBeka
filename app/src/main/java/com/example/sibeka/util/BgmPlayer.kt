package com.example.sibeka.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

/**
 * Background Music (BGM) Manager untuk siBeka.
 *
 * Menggunakan Android MediaPlayer untuk memutar musik latar ceria bertema alam
 * secara berulang (looping) dengan volume seimbang (~40%) agar tidak menenggelamkan SFX.
 *
 * Mendukung berkas di `assets/audio/bgm/`:
 * - main.mp3
 * - main.ogg
 * - main.wav
 *
 * Bersifat fail-safe: jika berkas belum ada, aplikasi tetap berjalan lancar tanpa error/crash.
 */
class BgmPlayer(private val context: Context) {

    companion object {
        private const val TAG = "BgmPlayer"
        private const val VOLUME_DEFAULT = 0.4f
        private val EXTENSI_BGM = listOf(".mp3", ".ogg", ".wav")
    }

    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    private var isMuted: Boolean = false
    @Volatile
    private var isPrepared: Boolean = false
    @Volatile
    private var shouldPlay: Boolean = false

    init {
        persiapkanPlayer()
    }

    private fun persiapkanPlayer() {
        for (ext in EXTENSI_BGM) {
            val path = "audio/bgm/main$ext"
            try {
                context.assets.openFd(path).use { afd ->
                    val player = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        isLooping = true
                        setVolume(VOLUME_DEFAULT, VOLUME_DEFAULT)
                        prepare()
                    }
                    mediaPlayer = player
                    isPrepared = true
                    Log.i(TAG, "BGM berhasil dimuat dari assets/$path")
                    return
                }
            } catch (_: IOException) {
                // Lanjut coba format berikutnya
            } catch (e: Exception) {
                Log.w(TAG, "Gagal menginisialisasi BGM dari $path: ${e.message}")
            }
        }
        Log.d(TAG, "Berkas BGM belum ditemukan di assets/audio/bgm/main.{mp3,ogg,wav}. BGM non-aktif sementara.")
    }

    /** Memulai pemutaran musik latar. */
    fun play() {
        shouldPlay = true
        if (isMuted) return
        if (mediaPlayer == null) {
            persiapkanPlayer()
        }
        val player = mediaPlayer ?: return
        if (isPrepared && !player.isPlaying) {
            try {
                player.start()
            } catch (e: Exception) {
                Log.w(TAG, "Gagal memulai BGM: ${e.message}")
            }
        }
    }

    /** Menjeda musik (misal saat aplikasi masuk ke latar belakang / onStop). */
    fun pause() {
        val player = mediaPlayer ?: return
        if (isPrepared && player.isPlaying) {
            try {
                player.pause()
            } catch (e: Exception) {
                Log.w(TAG, "Gagal menjeda BGM: ${e.message}")
            }
        }
    }

    /** Melanjutkan pemutaran musik (misal saat aplikasi kembali aktif / onStart). */
    fun resume() {
        if (!isMuted && shouldPlay) {
            val player = mediaPlayer ?: return
            if (isPrepared && !player.isPlaying) {
                try {
                    player.start()
                } catch (e: Exception) {
                    Log.w(TAG, "Gagal resume BGM: ${e.message}")
                }
            }
        }
    }

    /** Mengatur status bisu (sinkron dengan tombol suara di Peta / DataStore). */
    fun setMuted(muted: Boolean) {
        this.isMuted = muted
        val player = mediaPlayer ?: return
        if (muted) {
            if (isPrepared && player.isPlaying) {
                try {
                    player.pause()
                } catch (e: Exception) {
                    Log.w(TAG, "Gagal pause BGM saat mute: ${e.message}")
                }
            }
        } else {
            if (shouldPlay && isPrepared && !player.isPlaying) {
                try {
                    player.start()
                } catch (e: Exception) {
                    Log.w(TAG, "Gagal start BGM saat unmute: ${e.message}")
                }
            }
        }
    }

    /** Mengatur volume secara dinamis (0.0f - 1.0f). */
    fun setVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(vol, vol)
    }

    /** Membebaskan resource MediaPlayer saat aplikasi ditutup. */
    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPrepared = false
    }
}
