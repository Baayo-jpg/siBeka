package com.example.sibeka.ui.toko

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.model.AturanPermainan
import com.example.sibeka.data.repository.DataStoreManager
import com.example.sibeka.util.SfxPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Satu barang di katalog toko. */
data class AksesoriItem(
    val id: String,
    val nama: String,
    val harga: Int,
    val emoji: String,
    val deskripsi: String,
    /** Gambar aksesoris di `assets/toko`, null bila belum ada. */
    val gambar: String? = null,
    /** Gambar maskot yang memakai aksesoris ini di `assets/maskot`. */
    val maskot: String? = null
)

/** Katalog toko — data tetap, jadi cukup didefinisikan sekali di sini. */
val DAFTAR_AKSESORI = listOf(
    AksesoriItem("default", "Bulu Alami", 0, "🦜", "Tampilan asli Si Beka yang ramah"),
    AksesoriItem(
        "topi_penjelajah", "Topi Rimba", 100, "🤠", "Siap menjelajah hutan lebat",
        gambar = "aksesori_topi", maskot = "pakai_topi"
    ),
    AksesoriItem(
        "kacamata_keren", "Kacamata Hitam", 150, "😎", "Tampil keren di bawah terik laut",
        gambar = "aksesori_kacamata", maskot = "pakai_kacamata"
    ),
    AksesoriItem(
        "mahkota_bunga", "Mahkota Bunga", 200, "🌸", "Harum semerbak dari flora rawa",
        gambar = "aksesori_mahkota", maskot = "pakai_mahkota"
    ),
    AksesoriItem(
        "helm_gunung", "Helm Gunung", 250, "🧗", "Pelindung aman di tebing tinggi",
        gambar = "aksesori_helm", maskot = "pakai_helm"
    )
)

data class TokoUiState(
    val totalKoin: Int = AturanPermainan.KOIN_AWAL,
    val totalBintang: Int = 0,
    val aksesoriAktif: String = "default",
    val aksesoriTerbeli: Set<String> = setOf("default"),
    val pesan: String = "",
    val daftarAksesori: List<AksesoriItem> = DAFTAR_AKSESORI
) {
    /** Aksesori yang sedang dipakai; jatuh ke item pertama bila id tidak dikenal. */
    val itemAktif: AksesoriItem
        get() = daftarAksesori.firstOrNull { it.id == aksesoriAktif } ?: daftarAksesori.first()
}

class TokoViewModel(
    private val dataStore: DataStoreManager,
    private val sfxPlayer: SfxPlayer
) : ViewModel() {

    private val pesan = MutableStateFlow("")

    val uiState: StateFlow<TokoUiState> = combine(
        dataStore.totalKoin,
        dataStore.totalBintang,
        dataStore.aksesoriAktif,
        dataStore.aksesoriTerbeli,
        pesan
    ) { koin, bintang, aktif, terbeli, pesan ->
        TokoUiState(
            totalKoin = koin,
            totalBintang = bintang,
            aksesoriAktif = aktif,
            aksesoriTerbeli = terbeli,
            pesan = pesan
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TokoUiState())

    fun pakaiAksesori(item: AksesoriItem) {
        viewModelScope.launch {
            sfxPlayer.playClick()
            dataStore.pasangAksesori(item.id)
            pesan.value = "${item.nama} berhasil dipakai!"
        }
    }

    fun beliAksesori(item: AksesoriItem) {
        viewModelScope.launch {
            if (dataStore.beliAksesori(item.id, item.harga)) {
                sfxPlayer.playKoin()
                pesan.value = "Berhasil membeli ${item.nama}! 🎉"
            } else {
                sfxPlayer.playSalah()
                pesan.value = "Koinmu belum cukup! Kumpulkan dari kuis ya! 😊"
            }
        }
    }

    /** Efek klik untuk tombol navigasi di layar ini. */
    fun klik() = sfxPlayer.playClick()

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val container = SiBekaApp.instance.container
                return TokoViewModel(container.dataStore, container.sfxPlayer) as T
            }
        }
    }
}
