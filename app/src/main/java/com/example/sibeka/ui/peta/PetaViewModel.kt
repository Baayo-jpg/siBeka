package com.example.sibeka.ui.peta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.HabitatEntity
import com.example.sibeka.data.db.KoleksiEntity
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.model.AturanPermainan
import com.example.sibeka.data.model.urutkanHabitat
import com.example.sibeka.data.repository.SiBekaRepository
import com.example.sibeka.util.SfxPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetaUiState(
    val habitatTerurut: List<HabitatEntity> = emptyList(),
    val jumlahByHabitat: Map<String, Int> = emptyMap(),
    val ditemukanByHabitat: Map<String, Int> = emptyMap(),
    /** Habitat pertama yang masih punya spesies belum ditemukan. */
    val habitatAktif: String? = null,
    val namaPemain: String = "",
    val totalKoin: Int = AturanPermainan.KOIN_AWAL,
    val totalBintang: Int = 0,
    val sfxMuted: Boolean = false
) {
    /** Total spesies pada satu habitat. */
    fun jumlah(habitatId: String): Int = jumlahByHabitat[habitatId] ?: 0

    /** Spesies yang sudah ditemukan pada satu habitat. */
    fun ditemukan(habitatId: String): Int = ditemukanByHabitat[habitatId] ?: 0

    /** True bila semua spesies habitat itu sudah dikumpulkan. */
    fun lengkap(habitatId: String): Boolean = jumlah(habitatId).let { total ->
        total > 0 && ditemukan(habitatId) >= total
    }
}

class PetaViewModel(
    private val repository: SiBekaRepository,
    private val sfxPlayer: SfxPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetaUiState())
    val uiState: StateFlow<PetaUiState> = _uiState.asStateFlow()

    private var habitatList: List<HabitatEntity> = emptyList()
    private var semuaObjek: List<ObjekEntity> = emptyList()
    private var koleksi: List<KoleksiEntity> = emptyList()

    init {
        viewModelScope.launch {
            repository.getAllHabitats().collect { habitats ->
                habitatList = habitats
                hitungProgres()
            }
        }
        viewModelScope.launch {
            repository.getAllKoleksi().collect { daftar ->
                koleksi = daftar
                hitungProgres()
            }
        }
        viewModelScope.launch {
            semuaObjek = repository.getAllObjek()
            hitungProgres()
        }
        viewModelScope.launch {
            repository.dataStore.namaPemain.collect { nama ->
                _uiState.update { it.copy(namaPemain = nama) }
            }
        }
        viewModelScope.launch {
            repository.dataStore.totalKoin.collect { koin ->
                _uiState.update { it.copy(totalKoin = koin) }
            }
        }
        viewModelScope.launch {
            repository.dataStore.totalBintang.collect { bintang ->
                _uiState.update { it.copy(totalBintang = bintang) }
            }
        }
        viewModelScope.launch {
            repository.dataStore.sfxMuted.collect { muted ->
                sfxPlayer.setMuted(muted)
                _uiState.update { it.copy(sfxMuted = muted) }
            }
        }
    }

    private fun hitungProgres() {
        val terurut = habitatList.urutkanHabitat()
        val habitatById = semuaObjek.associate { it.idObjek to it.idHabitat }
        val jumlah = semuaObjek.groupingBy { it.idHabitat }.eachCount()
        val ditemukan = koleksi.mapNotNull { habitatById[it.idObjek] }.groupingBy { it }.eachCount()
        val aktif = terurut.firstOrNull { habitat ->
            (ditemukan[habitat.id] ?: 0) < (jumlah[habitat.id] ?: 0)
        }?.id ?: terurut.firstOrNull()?.id

        _uiState.update {
            it.copy(
                habitatTerurut = terurut,
                jumlahByHabitat = jumlah,
                ditemukanByHabitat = ditemukan,
                habitatAktif = aktif
            )
        }
    }

    fun toggleSuara() {
        viewModelScope.launch {
            val baru = !_uiState.value.sfxMuted
            repository.dataStore.toggleSfx(baru)
            sfxPlayer.setMuted(baru)
        }
    }

    /** Efek klik untuk tombol navigasi dan simpul jalur. */
    fun klik() = sfxPlayer.playClick()

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val container = SiBekaApp.instance.container
                return PetaViewModel(container.repository, container.sfxPlayer) as T
            }
        }
    }
}
