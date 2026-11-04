package com.example.sibeka.ui.koleksi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.AssetImageLoader

@Composable
fun KoleksiScreen(
    onBukaPeta: () -> Unit,
    onBukaToko: () -> Unit
) {
    val repository = SiBekaApp.instance.container.repository
    val dataStore = repository.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val audioManager = SiBekaApp.instance.container.audioManager

    val habitats by repository.getAllHabitats().collectAsState(initial = emptyList())
    val koleksiList by repository.getAllKoleksi().collectAsState(initial = emptyList())
    val collectedIds = remember(koleksiList) { koleksiList.map { it.idObjek }.toSet() }
    val totalKoin by dataStore.totalKoin.collectAsState(initial = 100)
    val totalBintang by dataStore.totalBintang.collectAsState(initial = 0)

    var selectedHabitatId by remember { mutableStateOf("SEMUA") }
    var allObjek by remember { mutableStateOf<List<ObjekEntity>>(emptyList()) }
    var selectedObjek by remember { mutableStateOf<ObjekEntity?>(null) }

    LaunchedEffect(Unit) {
        val list = mutableListOf<ObjekEntity>()
        for (h in listOf("HH", "LT", "SG", "RW", "GN")) {
            list.addAll(repository.getSoalByHabitat(h, 15))
        }
        allObjek = list
    }

    val filteredObjek = remember(selectedHabitatId, allObjek) {
        if (selectedHabitatId == "SEMUA") allObjek else allObjek.filter { it.idHabitat == selectedHabitatId }
    }
    val namaHabitatDari = { id: String -> habitats.firstOrNull { it.id == id }?.namaHabitat ?: "" }

    BackHandler(enabled = selectedObjek != null) { selectedObjek = null }

    val detail = selectedObjek
    if (detail != null) {
        DetailKoleksi(
            objek = detail,
            namaHabitat = namaHabitatDari(detail.idHabitat),
            totalKoin = totalKoin,
            totalBintang = totalBintang,
            onKembali = { sfxPlayer.playClick(); selectedObjek = null },
            onPlayAudio = {
                if (detail.fileSuaraPath.isNotBlank()) audioManager.playSuaraFauna(detail.fileSuaraPath)
            }
        )
        return
    }

    val total = allObjek.size.coerceAtLeast(1)
    val ditemukan = allObjek.count { it.idObjek in collectedIds }
    val persen = ditemukan * 100 / total

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            HeaderJelajahAlam { ChipBintang(totalBintang); ChipKoin(totalKoin) }
        },
        bottomBar = {
            SiBekaBottomBar(
                terpilih = TabUtama.KOLEKSI,
                onPeta = { sfxPlayer.playClick(); onBukaPeta() },
                onKoleksi = {},
                onToko = { sfxPlayer.playClick(); onBukaToko() }
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
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Buku Koleksiku", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                    Text("Setiap penemuan punya cerita.", fontSize = 13.sp, color = AbuTua)
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                KartuSiBeka(Modifier.fillMaxWidth(), container = HijauBenarMuda, borderColor = HijauBenarMuda, shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$ditemukan dari $total spesies ditemukan", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                            Text("$persen%", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                        }
                        Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(PutihBersih.copy(alpha = 0.7f))) {
                            Box(Modifier.fillMaxWidth(persen / 100f).fillMaxHeight().clip(RoundedCornerShape(4.dp)).background(HijauTua))
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        ChipFilter("Semua · $ditemukan", selectedHabitatId == "SEMUA") { selectedHabitatId = "SEMUA" }
                    }
                    items(habitats) { h ->
                        val n = allObjek.count { it.idHabitat == h.id && it.idObjek in collectedIds }
                        ChipFilter("${h.namaHabitat} · $n", selectedHabitatId == h.id) { selectedHabitatId = h.id }
                    }
                }
            }
            items(filteredObjek) { item ->
                val terbuka = collectedIds.contains(item.idObjek)
                KoleksiGridItem(
                    objek = item,
                    namaHabitat = namaHabitatDari(item.idHabitat),
                    terbuka = terbuka,
                    onClick = {
                        if (terbuka) { sfxPlayer.playClick(); selectedObjek = item } else sfxPlayer.playSalah()
                    }
                )
            }
        }
    }
}

