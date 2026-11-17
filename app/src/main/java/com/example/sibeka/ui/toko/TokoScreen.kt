package com.example.sibeka.ui.toko

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokoScreen(
    onBukaPeta: () -> Unit,
    onBukaKoleksi: () -> Unit,
    onBukaTentang: () -> Unit
) {
    val viewModel: TokoViewModel = viewModel(factory = TokoViewModel.Factory())
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val itemAktif = ui.itemAktif
    var opsiTerbuka by remember { mutableStateOf(false) }

    if (opsiTerbuka) {
        ModalBottomSheet(
            onDismissRequest = { opsiTerbuka = false },
            containerColor = LatarKrem
        ) {
            LembarOpsi(
                onTentang = {
                    opsiTerbuka = false
                    viewModel.klik()
                    onBukaTentang()
                }
            )
        }
    }

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            HeaderJelajahAlam(
                judul = "Toko Si Beka",
                subjudul = "Perlengkapan seru untuk petualanganmu.",
                // Opsi berupa pil berlabel agar sejajar dengan baris subjudul,
                // bukan tombol bulat yang berdiri sendiri.
                aksiSubjudul = {
                    Row(
                        Modifier.clip(RoundedCornerShape(50)).background(PutihBersih)
                            .border(1.dp, GarisKartu, RoundedCornerShape(50))
                            .clickable { viewModel.klik(); opsiTerbuka = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Settings, null, tint = HijauTua,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            "OPSI", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                            color = HijauTua, letterSpacing = 0.6.sp
                        )
                    }
                }
            ) { ChipBintang(ui.totalBintang); ChipKoin(ui.totalKoin) }
        },
        bottomBar = {
            SiBekaBottomBar(
                terpilih = TabUtama.TOKO,
                onPeta = { viewModel.klik(); onBukaPeta() },
                onKoleksi = { viewModel.klik(); onBukaKoleksi() },
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
                            // Nama aksesori dipisah dari pil status: pil dibuat ringkas
                            // supaya tidak pecah dua baris di layar 360 dp.
                            Text(
                                itemAktif.nama, fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold, color = HijauBenar
                            )
                            Text(itemAktif.deskripsi, fontSize = 12.sp, lineHeight = 16.sp, color = HitamLembut)
                            PillLabel("DIPAKAI", container = PutihBersih, icon = Icons.Default.Check)
                        }
                    }
                }
            }

            if (ui.pesan.isNotBlank()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(KuningMuda).padding(10.dp)) {
                        Text(ui.pesan, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoklatTua, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Aksesori Si Beka", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, modifier = Modifier.padding(top = 4.dp))
            }

            items(ui.daftarAksesori) { item ->
                val dimiliki = ui.aksesoriTerbeli.contains(item.id)
                val dipakai = ui.aksesoriAktif == item.id

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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (item.harga > 0) {
                                    KoinKecil(
                                        ukuran = 16.dp,
                                        ukuranCadangan = 11.sp,
                                        nama = "koin_miring"
                                    )
                                }
                                Text(
                                    if (item.harga == 0) "Gratis" else "${item.harga} koin",
                                    fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = OranjeAksen
                                )
                            }
                            when {
                                dipakai -> TombolMungil("Dipakai", HijauBenarMuda, HijauTua)
                                dimiliki -> TombolMungil("Pakai", GarisKartu, HijauTua) {
                                    viewModel.pakaiAksesori(item)
                                }
                                else -> TombolMungil("Beli", HijauTua, PutihBersih) {
                                    viewModel.beliAksesori(item)
                                }
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GambarAset(
                        nama = "koin_tumpuk3",
                        cadangan = "🪙",
                        modifier = Modifier.size(width = 24.dp, height = 28.dp),
                        ukuranCadangan = 16.sp,
                        folder = "koin"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Koin didapat dari bermain, bukan uang sungguhan.",
                        fontSize = 11.sp, color = AbuTua, textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
private fun LembarOpsi(onTentang: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().navigationBarsPadding()
            .padding(horizontal = 20.dp).padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Opsi", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
        }

        KartuSiBeka(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            onClick = onTentang
        ) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(38.dp).clip(CircleShape).background(HijauBenarMuda),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Info, null, tint = HijauTua,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Tentang Si Beka", fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold, color = HijauTua
                    )
                    Text(
                        "Kredit gambar, tim pengembang, dan info aplikasi",
                        fontSize = 11.5.sp, lineHeight = 15.sp, color = AbuTua
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AbuTua,
                    modifier = Modifier.size(20.dp)
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