package com.example.sibeka.ui.peta

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.sibeka.R
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.db.HabitatEntity
import com.example.sibeka.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PetaScreen(
    onPilihHabitat: (habitatId: String) -> Unit,
    onBukaKoleksi: () -> Unit,
    onBukaToko: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val repository = SiBekaApp.instance.container.repository
    val dataStore = repository.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val audioManager = SiBekaApp.instance.container.audioManager

    val habitats by repository.getAllHabitats().collectAsState(initial = emptyList())
    val namaPemain by dataStore.namaPemain.collectAsState(initial = "Penjelajah")
    val totalKoin by dataStore.totalKoin.collectAsState(initial = 100)
    val totalBintang by dataStore.totalBintang.collectAsState(initial = 0)
    val bgmMuted by dataStore.bgmMuted.collectAsState(initial = false)

    // Mulai background music jika belum mute
    LaunchedEffect(bgmMuted) {
        audioManager.setBgmMuted(bgmMuted)
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PutihBersih,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "♧  JELAJAH ALAM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = HijauTua
                        )
                        Text(
                            text = "Halo, $namaPemain!",
                            style = MaterialTheme.typography.bodySmall,
                            color = AbuTua
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Toggle Mute Audio
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    dataStore.toggleBgm(!bgmMuted)
                                    sfxPlayer.setMuted(!bgmMuted)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AbuMuda)
                        ) {
                            Text(text = if (bgmMuted) "🔇" else "🔊", fontSize = 16.sp)
                        }

                        // Koin Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = KuningMuda,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KuningBintang)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "¢", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoklatTua)
                                Text(text = "$totalKoin", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = CoklatTua)
                            }
                        }

                        // Bintang Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = KuningMuda,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KuningBintang)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = KuningBintang, modifier = Modifier.size(14.dp))
                                Text(text = "$totalBintang", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = CoklatTua)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LatarKrem,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavigationItem("▤", "Peta", selected = true, onClick = {})
                    NavigationItem(
                        icon = "▣",
                        label = "Buku Koleksi",
                        selected = false,
                        onClick = {
                            sfxPlayer.playClick()
                            onBukaKoleksi()
                        }
                    )
                    NavigationItem(
                        icon = "▢",
                        label = "Toko",
                        selected = false,
                        onClick = {
                            sfxPlayer.playClick()
                            onBukaToko()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LatarKrem)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Mau ke mana hari ini?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HijauTua,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Lima habitat, banyak teman baru!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AbuTua
                    )
                }
            }
            item {
                HabitatMapAnimation()
            }
            items(habitats) { habitat ->
                HabitatCard(
                    habitat = habitat,
                    onClick = {
                        sfxPlayer.playClick()
                        onPilihHabitat(habitat.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun HabitatMapAnimation() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaskotLatar
    ) {
        AndroidView(
            factory = {
                RiveAnimationView(context).apply {
                    setRiveResource(R.raw.habitat_map)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "Animasi peta habitat Indonesia"
                }
        )
    }
}

@Composable
private fun RowScope.NavigationItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f).heightIn(min = 56.dp).clickable(onClick = onClick),
        color = if (selected) HijauPucat else Color.Transparent,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(icon, color = HijauTua, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = HijauTua, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun HabitatCard(
    habitat: HabitatEntity,
    onClick: () -> Unit
) {
    val (warnaTema, emojiHabitat, deskripsi) = when (habitat.id) {
        "HH" -> Triple(WarnaHutan, "🌳", "Hutan rimbun kaya flora fauna endemik")
        "LT" -> Triple(WarnaLaut, "🌊", "Kedalaman samudra penuh biota menakjubkan")
        "SG" -> Triple(WarnaSungai, "🛶", "Arus air tawar tempat pesut & ikan endemik")
        "RW" -> Triple(WarnaRawa, "🌿", "Lahan basah gambut rumah bagi bekantan")
        "GN" -> Triple(WarnaGunung, "⛰️", "Puncak berkabut dengan satwa unik dataran tinggi")
        else -> Triple(HijauUtama, "🌍", "Habitat alam lestari")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(0.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PutihBersih),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarisKartu)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ikon Avatar Habitat
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(warnaTema.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emojiHabitat, fontSize = 34.sp)
            }

            // Info Habitat
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitat.namaHabitat,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = warnaTema
                )
                Text(
                    text = deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = AbuTua,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "15 Spesies",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = warnaTema
                    )
                }
            }

            // Tombol Mainkan
            Surface(
                shape = CircleShape,
                color = warnaTema,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "▶", color = PutihBersih, fontSize = 16.sp)
                }
            }
        }
    }
}
