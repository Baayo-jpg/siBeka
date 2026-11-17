package com.example.sibeka.data.model

import com.example.sibeka.data.db.HabitatEntity

/** Urutan tampil habitat pada jalur belajar, bukan urutan alfabet id. */
val URUTAN_HABITAT = listOf("GN", "HH", "RW", "SG", "LT")

/** Format nama resmi habitat untuk header, pemisah jalur, dan banner. */
fun namaTampilHabitat(habitatId: String, defaultNama: String = ""): String = when (habitatId) {
    "GN" -> "Gunung"
    "HH" -> "Hutan Hujan Tropis"
    "RW" -> "Rawa"
    "SG" -> "Sungai"
    "LT" -> "Lautan"
    else -> defaultNama.ifBlank { "Habitat" }
}

/** Urutkan habitat sesuai [URUTAN_HABITAT]; id yang tidak dikenal diletakkan di akhir. */
fun List<HabitatEntity>.urutkanHabitat(): List<HabitatEntity> =
    sortedBy { habitat ->
        URUTAN_HABITAT.indexOf(habitat.id).let { if (it < 0) URUTAN_HABITAT.size else it }
    }

/**
 * Aturan ekonomi dan sesi permainan — satu sumber kebenaran supaya
 * perubahan hadiah tidak perlu disunting di banyak tempat.
 */
object AturanPermainan {
    /** Saldo koin awal pemain baru. */
    const val KOIN_AWAL = 100

    /** Hadiah untuk spesies yang baru pertama kali ditemukan. */
    const val KOIN_PER_TEMUAN = 20
    const val BINTANG_PER_TEMUAN = 1

    /** Jumlah soal dalam satu sesi kuis. */
    const val JUMLAH_SOAL_PER_SESI = 5

    /** Aturan mode Potongan Gambar. */
    const val JUMLAH_UBIN_POTONGAN = 6
    const val POIN_POTONGAN_AWAL = 100
    const val PENGURANGAN_POIN_PER_UBIN = 15
    const val POIN_POTONGAN_MINIMAL = 20
}
