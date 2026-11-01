package com.example.sibeka.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
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
        val KEY_BGM_MUTED = booleanPreferencesKey("bgm_muted")
        val KEY_SFX_MUTED = booleanPreferencesKey("sfx_muted")
    }

    val namaPemain: Flow<String> = context.dataStore.data.map { it[KEY_NAMA_PEMAIN] ?: "" }
    val totalKoin: Flow<Int> = context.dataStore.data.map { it[KEY_KOIN] ?: 100 }
    val totalBintang: Flow<Int> = context.dataStore.data.map { it[KEY_BINTANG] ?: 0 }
    val aksesoriAktif: Flow<String> = context.dataStore.data.map { it[KEY_AKSESORI_AKTIF] ?: "default" }
    val aksesoriTerbeli: Flow<Set<String>> = context.dataStore.data.map { it[KEY_AKSESORI_TERBELI] ?: setOf("default") }
    val bgmMuted: Flow<Boolean> = context.dataStore.data.map { it[KEY_BGM_MUTED] ?: false }
    val sfxMuted: Flow<Boolean> = context.dataStore.data.map { it[KEY_SFX_MUTED] ?: false }

    suspend fun simpanNamaPemain(nama: String) {
        context.dataStore.edit { it[KEY_NAMA_PEMAIN] = nama }
    }

    suspend fun tambahKoin(jumlah: Int) {
        context.dataStore.edit {
            val current = it[KEY_KOIN] ?: 100
            it[KEY_KOIN] = (current + jumlah).coerceAtLeast(0)
        }
    }

    suspend fun kurangiKoin(jumlah: Int): Boolean {
        var sukses = false
        context.dataStore.edit {
            val current = it[KEY_KOIN] ?: 100
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
            val currentKoin = prefs[KEY_KOIN] ?: 100
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

    suspend fun toggleBgm(muted: Boolean) {
        context.dataStore.edit { it[KEY_BGM_MUTED] = muted }
    }

    suspend fun toggleSfx(muted: Boolean) {
        context.dataStore.edit { it[KEY_SFX_MUTED] = muted }
    }
}