@Composable
private fun ChipFilter(label: String, terpilih: Boolean, onClick: () -> Unit) {
    val bentuk = RoundedCornerShape(50)
    Box(
        Modifier.clip(bentuk)
            .background(if (terpilih) HijauTua else PutihBersih)
            .border(1.dp, if (terpilih) HijauTua else GarisKartu, bentuk)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (terpilih) PutihBersih else HijauTua)
    }
}

@Composable
fun KoleksiGridItem(objek: ObjekEntity, namaHabitat: String, terbuka: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    KartuSiBeka(
        Modifier.fillMaxWidth(),
        container = if (terbuka) PutihBersih else AbuMuda,
        borderColor = if (terbuka) GarisKartu else GarisKartu.copy(alpha = 0.6f),
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier.fillMaxWidth().height(92.dp).clip(RoundedCornerShape(12.dp))
                    .background(if (terbuka) HijauBenarMuda else MaskotLatar),
                contentAlignment = Alignment.Center
            ) {
                if (terbuka) {
                    AsyncImage(
                        model = AssetImageLoader.buildAssetRequest(context, objek.gambarUtamaPath, 240, 240),
                        contentDescription = objek.namaObjek,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(Icons.Default.Lock, "Terkunci", tint = AbuSedang, modifier = Modifier.size(30.dp))
                }
            }
            Column(Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                Text(
                    if (terbuka) objek.namaObjek else "Belum ditemukan",
                    fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (terbuka) HijauTua else AbuTua, maxLines = 1
                )
                Text(
                    if (terbuka) namaHabitat else "Menangkan kuis untuk membuka",
                    fontSize = 10.sp, color = AbuTua, maxLines = 1
                )
            }
        }
    }
}

// ── Detail koleksi (layar penuh, menggantikan dialog) ───────────────────────
@Composable
private fun DetailKoleksi(
    objek: ObjekEntity,
    namaHabitat: String,
    totalKoin: Int,
    totalBintang: Int,
    onKembali: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onKembali).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = HijauTua, modifier = Modifier.size(22.dp))
                    Text("Buku Koleksi", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ChipBintang(totalBintang); ChipKoin(totalKoin) }
            }
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(LatarKrem).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 10.dp)) {
                TombolSekunder("Kembali ke Buku Koleksi", onClick = onKembali, icon = Icons.Outlined.MenuBook)
            }
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(24.dp)).background(HijauBenarMuda),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, objek.gambarUtamaPath, 600, 420),
                    contentDescription = objek.namaObjek,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentScale = ContentScale.Fit
                )
                if (namaHabitat.isNotBlank()) {
                    PillLabel(namaHabitat.uppercase(), Modifier.align(Alignment.TopStart).padding(12.dp), container = PutihBersih)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(objek.namaObjek, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                if (namaHabitat.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Place, null, tint = BiruLaut, modifier = Modifier.size(15.dp))
                        Text(namaHabitat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BiruLaut)
                    }
                }
            }

            if (objek.fileSuaraPath.isNotBlank()) {
                KartuSiBeka(Modifier.fillMaxWidth(), container = BiruPucat, borderColor = BiruPucat, shape = RoundedCornerShape(18.dp), onClick = onPlayAudio) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(HijauTua), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayArrow, "Putar", tint = PutihBersih, modifier = Modifier.size(26.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Dengarkan suaranya", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                            Text("Ketuk untuk memutar suara ${objek.namaObjek}", fontSize = 11.sp, color = BiruLaut, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, tint = BiruLaut, modifier = Modifier.size(22.dp))
                    }
                }
            }

            KartuSiBeka(Modifier.fillMaxWidth(), container = KuningMuda, borderColor = KuningMuda, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.Lightbulb, null, tint = CoklatTua, modifier = Modifier.size(18.dp))
                        Text("Tahukah kamu?", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                    }
                    Text(objek.faktaUnik, fontSize = 13.sp, lineHeight = 19.sp, color = HitamLembut)
                }
            }

            BubbleSiBeka("${objek.namaObjek} sudah jadi teman barumu. Yuk, kumpulkan teman lainnya!")
            Spacer(Modifier.height(4.dp))
        }
    }
}