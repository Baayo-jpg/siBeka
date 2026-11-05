package com.example.sibeka.ui.kuis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.data.model.StatusJawaban
import com.example.sibeka.data.repository.SiBekaRepository
import com.example.sibeka.util.SfxPlayer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class KuisUiState(
    val isLoading: Boolean = true,
    val habitatId: String = "HH",
    val namaHabitat: String = "Hutan Hujan",
    val modeKuis: ModeKuis = ModeKuis.SUSUN_HURUF,
    val daftarSoal: List<ObjekEntity> = emptyList(),
    val currentSoalIndex: Int = 0,
    val statusJawaban: StatusJawaban = StatusJawaban.BELUM,
    val totalBintang: Int = 0,
    val skorSesi: Int = 0,
    val isKuisSelesai: Boolean = false,
    // State khusus Mode Susun Huruf
    val hurufTersedia: List<Char> = emptyList(),
    val hurufTerpilih: List<Char> = emptyList(),
    // State khusus Pilihan Ganda (Pasangkan)
    val opsiJawaban: List<ObjekEntity> = emptyList(),
    val opsiTerpilihId: String? = null,
    // State khusus Potongan Gambar (6 potongan: true = terbuka, false = tertutup)
    val potonganTerbuka: List<Boolean> = List(6) { false },
    val poinPotonganTersisa: Int = 100
)

