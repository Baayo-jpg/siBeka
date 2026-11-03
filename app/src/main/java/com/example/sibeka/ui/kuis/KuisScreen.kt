package com.example.sibeka.ui.kuis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.hasil.HasilScreen
import com.example.sibeka.ui.kuis.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.AssetImageLoader

private data class TeksMode(val judul: String, val instruksi: String, val hintBubble: String, val hintTombol: String)

private fun teksMode(mode: ModeKuis) = when (mode) {
    ModeKuis.SUSUN_HURUF -> TeksMode(
        "Susun nama spesiesnya!", "Pilih huruf untuk mengisi kotak-kotak ini.",
        "Ketuk huruf satu per satu sampai namanya lengkap. Salah pilih? Hapus huruf terakhir ya!",
        "Susun huruf di atas"
    )
    ModeKuis.PASANGKAN -> TeksMode(
        "Temukan pasangannya!", "Pilih satu kartu yang paling cocok.",
        "Perhatikan gambarnya baik-baik, lalu pilih pasangan yang paling cocok.",
        "Pilih pasangan di atas"
    )
    ModeKuis.TEBAK_SILUET -> TeksMode(
        "Siapa di balik bayangan?", "Perhatikan bentuk tubuhnya.",
        "Lihat bentuk tubuhnya. Siapa ya yang punya bayangan seperti itu?",
        "Pilih jawaban di atas"
    )
    ModeKuis.POTONGAN_GAMBAR -> TeksMode(
        "Buka sedikit, tebak cepat!", "Makhluk apakah yang bersembunyi ini?",
        "Buka potongan satu per satu. Makin sedikit yang dibuka, makin banyak poinnya!",
        "Pilih jawaban di atas"
    )
    ModeKuis.TEBAK_SUARA -> TeksMode(
        "Suara siapakah itu?", "Dengarkan, lalu pilih hewan yang tepat.",
        "Pasang telingamu! Kamu boleh mendengarkan lebih dari sekali.",
        "Pilih hewan di atas"
    )
}

private fun statusOpsi(opsi: ObjekEntity, ui: KuisUiState, soal: ObjekEntity): StatusOpsi {
    if (ui.statusJawaban == StatusJawaban.BELUM) return StatusOpsi.NETRAL
    return when {
        opsi.idObjek == soal.idObjek -> StatusOpsi.BENAR
        opsi.idObjek == ui.opsiTerpilihId -> StatusOpsi.SALAH
        else -> StatusOpsi.NETRAL
    }
}

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
            totalSoal = uiState.daftarSoal.size,
            onMainLagi = { viewModel.mulaiKuis(habitatId, modeKuis) },
            onKembaliKePeta = onKeluarKuis
        )
        return
    }

    if (uiState.isLoading || uiState.daftarSoal.isEmpty()) {
        Box(Modifier.fillMaxSize().background(LatarKrem), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HijauUtama)
        }
        return
    }

    val currentSoal = viewModel.currentSoal ?: return
    val sudahJawab = uiState.statusJawaban != StatusJawaban.BELUM
    val teks = teksMode(uiState.modeKuis)
    val soalTerakhir = uiState.currentSoalIndex + 1 >= uiState.daftarSoal.size

    // Teks "Jawabanmu" / "Jawaban benar" untuk kartu umpan balik
    val (jawabanmu, jawabanBenar) = when (uiState.modeKuis) {
        ModeKuis.SUSUN_HURUF ->
            uiState.hurufTerpilih.joinToString("") to currentSoal.kataKunci.uppercase()
        ModeKuis.PASANGKAN -> null to null // opsi berupa gambar pasangan, tidak punya label
        else ->
            (uiState.opsiJawaban.find { it.idObjek == uiState.opsiTerpilihId }?.namaObjek) to currentSoal.namaObjek
    }

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            KuisTopBar(namaHabitat = uiState.namaHabitat, totalKoin = totalKoin, onCloseClick = onKeluarKuis)
        },
        bottomBar = {
            KuisAksiBawah(
                sudahMenjawab = sudahJawab,
                teksHint = teks.hintTombol,
                teksLanjut = if (soalTerakhir) "Lihat hasil" else "Lanjut ke soal berikutnya",
                onLanjutClick = { viewModel.lanjutKeSoalBerikutnya() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProgresSoalBar(
                currentStep = uiState.currentSoalIndex + 1,
                totalSteps = uiState.daftarSoal.size,
                bintangTerkumpul = uiState.totalBintang
            )

            KuisHeader(judul = teks.judul, instruksi = teks.instruksi)

            when (uiState.modeKuis) {
                ModeKuis.SUSUN_HURUF -> SusunHurufView(uiState, currentSoal, viewModel)
                ModeKuis.PASANGKAN -> PasangkanGambarView(uiState, currentSoal, viewModel)
                ModeKuis.TEBAK_SILUET -> TebakSiluetView(uiState, currentSoal, viewModel)
                ModeKuis.POTONGAN_GAMBAR -> PotonganGambarView(uiState, currentSoal, viewModel)
                ModeKuis.TEBAK_SUARA -> TebakSuaraView(uiState, currentSoal, viewModel)
            }

            if (sudahJawab) {
                KartuUmpanBalik(
                    status = uiState.statusJawaban,
                    jawabanmu = jawabanmu,
                    jawabanBenar = jawabanBenar,
                    faktaUnik = currentSoal.faktaUnik
                )
            } else {
                BubbleSiBeka(teks.hintBubble)
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

// ── 1. Susun Huruf ───────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SusunHurufView(uiState: KuisUiState, currentSoal: ObjekEntity, viewModel: KuisViewModel) {
    val context = LocalContext.current
    val target = currentSoal.kataKunci.length
    val status = uiState.statusJawaban

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AsyncImage(
            model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 320, 320),
            contentDescription = currentSoal.namaObjek,
            modifier = Modifier.fillMaxWidth().height(190.dp),
            contentScale = ContentScale.Fit
        )
        Text("$target huruf", fontSize = 11.sp, color = AbuTua, fontWeight = FontWeight.SemiBold)

        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 0 until target) {
                val char = uiState.hurufTerpilih.getOrNull(i)
                val salah = status == StatusJawaban.SALAH
                val latar = when {
                    char == null -> PutihBersih
                    salah -> MerahMuda
                    else -> HijauBenarMuda
                }
                val garis = when {
                    char == null -> GarisKartu
                    salah -> MerahSalah
                    else -> HijauBenar
                }
                Box(
                    Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(latar)
                        .border(1.5.dp, garis, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        char?.toString() ?: "·", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
                        color = if (salah) MerahSalah else if (char == null) AbuSedang else HijauTua
                    )
                }
            }
        }

        if (status == StatusJawaban.BELUM) {
            if (uiState.hurufTerpilih.isNotEmpty()) {
                Row(
                    Modifier.clip(RoundedCornerShape(8.dp)).clickable { viewModel.hapusHurufTerakhir() }.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Backspace, null, tint = HijauBenar, modifier = Modifier.size(14.dp))
                    Text("Hapus huruf terakhir", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HijauBenar)
                }
            }
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.hurufTersedia.forEachIndexed { index, c ->
                    UbinHuruf(c) { viewModel.pilihHuruf(index) }
                }
            }
        }
    }
}

