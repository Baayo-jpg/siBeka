package com.example.sibeka.ui.kuis

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.theme.BiruLaut
import com.example.sibeka.ui.theme.HijauUtama
import com.example.sibeka.ui.theme.OranjeAksen

/**
 * Seluruh teks, warna, dan ikon satu mode kuis.
 *
 * Satu sumber kebenaran: layar pilih tantangan, simpul peta, dan header kuis
 * membaca dari sini, jadi menambah atau mengubah mode tidak perlu menyunting
 * tiga tempat.
 */
data class ModeKuisInfo(
    val judul: String,
    val deskripsi: String,
    val warna: Color,
    val ikon: ImageVector,
    /** Label pendek untuk simpul peta bila ikon asetnya belum tersedia. */
    val inisial: String,
    val judulKuis: String,
    val instruksi: String,
    val hintBubble: String,
    val hintTombol: String
)

val ModeKuis.info: ModeKuisInfo
    get() = when (this) {
        ModeKuis.SUSUN_HURUF -> ModeKuisInfo(
            judul = "Susun Huruf",
            deskripsi = "Susun huruf teracak menjadi nama flora atau fauna yang tepat",
            warna = HijauUtama,
            ikon = Icons.Outlined.TextFields,
            inisial = "SH",
            judulKuis = "Susun nama spesiesnya!",
            instruksi = "Pilih huruf untuk mengisi kotak-kotak ini.",
            hintBubble = "Ketuk huruf satu per satu sampai namanya lengkap. Salah pilih? Hapus huruf terakhir ya!",
            hintTombol = "Susun huruf di atas"
        )
        ModeKuis.PASANGKAN -> ModeKuisInfo(
            judul = "Pasangkan Gambar",
            deskripsi = "Cocokkan hewan atau tumbuhan dengan pasangan makanannya",
            warna = BiruLaut,
            ikon = Icons.Outlined.Extension,
            inisial = "PG",
            judulKuis = "Temukan pasangannya!",
            instruksi = "Pilih satu kartu yang paling cocok.",
            hintBubble = "Perhatikan gambarnya baik-baik, lalu pilih pasangan yang paling cocok.",
            hintTombol = "Pilih pasangan di atas"
        )
        ModeKuis.POTONGAN_GAMBAR -> ModeKuisInfo(
            judul = "Potongan Gambar",
            deskripsi = "Buka sedikit potongan penutup untuk menebak gambarnya",
            warna = OranjeAksen,
            ikon = Icons.Outlined.GridOn,
            inisial = "PT",
            judulKuis = "Buka sedikit, tebak cepat!",
            instruksi = "Makhluk apakah yang bersembunyi ini?",
            hintBubble = "Buka potongan satu per satu. Makin sedikit yang dibuka, makin banyak poinnya!",
            hintTombol = "Pilih jawaban di atas"
        )
    }

/** Urutan tampil mode pada peta dan layar pilih tantangan. */
val URUTAN_MODE: List<ModeKuis> = listOf(
    ModeKuis.SUSUN_HURUF,
    ModeKuis.PASANGKAN,
    ModeKuis.POTONGAN_GAMBAR
)
