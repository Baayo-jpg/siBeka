package com.example.sibeka.data.model

/**
 * Data model untuk tabel HABITAT.
 * 5 wilayah: hutan_hujan_tropis, laut, sungai, rawa, gunung
 */
data class Habitat(
    val id: String,           // "HH", "LT", "SG", "RW", "GN"
    val namaHabitat: String,  // "Hutan Hujan", "Laut", dll.
    val assetFolder: String  // Nama folder di assets/ (e.g. "hutan_hujan_tropis")
)

/**
 * Data model untuk tabel OBJEK_FLORA_FAUNA.
 * Mewakili 1 spesies flora atau fauna.
 */
data class ObjekFloraFauna(
    val idObjek: String,          // "HH-F-001", "LT-FL-001", dll.
    val idHabitat: String,        // FK ke Habitat.id
    val kategori: String,         // "fauna" atau "flora"
    val namaObjek: String,        // "Beruang Madu"
    val namaLatin: String,
    val namaInternasional: String,
    val ciriCiri: String,
    val sebaran: String,
    val statusKonservasi: String,
    val kataKunci: String,        // "BERUANG" (untuk susun huruf)
    val faktaUnik: String,        // "Beruang madu adalah beruang terkecil..."
    val gambarUtamaPath: String,  // "hutan_hujan_tropis/fauna/beruang_madu.svg"
    val gambarPasanganPath: String // "hutan_hujan_tropis/pasangan/pasangan_madu.svg"
)

/**
 * Data model untuk tabel BUKU_KOLEKSI.
 * Menyimpan spesies yang sudah berhasil ditebak pemain.
 */
data class BukuKoleksi(
    val id: Long = 0,
    val idObjek: String,       // FK ke ObjekFloraFauna.idObjek
    val waktuDitemukan: Long = System.currentTimeMillis()
)

/**
 * Data model untuk progres pemain (disimpan via DataStore,
 * bukan Room, karena bersifat key-value sederhana).
 */
data class ProgresPemain(
    val namaPemain: String = "",
    val totalKoin: Int = 0,
    val totalBintang: Int = 0,
    val aksesoriAktif: String = ""   // e.g. "topi_petualang"
)

/**
 * Enum untuk 5 mode kuis yang tersedia.
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