@Composable
private fun UbinHuruf(huruf: Char, onClick: () -> Unit) {
    val bentuk = RoundedCornerShape(12.dp)
    Box(Modifier.size(width = 46.dp, height = 50.dp).clickable(onClick = onClick)) {
        Box(Modifier.fillMaxWidth().height(46.dp).offset(y = 4.dp).clip(bentuk).background(OranjeAksen))
        Box(Modifier.fillMaxWidth().height(46.dp).clip(bentuk).background(KuningBintang), contentAlignment = Alignment.Center) {
            Text(huruf.toString(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = CoklatTua)
        }
    }
}

// ── 2. Pasangkan Gambar ──────────────────────────────────────────────────────

@Composable
fun PasangkanGambarView(uiState: KuisUiState, currentSoal: ObjekEntity, viewModel: KuisViewModel) {
    val context = LocalContext.current

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LabelKolom("FLORA & FAUNA")
            KartuSiBeka(Modifier.fillMaxWidth().height(292.dp), shape = RoundedCornerShape(18.dp)) {
                Column(
                    Modifier.fillMaxSize().padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 300, 300),
                        contentDescription = currentSoal.namaObjek,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(currentSoal.namaObjek, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, textAlign = TextAlign.Center)
                }
            }
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabelKolom("HABITAT / MAKANAN")
            uiState.opsiJawaban.forEach { opsi ->
                KartuPilihan(
                    status = statusOpsi(opsi, uiState, currentSoal),
                    onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) },
                    modifier = Modifier.fillMaxWidth().height(92.dp)
                ) {
                    AsyncImage(
                        model = AssetImageLoader.buildAssetRequest(context, opsi.gambarPasanganPath, 200, 200),
                        contentDescription = "Pasangan",
                        modifier = Modifier.fillMaxSize().padding(6.dp).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelKolom(teks: String) {
    Text(teks, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AbuTua, letterSpacing = 0.6.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

// ── 3. Tebak Siluet ──────────────────────────────────────────────────────────

@Composable
fun TebakSiluetView(uiState: KuisUiState, currentSoal: ObjekEntity, viewModel: KuisViewModel) {
    val context = LocalContext.current
    val terungkap = uiState.statusJawaban != StatusJawaban.BELUM
    val huruf = listOf("A", "B", "C", "D")

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(24.dp)).background(HijauBenarMuda),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(170.dp).clip(CircleShape).background(MaskotLatar))
            AsyncImage(
                model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 340, 340),
                contentDescription = "Siluet",
                colorFilter = if (!terungkap) ColorFilter.tint(HitamLembut, BlendMode.SrcIn) else null,
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentScale = ContentScale.Fit
            )
            PillLabel(
                if (terungkap) currentSoal.namaObjek.uppercase() else "TEBAK SILUET",
                Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                container = PutihBersih
            )
        }
        uiState.opsiJawaban.forEachIndexed { i, opsi ->
            OpsiBerhuruf(
                huruf = huruf.getOrElse(i) { "?" },
                teks = opsi.namaObjek,
                status = statusOpsi(opsi, uiState, currentSoal),
                onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) }
            )
        }
    }
}

