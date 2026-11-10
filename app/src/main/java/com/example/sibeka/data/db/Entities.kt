package com.example.sibeka.data.db

data class HabitatEntity(
    val id: String,
    val namaHabitat: String,
    val assetFolder: String
)

data class ObjekEntity(
    val idObjek: String,
    val idHabitat: String,
    val kategori: String,         // "fauna" | "flora"
    val namaObjek: String,
    val namaLatin: String,
    val namaInternasional: String,
    val ciriCiri: String,
    val sebaran: String,
    val statusKonservasi: String,
    val kataKunci: String,        // Kata untuk mode susun huruf
    val faktaUnik: String,
    val gambarUtamaPath: String,  // Relatif dari assets/
    val gambarPasanganPath: String
)

data class KoleksiEntity(
    val id: Long = 0,
    val idObjek: String,
    val waktuDitemukan: Long = System.currentTimeMillis()
)
