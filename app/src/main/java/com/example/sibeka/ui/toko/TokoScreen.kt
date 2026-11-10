package com.example.sibeka.ui.toko

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset
import kotlinx.coroutines.launch

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

@Composable
fun TokoScreen(
    onBukaPeta: () -> Unit,
    onBukaKoleksi: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val repository = SiBekaApp.instance.container.repository
    val dataStore = repository.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    val totalKoin by dataStore.totalKoin.collectAsState(initial = 100)
    val totalBintang by dataStore.totalBintang.collectAsState(initial = 0)
    val aksesoriAktif by dataStore.aksesoriAktif.collectAsState(initial = "default")
    val aksesoriTerbeli by dataStore.aksesoriTerbeli.collectAsState(initial = setOf("default"))

    var pesan by remember { mutableStateOf("") }

    val daftarAksesori = listOf(
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
    val itemAktif = daftarAksesori.find { it.id == aksesoriAktif } ?: daftarAksesori.first()

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            HeaderJelajahAlam(
                judul = "Toko Si Beka",
                subjudul = "Perlengkapan seru untuk petualanganmu."
            ) { ChipBintang(totalBintang); ChipKoin(totalKoin) }
        },
        bottomBar = {
            SiBekaBottomBar(
                terpilih = TabUtama.TOKO,
                onPeta = { sfxPlayer.playClick(); onBukaPeta() },
                onKoleksi = { sfxPlayer.playClick(); onBukaKoleksi() },
                onToko = {}
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Pratinjau maskot
            item(span = { GridItemSpan(maxLineSpan) }) {
                KartuSiBeka(Modifier.fillMaxWidth(), container = HijauBenarMuda, borderColor = HijauBenarMuda, shape = RoundedCornerShape(24.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (itemAktif.maskot != null) {
                            GambarAset(
                                nama = itemAktif.maskot,
                                cadangan = itemAktif.emoji,
                                modifier = Modifier.size(width = 116.dp, height = 142.dp),
                                ukuranCadangan = 54.sp,
                                folder = "maskot"
                            )
                        } else {
                            MaskotSiBeka(size = 110.dp, melompat = true)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Gayaku hari ini!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                            Text(itemAktif.deskripsi, fontSize = 12.sp, lineHeight = 16.sp, color = HitamLembut)
                            PillLabel("${itemAktif.nama.uppercase()} · DIPAKAI", container = PutihBersih, icon = Icons.Default.Check)
                        }
                    }
                }
            }

            if (pesan.isNotBlank()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(KuningMuda).padding(10.dp)) {
                        Text(pesan, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoklatTua, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Aksesori Si Beka", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, modifier = Modifier.padding(top = 4.dp))
            }

            items(daftarAksesori) { item ->
                val dimiliki = aksesoriTerbeli.contains(item.id)
                val dipakai = aksesoriAktif == item.id

                KartuSiBeka(
                    Modifier.fillMaxWidth(),
                    container = PutihBersih,
                    borderColor = if (dipakai) HijauBenar else GarisKartu,
                    borderWidth = if (dipakai) 1.5.dp else 1.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            Modifier.fillMaxWidth().height(88.dp).clip(RoundedCornerShape(12.dp)).background(HijauBenarMuda.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.gambar != null) {
                                GambarAset(
                                    nama = item.gambar,
                                    cadangan = item.emoji,
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    ukuranCadangan = 36.sp,
                                    folder = "toko"
                                )
                            } else {
                                Text(item.emoji, fontSize = 38.sp)
                            }
                        }

                        Text(item.nama, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, maxLines = 1)

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                if (item.harga == 0) "Gratis" else "${item.harga} koin",
                                fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = OranjeAksen
                            )
                            when {
                                dipakai -> TombolMungil("Dipakai", HijauBenarMuda, HijauTua)
                                dimiliki -> TombolMungil("Pakai", GarisKartu, HijauTua) {
                                    sfxPlayer.playClick()
                                    coroutineScope.launch {
                                        dataStore.pasangAksesori(item.id)
                                        pesan = "${item.nama} berhasil dipakai!"
                                    }
                                }
                                else -> TombolMungil("Beli", HijauTua, PutihBersih) {
                                    coroutineScope.launch {
                                        val berhasil = dataStore.beliAksesori(item.id, item.harga)
                                        if (berhasil) {
                                            sfxPlayer.playKoin()
                                            pesan = "Berhasil membeli ${item.nama}! 🎉"
                                        } else {
                                            sfxPlayer.playSalah()
                                            pesan = "Koinmu belum cukup! Kumpulkan dari kuis ya! 😊"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Koin didapat dari bermain, bukan uang sungguhan.",
                    fontSize = 11.sp, color = AbuTua, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TombolMungil(
    teks: String,
    latar: Color,
    warnaTeks: Color,
    onClick: (() -> Unit)? = null
) {
    val bentuk = RoundedCornerShape(10.dp)
    val dasar = Modifier.clip(bentuk).background(latar)
    Box(
        (if (onClick != null) dasar.clickable(onClick = onClick) else dasar)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(teks, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = warnaTeks)
    }
}