package com.example.sibeka.ui.koleksi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.HabitatEntity
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

/** Nilai filter untuk "semua habitat" pada daftar koleksi. */
const val FILTER_SEMUA_HABITAT = "SEMUA"

data class KoleksiUiState(
    val habitatTerurut: List<HabitatEntity> = emptyList(),
    val semuaObjek: List<ObjekEntity> = emptyList(),
    val collectedIds: Set<String> = emptySet(),
    val totalKoin: Int = AturanPermainan.KOIN_AWAL,
    val totalBintang: Int = 0,
    val filterHabitatId: String = FILTER_SEMUA_HABITAT,
    val objekTerpilih: ObjekEntity? = null
) {
    /** Objek yang ditampilkan sesuai filter habitat aktif. */
    val objekTampil: List<ObjekEntity>
        get() = if (filterHabitatId == FILTER_SEMUA_HABITAT) {
            semuaObjek
        } else {
            semuaObjek.filter { it.idHabitat == filterHabitatId }
        }

    val totalSpesies: Int get() = semuaObjek.size

    val ditemukan: Int get() = semuaObjek.count { it.idObjek in collectedIds }

    /** Persentase koleksi yang sudah lengkap, 0..100. */
    val persen: Int get() = ditemukan * 100 / totalSpesies.coerceAtLeast(1)

    /** Jumlah spesies yang sudah ditemukan pada satu habitat. */
    fun jumlahDitemukan(habitatId: String): Int =
        semuaObjek.count { it.idHabitat == habitatId && it.idObjek in collectedIds }

    /** Nama habitat untuk ditampilkan; kosong bila id tidak dikenal. */
    fun namaHabitat(habitatId: String): String =
        habitatTerurut.firstOrNull { it.id == habitatId }?.namaHabitat ?: ""
}

class KoleksiViewModel(
    private val repository: SiBekaRepository,
    private val sfxPlayer: SfxPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(KoleksiUiState())
    val uiState: StateFlow<KoleksiUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllHabitats().collect { habitats ->
                _uiState.update { it.copy(habitatTerurut = habitats.urutkanHabitat()) }
            }
        }
        viewModelScope.launch {
            repository.getAllKoleksi().collect { koleksi ->
                _uiState.update { it.copy(collectedIds = koleksi.map { k -> k.idObjek }.toSet()) }
            }
        }
        viewModelScope.launch {
            val objek = repository.getAllObjek()
            _uiState.update { it.copy(semuaObjek = objek) }
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
    }

    fun pilihFilter(habitatId: String) {
        _uiState.update { it.copy(filterHabitatId = habitatId) }
    }

    /** Buka detail bila spesies sudah ditemukan; bunyikan nada salah bila masih terkunci. */
    fun bukaDetail(objek: ObjekEntity, terbuka: Boolean) {
        if (terbuka) {
            sfxPlayer.playClick()
            _uiState.update { it.copy(objekTerpilih = objek) }
        } else {
            sfxPlayer.playSalah()
        }
    }

    fun tutupDetail(putarSuara: Boolean = true) {
        if (putarSuara) sfxPlayer.playClick()
        _uiState.update { it.copy(objekTerpilih = null) }
    }

    /** Efek klik untuk tombol navigasi di layar ini. */
    fun klik() = sfxPlayer.playClick()

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val container = SiBekaApp.instance.container
                return KoleksiViewModel(container.repository, container.sfxPlayer) as T
            }
        }
    }
}