// ── 4. Potongan Gambar ───────────────────────────────────────────────────────

@Composable
fun PotonganGambarView(uiState: KuisUiState, currentSoal: ObjekEntity, viewModel: KuisViewModel) {
    val context = LocalContext.current
    val terungkap = uiState.statusJawaban != StatusJawaban.BELUM
    val huruf = listOf("A", "B", "C", "D")
    val jumlahTerbuka = uiState.potonganTerbuka.count { it }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            PillLabel("${uiState.poinPotonganTersisa} POIN", container = KuningBintang, contentColor = CoklatTua, icon = Icons.Outlined.Eco)
            Text("$jumlahTerbuka dari ${uiState.potonganTerbuka.size} potongan terbuka", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AbuTua)
        }

        Box(Modifier.fillMaxWidth().aspectRatio(1.5f).clip(RoundedCornerShape(20.dp)).background(PutihBersih)) {
            AsyncImage(
                model = AssetImageLoader.buildAssetRequest(context, currentSoal.gambarUtamaPath, 480, 320),
                contentDescription = "Objek",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.fillMaxSize()) {
                for (row in 0..1) {
                    Row(Modifier.weight(1f)) {
                        for (col in 0..2) {
                            val idx = row * 3 + col
                            val buka = terungkap || uiState.potonganTerbuka.getOrElse(idx) { false }
                            Box(
                                Modifier.weight(1f).fillMaxHeight()
                                    .background(if (buka) Color.Transparent else if ((row + col) % 2 == 0) HijauBenarMuda else MaskotLatar)
                                    .border(0.5.dp, if (buka) Color.Transparent else PutihBersih)
                                    .clickable(enabled = !buka) { viewModel.bukaPotonganTile(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (!buka) Icon(Icons.Outlined.Eco, null, tint = HijauMuda, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }
        }

        Text("Mulai 100 • −15 tiap potongan dibuka (ketuk daun untuk membuka)", fontSize = 11.sp, color = AbuTua)

        uiState.opsiJawaban.forEachIndexed { i, opsi ->
            OpsiBerhuruf(
                huruf = huruf.getOrElse(i) { "?" },
                teks = opsi.namaObjek,
                status = statusOpsi(opsi, uiState, currentSoal),
                onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) }
            )
        }
    }
}

// ── 5. Tebak Suara ───────────────────────────────────────────────────────────

@Composable
fun TebakSuaraView(uiState: KuisUiState, currentSoal: ObjekEntity, viewModel: KuisViewModel) {
    val context = LocalContext.current
    val audioManager = SiBekaApp.instance.container.audioManager
    val putar = {
        if (currentSoal.fileSuaraPath.isNotBlank()) audioManager.playSuaraFauna(currentSoal.fileSuaraPath)
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        KartuSiBeka(Modifier.fillMaxWidth(), container = HijauTua, borderColor = HijauTua, shape = RoundedCornerShape(24.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 22.dp).clickable { putar() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(Modifier.size(150.dp).border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape))
                    Box(Modifier.size(120.dp).border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape))
                    Box(Modifier.size(90.dp).clip(CircleShape).background(KuningPlay), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, "Putar suara", tint = HijauTua, modifier = Modifier.size(48.dp))
                    }
                }
                Text("Ketuk untuk mendengar", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = PutihBersih)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, tint = PutihBersih.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                    Text("Suara alam", fontSize = 11.sp, color = PutihBersih.copy(alpha = 0.8f))
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            uiState.opsiJawaban.forEach { opsi ->
                val status = statusOpsi(opsi, uiState, currentSoal)
                KartuPilihan(
                    status = status,
                    onClick = { viewModel.pilihOpsiJawaban(opsi.idObjek) },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AsyncImage(
                            model = AssetImageLoader.buildAssetRequest(context, opsi.gambarUtamaPath, 240, 240),
                            contentDescription = opsi.namaObjek,
                            modifier = Modifier.fillMaxWidth().height(96.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            opsi.namaObjek, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut,
                            textAlign = TextAlign.Center, maxLines = 2, minLines = 2
                        )
                        when (status) {
                            StatusOpsi.BENAR -> Icon(Icons.Outlined.CheckCircle, null, tint = HijauBenar, modifier = Modifier.size(18.dp))
                            StatusOpsi.SALAH -> Icon(Icons.Outlined.Cancel, null, tint = MerahSalah, modifier = Modifier.size(18.dp))
                            else -> Box(Modifier.size(16.dp).border(1.5.dp, GarisKartu, CircleShape))
                        }
                    }
                }
            }
        }
    }
}