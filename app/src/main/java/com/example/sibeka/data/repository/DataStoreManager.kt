package com.example.sibeka.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sibeka.data.model.AturanPermainan
import com.example.sibeka.data.model.ModeKuis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sibeka_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_NAMA_PEMAIN = stringPreferencesKey("nama_pemain")
        val KEY_KOIN = intPreferencesKey("total_koin")
        val KEY_BINTANG = intPreferencesKey("total_bintang")
        val KEY_AKSESORI_AKTIF = stringPreferencesKey("aksesori_aktif")
        val KEY_AKSESORI_TERBELI = stringSetPreferencesKey("aksesori_terbeli")
        val KEY_SFX_MUTED = booleanPreferencesKey("sfx_muted")

        /** Awalan kunci skor terbaik per habitat+mode, mis. `skor_terbaik_GN:PASANGKAN`. */
        private const val PREFIX_SKOR = "skor_terbaik_"

        /** Kunci skor terbaik untuk satu pasangan habitat dan mode kuis. */
        fun kunciSkor(habitatId: String, mode: ModeKuis): String = "$habitatId:${mode.name}"
    }

    val namaPemain: Flow<String> = context.dataStore.data.map { it[KEY_NAMA_PEMAIN] ?: "" }
    val totalKoin: Flow<Int> = context.dataStore.data.map { it[KEY_KOIN] ?: AturanPermainan.KOIN_AWAL }
    val totalBintang: Flow<Int> = context.dataStore.data.map { it[KEY_BINTANG] ?: 0 }
    val aksesoriAktif: Flow<String> = context.dataStore.data.map { it[KEY_AKSESORI_AKTIF] ?: "default" }
    val aksesoriTerbeli: Flow<Set<String>> = context.dataStore.data.map { it[KEY_AKSESORI_TERBELI] ?: setOf("default") }
    val sfxMuted: Flow<Boolean> = context.dataStore.data.map { it[KEY_SFX_MUTED] ?: false }

    /** Peta skor terbaik: kunci `habitatId:MODE` → poin tertinggi. */
    val skorTerbaik: Flow<Map<String, Int>> = context.dataStore.data.map { prefs ->
        prefs.asMap().entries.mapNotNull { (kunci, nilai) ->
            if (kunci.name.startsWith(PREFIX_SKOR) && nilai is Int) {
                kunci.name.removePrefix(PREFIX_SKOR) to nilai
            } else {
                null
            }
        }.toMap()
    }

    suspend fun simpanNamaPemain(nama: String) {
        context.dataStore.edit { it[KEY_NAMA_PEMAIN] = nama }
    }

    suspend fun tambahKoin(jumlah: Int) {
        context.dataStore.edit {
            val current = it[KEY_KOIN] ?: AturanPermainan.KOIN_AWAL
            it[KEY_KOIN] = (current + jumlah).coerceAtLeast(0)
        }
    }

    suspend fun kurangiKoin(jumlah: Int): Boolean {
        var sukses = false
        context.dataStore.edit {
            val current = it[KEY_KOIN] ?: AturanPermainan.KOIN_AWAL
            if (current >= jumlah) {
                it[KEY_KOIN] = current - jumlah
                sukses = true
            }
        }
        return sukses
    }

    suspend fun tambahBintang(jumlah: Int) {
        context.dataStore.edit {
            val current = it[KEY_BINTANG] ?: 0
            it[KEY_BINTANG] = current + jumlah
        }
    }

    suspend fun pasangAksesori(namaAksesori: String) {
        context.dataStore.edit { it[KEY_AKSESORI_AKTIF] = namaAksesori }
    }

    suspend fun beliAksesori(namaAksesori: String, harga: Int): Boolean {
        var berhasil = false
        context.dataStore.edit { prefs ->
            val currentKoin = prefs[KEY_KOIN] ?: AturanPermainan.KOIN_AWAL
            if (currentKoin >= harga) {
                prefs[KEY_KOIN] = currentKoin - harga
                val currentSet = prefs[KEY_AKSESORI_TERBELI]?.toMutableSet() ?: mutableSetOf("default")
                currentSet.add(namaAksesori)
                prefs[KEY_AKSESORI_TERBELI] = currentSet
                prefs[KEY_AKSESORI_AKTIF] = namaAksesori
                berhasil = true
            }
        }
        return berhasil
    }

    /**
     * Menyimpan skor bila lebih tinggi dari rekor sebelumnya, atau bila mode ini
     * baru pertama kali dimainkan (walau skornya 0) supaya layar pilih tantangan
     * bisa menandai mode yang sudah dicoba.
     */
    suspend fun simpanSkorTerbaik(kunci: String, skor: Int) {
        context.dataStore.edit { prefs ->
            val prefKey = intPreferencesKey(PREFIX_SKOR + kunci)
            val rekorLama = prefs[prefKey]
            if (rekorLama == null || skor > rekorLama) prefs[prefKey] = skor
        }
    }

    suspend fun toggleSfx(muted: Boolean) {
        context.dataStore.edit { it[KEY_SFX_MUTED] = muted }
    }
}
