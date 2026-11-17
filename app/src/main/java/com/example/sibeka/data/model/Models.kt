package com.example.sibeka.data.model

/**
 * Tiga mode kuis yang tersedia.
 * Model data tabel (habitat, flora/fauna, koleksi) ada di `data/db/Entities.kt`.
 */
enum class ModeKuis {
    PASANGKAN,
    SUSUN_HURUF,
    POTONGAN_GAMBAR
}

/**
 * State jawaban pemain di satu soal kuis.
 */
enum class StatusJawaban {
    BELUM,   // Pemain belum memilih
    BENAR,   // Jawaban benar
    SALAH    // Jawaban salah
}
