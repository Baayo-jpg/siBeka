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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset
import kotlinx.coroutines.launch

/** Urutan tampil habitat pada jalur belajar. */
val URUTAN_HABITAT = listOf("GN", "HH", "RW", "SG", "LT")

/** Simpul mode kuis (id mode dipakai untuk navigasi). */
private val SIMPUL_MODE = listOf(
    ModeKuis.SUSUN_HURUF to "🔤",
    ModeKuis.PASANGKAN to "🔗",
    ModeKuis.POTONGAN_GAMBAR to "🍃"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetaScreen(
    onPilihHabitat: (habitatId: String) -> Unit,
    onMulaiKuis: (habitatId: String, mode: ModeKuis) -> Unit,
    onBukaKoleksi: () -> Unit,
    onBukaToko: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val repository = SiBekaApp.instance.container.repository
    val dataStore = repository.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    val habitats by repository.getAllHabitats().collectAsState(initial = emptyList())
    val koleksi by repository.getAllKoleksi().collectAsState(initial = emptyList())
    val namaPemain by dataStore.namaPemain.collectAsState(initial = "")
    val totalKoin by dataStore.totalKoin.collectAsState(initial = 100)
    val totalBintang by dataStore.totalBintang.collectAsState(initial = 0)
    val sfxMutedPreference by dataStore.sfxMuted.collectAsState(initial = null)
    val sfxMuted = sfxMutedPreference ?: false

    var semuaObjek by remember { mutableStateOf<List<ObjekEntity>>(emptyList()) }
    LaunchedEffect(Unit) { semuaObjek = repository.getAllObjek() }

    LaunchedEffect(sfxMutedPreference) {
        sfxMutedPreference?.let(sfxPlayer::setMuted)
    }

    val habitatTerurut = remember(habitats) {
        habitats.sortedBy { habitat ->
            URUTAN_HABITAT.indexOf(habitat.id).let { if (it < 0) URUTAN_HABITAT.size else it }
        }
    }
    val habitatById = remember(semuaObjek) { semuaObjek.associate { it.idObjek to it.idHabitat } }
    val jumlahByHabitat = remember(semuaObjek) { semuaObjek.groupingBy { it.idHabitat }.eachCount() }
    val ditemukanByHabitat = remember(koleksi, habitatById) {
        koleksi.mapNotNull { habitatById[it.idObjek] }.groupingBy { it }.eachCount()
    }

    // Habitat yang masih punya spesies belum ditemukan menjadi habitat aktif.
    val habitatAktif = remember(habitatTerurut, jumlahByHabitat, ditemukanByHabitat) {
        habitatTerurut.firstOrNull { habitat ->
            val total = jumlahByHabitat[habitat.id] ?: 0
            (ditemukanByHabitat[habitat.id] ?: 0) < total
        }?.id ?: habitatTerurut.firstOrNull()?.id
    }

    val listState = rememberLazyListState()

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
                nama = namaPemain.ifBlank { "Penjelajah" },
                totalBintang = totalBintang,
                totalKoin = totalKoin,
                sfxMuted = sfxMuted,
                onToggleSuara = {
                    coroutineScope.launch {
                        dataStore.toggleSfx(!sfxMuted)
                        sfxPlayer.setMuted(!sfxMuted)
                    }
                }
            )
        },
        bottomBar = {
            SiBekaBottomBar(
                terpilih = TabUtama.PETA,
                onPeta = {},
                onKoleksi = { sfxPlayer.playClick(); onBukaKoleksi() },
                onToko = { sfxPlayer.playClick(); onBukaToko() }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                Modifier.fillMaxSize(),
                state = listState,
                // Beri ruang atas untuk header tunggal yang overlay
                contentPadding = PaddingValues(top = 98.dp, bottom = 24.dp)
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
                    val jumlah = jumlahByHabitat[habitat.id] ?: 0
                    val ditemukan = ditemukanByHabitat[habitat.id] ?: 0
                    val lengkap = jumlah > 0 && ditemukan >= jumlah

                    item(key = "jalur-${habitat.id}") {
                        val simpul = buildList {
                            SIMPUL_MODE.forEachIndexed { urutanMode, (mode, ikon) ->
                                add(
                                    SimpulBelajar(
                                        id = "mode:${mode.name}",
                                        ikon = ikon,
                                        judul = mode.name,
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
                            sfxPlayer.playClick()
                            val mode = SIMPUL_MODE.firstOrNull { "mode:${it.first.name}" == dipilih.id }?.first
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
                    val jml = jumlahByHabitat[hab.id] ?: 0
                    val dtk = ditemukanByHabitat[hab.id] ?: 0

                    BannerHabitat(
                        nomor = targetIdx + 1,
                        totalHabitat = habitatTerurut.size,
                        namaHabitat = hab.namaHabitat,
                        tema = tm,
                        ditemukan = dtk,
                        jumlahSpesies = jml,
                        habitatId = hab.id,
                        onBukaPanduan = {
                            sfxPlayer.playClick()
                            onBukaKoleksi()
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
