package com.example.sibeka.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import java.io.IOException

/**
 * SoundPool manager untuk SFX instan tanpa delay (<10ms latency).
 * Menyediakan fallback ToneGenerator sintesis jika file suara belum ada.
 */
class SfxPlayer(private val context: Context) {

    private val soundPool: SoundPool
    private var isMuted: Boolean = false

    private var soundClickId: Int = 0
    private var soundBenarId: Int = 0
    private var soundSalahId: Int = 0
    private var soundKoinId: Int = 0
    private var soundBintangId: Int = 0

    private val toneGen: ToneGenerator by lazy {
        ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()
    }

    private fun loadSounds() {
        soundClickId = loadFromAsset("audio/sfx/click.mp3")
        soundBenarId = loadFromAsset("audio/sfx/benar.mp3")
        soundSalahId = loadFromAsset("audio/sfx/salah.mp3")
        soundKoinId = loadFromAsset("audio/sfx/koin.mp3")
        soundBintangId = loadFromAsset("audio/sfx/bintang.mp3")
    }

    private fun loadFromAsset(assetPath: String): Int {
        return try {
            val afd = context.assets.openFd(assetPath)
            soundPool.load(afd, 1)
        } catch (e: IOException) {
            0 // Belum ada file, fallback ke synth tone
        }
    }

    fun setMuted(muted: Boolean) {
        this.isMuted = muted
    }

    fun playClick() {
        if (isMuted) return
        if (soundClickId != 0) {
            soundPool.play(soundClickId, 0.7f, 0.7f, 0, 0, 1.0f)
        } else {
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        }
    }

    fun playBenar() {
        if (isMuted) return
        if (soundBenarId != 0) {
            soundPool.play(soundBenarId, 1.0f, 1.0f, 1, 0, 1.0f)
        } else {
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
        }
    }

    fun playSalah() {
        if (isMuted) return
        if (soundSalahId != 0) {
            soundPool.play(soundSalahId, 0.8f, 0.8f, 1, 0, 1.0f)
        } else {
            toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 220)
        }
    }

    fun playKoin() {
        if (isMuted) return
        if (soundKoinId != 0) {
            soundPool.play(soundKoinId, 0.9f, 0.9f, 1, 0, 1.2f)
        } else {
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        }
    }

    fun playBintang() {
        if (isMuted) return
        if (soundBintangId != 0) {
            soundPool.play(soundBintangId, 1.0f, 1.0f, 1, 0, 1.1f)
        } else {
            toneGen.startTone(ToneGenerator.TONE_PROP_PROMPT, 250)
        }
    }

    fun release() {
        soundPool.release()
    }
}
