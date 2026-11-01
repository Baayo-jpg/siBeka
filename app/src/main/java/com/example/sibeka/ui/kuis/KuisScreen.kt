package com.example.sibeka.ui.kuis

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.data.model.StatusJawaban
import com.example.sibeka.ui.hasil.HasilScreen
import com.example.sibeka.ui.kuis.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.AssetImageLoader

@Composable
fun KuisScreen(
    habitatId: String,
    modeKuis: ModeKuis,
    onKeluarKuis: () -> Unit,
    viewModel: KuisViewModel = viewModel(factory = KuisViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val totalKoin by viewModel.totalKoin.collectAsState()

    LaunchedEffect(habitatId, modeKuis) {
        viewModel.mulaiKuis(habitatId, modeKuis)
    }

    if (uiState.isKuisSelesai) {
        HasilScreen(
            bintangDidapat = uiState.totalBintang,
            skorTotal = uiState.skorSesi,
            onMainLagi = { viewModel.mulaiKuis(habitatId, modeKuis) },
            onKembaliKePeta = onKeluarKuis
        )
        return
    }

    if (uiState.isLoading || uiState.daftarSoal.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HijauUtama)
        }
        return
    }

    val currentSoal = viewModel.currentSoal ?: return

    val (jenisKuis, instruksi) = when (uiState.modeKuis) {
        ModeKuis.SUSUN_HURUF -> Pair("Susun Huruf", "Susun huruf teracak menjadi nama spesies ini!")
        ModeKuis.PASANGKAN -> Pair("Pasangkan Gambar", "Pilih gambar pasangan yang cocok!")
        ModeKuis.TEBAK_SILUET -> Pair("Tebak Siluet", "Siapakah pemilik bayangan misterius ini?")
        ModeKuis.POTONGAN_GAMBAR -> Pair("Potongan Gambar", "Buka daun penutup & tebak objek tersembunyi!")
        ModeKuis.TEBAK_SUARA -> Pair("Tebak Suara", "Dengarkan audionya & tebak hewannya!")
    }

    Scaffold(
        topBar = {
            KuisTopBar(
                namaHabitat = uiState.namaHabitat,
                totalKoin = totalKoin,
                onCloseClick = onKeluarKuis
            )
        },
        bottomBar = {
            if (uiState.statusJawaban != StatusJawaban.BELUM) {
                UmpanBalikPanel(
                    status = uiState.statusJawaban,
                    faktaUnik = currentSoal.faktaUnik,
                    onLanjutClick = { viewModel.lanjutKeSoalBerikutnya() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LatarKrem)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProgresSoalBar(
                currentStep = uiState.currentSoalIndex + 1,
                totalSteps = uiState.daftarSoal.size,
                bintangTerkumpul = uiState.totalBintang
            )

            KuisHeader(jenisKuis = jenisKuis, instruksi = instruksi)

            when (uiState.modeKuis) {
                ModeKuis.SUSUN_HURUF -> SusunHurufView(uiState = uiState, currentSoal = currentSoal, viewModel = viewModel)
                ModeKuis.PASANGKAN -> PasangkanGambarView(uiState = uiState, currentSoal = currentSoal, viewModel = viewModel)
                ModeKuis.TEBAK_SILUET -> TebakSiluetView(uiState = uiState, currentSoal = currentSoal, viewModel = viewModel)
                ModeKuis.POTONGAN_GAMBAR -> PotonganGambarView(uiState = uiState, currentSoal = currentSoal, viewModel = viewModel)
                ModeKuis.TEBAK_SUARA -> TebakSuaraView(uiState = uiState, currentSoal = currentSoal, viewModel = viewModel)
            }
        }
    }
}

// ── 1. Susun Huruf View ──────────────────────────────────────────────────────

@Composable
fun SusunHurufView(
    uiState: KuisUiState,
    currentSoal: ObjekEntity,
    viewModel: KuisViewModel
) {
    val context = LocalContext.current
    val targetLength = currentSoal.kataKunci.length

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gambar Utama
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PutihBersih),
            modifier = Modifier.fillMaxWidth().height(190.dp).shadow(0.dp, RoundedCornerShape(20.dp))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(10.dp)) {
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 320, 320),
                    contentDescription = currentSoal.namaObjek,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Slot Jawaban
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until targetLength) {
                val char = uiState.hurufTerpilih.getOrNull(i)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (char != null) KuningMuda else PutihBersih,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (char != null) HijauBenar else GarisKartu
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = char?.toString() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoklatTua
                        )
                    }
                }
            }
        }

        // Tombol Huruf Teracak
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiState.hurufTersedia.forEachIndexed { index, c ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HijauTua,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { viewModel.pilihHuruf(index) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = c.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PutihBersih
                        )
                    }
                }
            }

            // Tombol Backspace / Hapus
            if (uiState.hurufTerpilih.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MerahSalah,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { viewModel.hapusHurufTerakhir() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "⌫", color = PutihBersih, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── 2. Pasangkan Gambar View ─────────────────────────────────────────────────

@Composable
fun PasangkanGambarView(
    uiState: KuisUiState,
    currentSoal: ObjekEntity,
    viewModel: KuisViewModel
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gambar Utama (Kiri)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PutihBersih),
            modifier = Modifier.weight(1f).height(210.dp).shadow(4.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 300, 300),
                    contentDescription = currentSoal.namaObjek,
                    modifier = Modifier.size(130.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentSoal.namaObjek,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = HijauTua,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Pilihan Gambar Pasangan (Kanan)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.opsiJawaban.forEach { opsi ->
                val isSelected = uiState.opsiTerpilihId == opsi.idObjek
                val borderColor = when {
                    isSelected && uiState.statusJawaban == StatusJawaban.BENAR -> HijauBenar
                    isSelected && uiState.statusJawaban == StatusJawaban.SALAH -> MerahSalah
                    isSelected -> OranjeAksen
                    else -> Color.Transparent
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PutihBersih),
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clickable { viewModel.pilihOpsiJawaban(opsi.idObjek) }
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        AsyncImage(
                            model = AssetImageLoader.buildAssetRequest(context, opsi.gambarPasanganPath, 160, 160),
                            contentDescription = "Pasangan",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

// ── 3. Tebak Siluet View ─────────────────────────────────────────────────────

@Composable
fun TebakSiluetView(
    uiState: KuisUiState,
    currentSoal: ObjekEntity,
    viewModel: KuisViewModel
) {
    val context = LocalContext.current
    val isRevealed = uiState.statusJawaban != StatusJawaban.BELUM

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PutihBersih),
            modifier = Modifier.size(170.dp).shadow(4.dp, RoundedCornerShape(20.dp))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 340, 340),
                    contentDescription = "Siluet",
                    colorFilter = if (!isRevealed) ColorFilter.tint(HijauTua, BlendMode.SrcIn) else null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // 3 Pilihan Jawaban Teks
        uiState.opsiJawaban.forEach { opsi ->
            val isSelected = uiState.opsiTerpilihId == opsi.idObjek
            Button(
                onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) OranjeAksen else PutihBersih,
                    contentColor = if (isSelected) PutihBersih else HitamLembut
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) OranjeAksen else AbuSedang),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(text = opsi.namaObjek, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── 4. Potongan Gambar View ──────────────────────────────────────────────────

@Composable
fun PotonganGambarView(
    uiState: KuisUiState,
    currentSoal: ObjekEntity,
    viewModel: KuisViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Gambar dengan 6 Tile Penutup Daun
        Box(
            modifier = Modifier
                .size(width = 240.dp, height = 160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PutihBersih)
                .shadow(4.dp, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 480, 320),
                contentDescription = "Objek",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Grid 3x2 Daun Penutup
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0..1) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (col in 0..2) {
                            val tileIndex = row * 3 + col
                            val isOpen = uiState.potonganTerbuka.getOrElse(tileIndex) { false }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(0.5.dp, Color.White.copy(alpha = 0.5f))
                                    .background(if (isOpen) Color.Transparent else HijauUtama)
                                    .clickable { viewModel.bukaPotonganTile(tileIndex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isOpen) {
                                    Text(text = "🍃", fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pilihan Jawaban
        uiState.opsiJawaban.forEach { opsi ->
            val isSelected = uiState.opsiTerpilihId == opsi.idObjek
            Button(
                onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) OranjeAksen else PutihBersih,
                    contentColor = if (isSelected) PutihBersih else HitamLembut
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) OranjeAksen else AbuSedang),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text(text = opsi.namaObjek, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── 5. Tebak Suara View ──────────────────────────────────────────────────────

@Composable
fun TebakSuaraView(
    uiState: KuisUiState,
    currentSoal: ObjekEntity,
    viewModel: KuisViewModel
) {
    val audioManager = SiBekaApp.instance.container.audioManager

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tombol Putar Suara Fauna
        Surface(
            shape = CircleShape,
            color = Color(0xFF6A1B9A),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(110.dp)
                .clickable {
                    if (currentSoal.fileSuaraPath.isNotBlank()) {
                        audioManager.playSuaraFauna(currentSoal.fileSuaraPath)
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔊", fontSize = 42.sp)
                    Text(text = "Putar Suara", color = PutihBersih, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "Dengarkan suara di atas, lalu tebak siapa pemiliknya!",
            style = MaterialTheme.typography.bodySmall,
            color = AbuTua
        )

        // Pilihan Jawaban
        uiState.opsiJawaban.forEach { opsi ->
            val isSelected = uiState.opsiTerpilihId == opsi.idObjek
            Button(
                onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF6A1B9A) else PutihBersih,
                    contentColor = if (isSelected) PutihBersih else HitamLembut
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) Color(0xFF6A1B9A) else AbuSedang),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(text = opsi.namaObjek, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}
