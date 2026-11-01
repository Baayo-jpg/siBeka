package com.example.sibeka.ui.koleksi

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.AssetImageLoader

@Composable
fun KoleksiScreen(
    onKembali: () -> Unit
) {
    val repository = SiBekaApp.instance.container.repository
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val audioManager = SiBekaApp.instance.container.audioManager

    val habitats by repository.getAllHabitats().collectAsState(initial = emptyList())
    val koleksiList by repository.getAllKoleksi().collectAsState(initial = emptyList())
    val collectedIds = remember(koleksiList) { koleksiList.map { it.idObjek }.toSet() }

    var selectedHabitatId by remember { mutableStateOf("SEMUA") }
    var allObjek by remember { mutableStateOf<List<ObjekEntity>>(emptyList()) }
    var selectedObjekForDetail by remember { mutableStateOf<ObjekEntity?>(null) }

    LaunchedEffect(Unit) {
        val list = mutableListOf<ObjekEntity>()
        val habs = listOf("HH", "LT", "SG", "RW", "GN")
        for (h in habs) {
            list.addAll(repository.getSoalByHabitat(h, 15))
        }
        allObjek = list
    }

    val filteredObjek = remember(selectedHabitatId, allObjek) {
        if (selectedHabitatId == "SEMUA") allObjek
        else allObjek.filter { it.idHabitat == selectedHabitatId }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PutihBersih,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = {
                                sfxPlayer.playClick()
                                onKembali()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AbuMuda)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = HitamLembut
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Buku Koleksi Flora & Fauna",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = HijauTua
                            )
                            Text(
                                text = "Terkumpul: ${collectedIds.size} dari 75 Spesies",
                                style = MaterialTheme.typography.bodySmall,
                                color = AbuTua
                            )
                        }
                    }

                    // Filter Tab Habitat
                    ScrollableTabRow(
                        selectedTabIndex = if (selectedHabitatId == "SEMUA") 0 else habitats.indexOfFirst { it.id == selectedHabitatId } + 1,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedHabitatId == "SEMUA",
                            onClick = { selectedHabitatId = "SEMUA" },
                            text = { Text("Semua") }
                        )
                        habitats.forEach { h ->
                            Tab(
                                selected = selectedHabitatId == h.id,
                                onClick = { selectedHabitatId = h.id },
                                text = { Text(h.namaHabitat) }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredObjek) { item ->
                val isUnlocked = collectedIds.contains(item.idObjek)
                KoleksiGridItem(
                    objek = item,
                    isUnlocked = isUnlocked,
                    onClick = {
                        if (isUnlocked) {
                            sfxPlayer.playClick()
                            selectedObjekForDetail = item
                        } else {
                            sfxPlayer.playSalah()
                        }
                    }
                )
            }
        }
    }

    // Modal Detail Kartu Koleksi
    selectedObjekForDetail?.let { objek ->
        DetailKoleksiDialog(
            objek = objek,
            onDismiss = { selectedObjekForDetail = null },
            onPlayAudio = {
                if (objek.fileSuaraPath.isNotBlank()) {
                    audioManager.playSuaraFauna(objek.fileSuaraPath)
                }
            }
        )
    }
}

@Composable
fun KoleksiGridItem(
    objek: ObjekEntity,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick)
            .shadow(if (isUnlocked) 3.dp else 1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) PutihBersih else Color(0xFFE0E0E0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isUnlocked) {
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, objek.gambarUtamaPath, 160, 160),
                    contentDescription = objek.namaObjek,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = objek.namaObjek,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = HitamLembut,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Terkunci",
                    tint = AbuSedang,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "???",
                    style = MaterialTheme.typography.bodySmall,
                    color = AbuSedang,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DetailKoleksiDialog(
    objek: ObjekEntity,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PutihBersih)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kartu Spesies Alam",
                        style = MaterialTheme.typography.labelLarge,
                        color = HijauUtama
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                // Gambar Besar
                AsyncImage(
                    model = AssetImageLoader.buildAssetRequest(context, objek.gambarUtamaPath, 300, 300),
                    contentDescription = objek.namaObjek,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = objek.namaObjek,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = HijauTua,
                    textAlign = TextAlign.Center
                )

                // Fakta Unik Card
                Surface(
                    color = HijauPucat,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "💡 Tahukah Kamu?",
                            style = MaterialTheme.typography.labelLarge,
                            color = CoklatTua,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = objek.faktaUnik,
                            style = MaterialTheme.typography.bodySmall,
                            color = HitamLembut,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
