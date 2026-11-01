package com.example.sibeka.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import java.io.IOException

class AudioPlayerManager(private val context: Context) {

    private var bgmPlayer: MediaPlayer? = null
    private var faunaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var isBgmMuted = false

    fun startBgm(assetPath: String = "audio/bgm/bgm_petualangan.mp3") {
        if (isBgmMuted) return
        stopBgm()
        try {
            val afd = context.assets.openFd(assetPath)
            bgmPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = true
                setVolume(0.35f, 0.35f)
                prepare()
                start()
            }
        } catch (e: Exception) {
            // File BGM belum ada, aplikasi tidak akan crash
        }
    }

    fun stopBgm() {
        bgmPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        bgmPlayer = null
    }

    fun setBgmMuted(muted: Boolean) {
        isBgmMuted = muted
        if (muted) {
            bgmPlayer?.pause()
        } else {
            if (bgmPlayer != null && !bgmPlayer!!.isPlaying) {
                bgmPlayer?.start()
            } else {
                startBgm()
            }
        }
    }

    fun playSuaraFauna(assetPath: String, onCompletion: () -> Unit = {}) {
        stopSuaraFauna()
        if (assetPath.isBlank()) {
            onCompletion()
            return
        }
        try {
            val afd = context.assets.openFd(assetPath)
            faunaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener {
                    onCompletion()
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            onCompletion()
        }
    }

    fun stopSuaraFauna() {
        faunaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        faunaPlayer = null
    }

    fun release() {
        stopBgm()
        stopSuaraFauna()
    }
}
