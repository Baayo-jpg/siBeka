package com.example.sibeka.ui.peta

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.data.model.namaTampilHabitat
import com.example.sibeka.data.model.urutkanHabitat
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.kuis.URUTAN_MODE
import com.example.sibeka.ui.kuis.info
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset
import kotlinx.coroutines.launch

/** Jarak aman sebelum banner kepala selesai diukur. */
private val JARAK_AWAL_BANNER = 98.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetaScreen(
    onPilihHabitat: (habitatId: String) -> Unit,
    onMulaiKuis: (habitatId: String, mode: ModeKuis) -> Unit,
    /** `null` = koleksi semua habitat; id habitat = koleksi terfilter habitat itu. */
    onBukaKoleksi: (habitatId: String?) -> Unit,
    onBukaToko: () -> Unit
) {
    val viewModel: PetaViewModel = viewModel(factory = PetaViewModel.Factory())
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val habitatTerurut = ui.habitatTerurut
    val habitatAktif = ui.habitatAktif

    val listState = rememberLazyListState()

    // Tinggi banner kepala diukur langsung agar jarak daftar tidak bergantung angka tetap.
    var tinggiBannerPx by remember { mutableStateOf(0) }
    val kerapatan = LocalDensity.current
    val paddingAtasBanner = remember(tinggiBannerPx, kerapatan) {
        if (tinggiBannerPx > 0) with(kerapatan) { tinggiBannerPx.toDp() } + 16.dp else JARAK_AWAL_BANNER
    }

    // Deteksi habitat aktif berdasarkan koordinat Y item yang sedang berada di bawah header (~20% tinggi layar).
    // Mendukung scroll turun maupun naik dengan transisi yang akurat.
    val habitatTerlihatIndex by remember(habitatTerurut) {
        derivedStateOf {
            if (habitatTerurut.isEmpty()) return@derivedStateOf 0
            val viewportH = listState.layoutInfo.viewportSize.height
            val sampleY = if (viewportH > 0) viewportH * 0.22f else 320f

            var targetHabitatId = habitatTerurut.first().id
            var maxOffsetBelowSample = Float.NEGATIVE_INFINITY

            for (item in listState.layoutInfo.visibleItemsInfo) {
                val key = item.key.toString()
                val habId = when {
                    key.startsWith("jalur-") -> key.removePrefix("jalur-")
                    key.startsWith("pemisah-") -> key.removePrefix("pemisah-")
                    else -> null
                }
                if (habId != null && item.offset.toFloat() <= sampleY) {
                    if (item.offset.toFloat() > maxOffsetBelowSample) {
                        maxOffsetBelowSample = item.offset.toFloat()
                        targetHabitatId = habId
                    }
                }
            }

            val idx = habitatTerurut.indexOfFirst { it.id == targetHabitatId }
            if (idx >= 0) idx else 0
        }
    }

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            SapaanAtas(
                nama = ui.namaPemain.ifBlank { "Penjelajah" },
                totalBintang = ui.totalBintang,
                totalKoin = ui.totalKoin,
                sfxMuted = ui.sfxMuted,
                onToggleSuara = { viewModel.toggleSuara() }
            )
        },
        bottomBar = {
            SiBekaBottomBar(
                terpilih = TabUtama.PETA,
                onPeta = {},
                onKoleksi = { viewModel.klik(); onBukaKoleksi(null) },
                onToko = { viewModel.klik(); onBukaToko() }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                Modifier.fillMaxSize(),
                state = listState,
                // Beri ruang atas untuk header tunggal yang overlay
                contentPadding = PaddingValues(top = paddingAtasBanner, bottom = 24.dp)
            ) {
                if (habitatTerurut.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxHeight(0.6f).padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Memuat jalur jelajah...", fontSize = 13.sp, color = AbuTua)
                        }
                    }
                }

                habitatTerurut.forEachIndexed { index, habitat ->
                    val tema = temaHabitat(habitat.id)
                    val jumlah = ui.jumlah(habitat.id)
                    val ditemukan = ui.ditemukan(habitat.id)
                    val lengkap = ui.lengkap(habitat.id)

                    item(key = "jalur-${habitat.id}") {
                        val simpul = buildList {
                            URUTAN_MODE.forEachIndexed { urutanMode, mode ->
                                add(
                                    SimpulBelajar(
                                        id = "mode:${mode.name}",
                                        ikon = mode.info.inisial,
                                        judul = mode.info.judul,
                                        selesai = lengkap,
                                        aktif = habitat.id == habitatAktif && urutanMode == 0 && !lengkap,
                                        aset = "mode_${mode.name.lowercase()}"
                                    )
                                )
                            }
                            add(
                                SimpulBelajar(
                                    id = "koleksi",
                                    ikon = if (lengkap) "🏆" else "🎁",
                                    judul = "Koleksi habitat",
                                    selesai = lengkap,
                                    aktif = false,
                                    aset = if (lengkap) "simpul_trofi" else "simpul_hadiah"
                                )
                            )
                        }

                        // Background habitat langsung menyambung tanpa jeda strip pemisah
                        JalurBelajar(
                            simpul = simpul,
                            warna = tema.warna,
                            modifier = Modifier.fillMaxWidth(),
                            idHabitat = habitat.id,
                            namaHabitat = if (index > 0) namaTampilHabitat(habitat.id, habitat.namaHabitat) else null
                        ) { dipilih ->
                            viewModel.klik()
                            val mode = URUTAN_MODE.firstOrNull { "mode:${it.name}" == dipilih.id }
                            if (mode != null) onMulaiKuis(habitat.id, mode) else onPilihHabitat(habitat.id)
                        }
                    }
                }

                item {
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        BubbleSiBeka("Kita lanjut menjelajah, yuk! Masih banyak teman yang bisa ditemukan.")
                    }
                }
            }

            // Header tunggal overlay di atas LazyColumn — berubah dengan animasi halus saat scroll
            if (habitatTerurut.isNotEmpty()) {
                val idx = habitatTerlihatIndex.coerceIn(0, habitatTerurut.lastIndex)

                AnimatedContent(
                    targetState = idx,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { tinggiBannerPx = it.height },
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { height -> height / 2 } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height / 2 } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height / 2 } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> height / 2 } + fadeOut())
                        }
                    },
                    label = "header-anim"
                ) { targetIdx ->
                    val hab = habitatTerurut[targetIdx]
                    val tm = temaHabitat(hab.id)
                    val jml = ui.jumlah(hab.id)
                    val dtk = ui.ditemukan(hab.id)

                    BannerHabitat(
                        nomor = targetIdx + 1,
                        totalHabitat = habitatTerurut.size,
                        namaHabitat = hab.namaHabitat,
                        tema = tm,
                        ditemukan = dtk,
                        jumlahSpesies = jml,
                        habitatId = hab.id,
                                                    onBukaPanduan = {
                                viewModel.klik()
                                onBukaKoleksi(hab.id)
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun SapaanAtas(
    nama: String,
    totalBintang: Int,
    totalKoin: Int,
    sfxMuted: Boolean,
    onToggleSuara: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(LatarKrem)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 16.dp, top = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GambarAset(
            nama = "berpikir_kecil",
            cadangan = "🐒",
            modifier = Modifier.size(28.dp),
            ukuranCadangan = 22.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            nama,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = HijauTua,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        // Tombol suara tanpa kotak, hanya ikon agar header tetap ringan.
        Box(
            Modifier.size(30.dp).clip(CircleShape).clickable(onClick = onToggleSuara),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (sfxMuted) Icons.AutoMirrored.Outlined.VolumeOff
                else Icons.AutoMirrored.Outlined.VolumeUp,
                contentDescription = "Atur suara",
                tint = AbuTua,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.width(6.dp))
        ChipStatistik(bintang = totalBintang, koin = totalKoin)
    }
}