class KuisViewModel(
    private val repository: SiBekaRepository,
    private val sfxPlayer: SfxPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(KuisUiState())
    val uiState: StateFlow<KuisUiState> = _uiState.asStateFlow()

    val totalKoin: StateFlow<Int> = repository.dataStore.totalKoin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    val currentSoal: ObjekEntity?
        get() = _uiState.value.daftarSoal.getOrNull(_uiState.value.currentSoalIndex)

    fun mulaiKuis(habitatId: String, mode: ModeKuis) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, habitatId = habitatId, modeKuis = mode) }
            val habitat = repository.getHabitatById(habitatId)
            val namaHab = habitat?.namaHabitat ?: "Habitat"

            val soalList = repository.getSoalByHabitat(habitatId, count = 5)
            if (soalList.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        namaHabitat = namaHab,
                        daftarSoal = soalList,
                        currentSoalIndex = 0,
                        statusJawaban = StatusJawaban.BELUM,
                        totalBintang = 0,
                        skorSesi = 0,
                        isKuisSelesai = false
                    )
                }
                setupSoalAktif()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun setupSoalAktif() {
        val soal = currentSoal ?: return
        val mode = _uiState.value.modeKuis

        when (mode) {
            ModeKuis.SUSUN_HURUF -> {
                val keyword = soal.kataKunci.uppercase()
                val scrambled = keyword.toList().shuffled()
                _uiState.update {
                    it.copy(
                        hurufTersedia = scrambled,
                        hurufTerpilih = emptyList(),
                        statusJawaban = StatusJawaban.BELUM
                    )
                }
            }
            ModeKuis.PASANGKAN -> {
                val decoys = repository.getDecoyAnswers(
                    habitatId = _uiState.value.habitatId,
                    excludeIds = listOf(soal.idObjek),
                    count = 2
                )
                val allOptions = (decoys + soal).shuffled()
                _uiState.update {
                    it.copy(
                        opsiJawaban = allOptions,
                        opsiTerpilihId = null,
                        statusJawaban = StatusJawaban.BELUM
                    )
                }
            }
            ModeKuis.POTONGAN_GAMBAR -> {
                val decoys = repository.getDecoyAnswers(
                    habitatId = _uiState.value.habitatId,
                    excludeIds = listOf(soal.idObjek),
                    count = 2
                )
                val allOptions = (decoys + soal).shuffled()
                _uiState.update {
                    it.copy(
                        opsiJawaban = allOptions,
                        opsiTerpilihId = null,
                        potonganTerbuka = List(6) { false },
                        poinPotonganTersisa = 100,
                        statusJawaban = StatusJawaban.BELUM
                    )
                }
            }
        }
    }

    // ── Input & Jawaban Pemain ───────────────────────────────────────────────

    fun pilihHuruf(indexDiTersedia: Int) {
        val state = _uiState.value
        if (state.statusJawaban != StatusJawaban.BELUM) return
        val available = state.hurufTersedia.toMutableList()
        val chosen = state.hurufTerpilih.toMutableList()

        if (indexDiTersedia in available.indices) {
            val char = available.removeAt(indexDiTersedia)
            chosen.add(char)
            sfxPlayer.playClick()

            _uiState.update {
                it.copy(hurufTersedia = available, hurufTerpilih = chosen)
            }

            val targetWord = currentSoal?.kataKunci?.uppercase() ?: ""
            if (chosen.size == targetWord.length) {
                val jawabanUser = chosen.joinToString("")
                evaluasiJawaban(jawabanUser == targetWord)
            }
        }
    }

    fun hapusHurufTerakhir() {
        val state = _uiState.value
        if (state.statusJawaban != StatusJawaban.BELUM) return
        val available = state.hurufTersedia.toMutableList()
        val chosen = state.hurufTerpilih.toMutableList()

        if (chosen.isNotEmpty()) {
            val char = chosen.removeAt(chosen.size - 1)
            available.add(char)
            sfxPlayer.playClick()
            _uiState.update {
                it.copy(hurufTersedia = available, hurufTerpilih = chosen)
            }
        }
    }

    fun pilihOpsiJawaban(opsiId: String) {
        val state = _uiState.value
        if (state.statusJawaban != StatusJawaban.BELUM) return
        val soal = currentSoal ?: return

        sfxPlayer.playClick()
        _uiState.update { it.copy(opsiTerpilihId = opsiId) }

        val isBenar = (opsiId == soal.idObjek)
        evaluasiJawaban(isBenar)
    }

    fun bukaPotonganTile(index: Int) {
        val state = _uiState.value
        if (state.statusJawaban != StatusJawaban.BELUM) return
        val tiles = state.potonganTerbuka.toMutableList()

        if (index in tiles.indices && !tiles[index]) {
            tiles[index] = true
            val poinBaru = (state.poinPotonganTersisa - 15).coerceAtLeast(20)
            sfxPlayer.playClick()
            _uiState.update {
                it.copy(potonganTerbuka = tiles, poinPotonganTersisa = poinBaru)
            }
        }
    }

    private fun evaluasiJawaban(isBenar: Boolean) {
        val soal = currentSoal ?: return
        viewModelScope.launch {
            if (isBenar) {
                sfxPlayer.playBenar()
                repository.catatPenemuan(soal.idObjek)
                repository.dataStore.tambahKoin(20)
                repository.dataStore.tambahBintang(1)

                _uiState.update {
                    it.copy(
                        statusJawaban = StatusJawaban.BENAR,
                        totalBintang = it.totalBintang + 1,
                        skorSesi = it.skorSesi + 100
                    )
                }
            } else {
                sfxPlayer.playSalah()
                _uiState.update {
                    it.copy(statusJawaban = StatusJawaban.SALAH)
                }
            }
        }
    }

    fun lanjutKeSoalBerikutnya() {
        val state = _uiState.value
        val nextIndex = state.currentSoalIndex + 1

        if (nextIndex < state.daftarSoal.size) {
            _uiState.update {
                it.copy(currentSoalIndex = nextIndex, statusJawaban = StatusJawaban.BELUM)
            }
            viewModelScope.launch { setupSoalAktif() }
        } else {
            sfxPlayer.playBintang()
            _uiState.update { it.copy(isKuisSelesai = true) }
        }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val container = SiBekaApp.instance.container
                return KuisViewModel(
                    container.repository,
                    container.sfxPlayer
                ) as T
            }
        }
    }
}
