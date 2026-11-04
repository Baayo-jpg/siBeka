package com.example.sibeka.ui.peta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.sibeka.ui.components.*
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

    LaunchedEffect(bgmMuted) {
        audioManager.setBgmMuted(bgmMuted)
    }

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            HeaderJelajahAlam {
                TombolIkonBulat(
                    icon = if (bgmMuted) Icons.AutoMirrored.Outlined.VolumeOff else Icons.AutoMirrored.Outlined.VolumeUp,
                    deskripsi = "Atur suara",
                    ukuran = 34.dp,
                    onClick = {
                        coroutineScope.launch {
                            dataStore.toggleBgm(!bgmMuted)
                            sfxPlayer.setMuted(!bgmMuted)
                        }
                    }
                )
                ChipBintang(totalBintang)
                ChipKoin(totalKoin)
            }
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
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Mau ke mana hari ini?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                    Text("Halo, $namaPemain! Lima habitat, banyak teman baru.", fontSize = 13.sp, color = AbuTua)
                }
            }
            item { HabitatMapAnimation() }
            items(habitats) { habitat ->
                HabitatCard(habitat = habitat, onClick = { sfxPlayer.playClick(); onPilihHabitat(habitat.id) })
            }
            item { BubbleSiBeka("Kita lanjut menjelajah, yuk! Masih banyak teman yang bisa ditemukan.") }
        }
    }
}

@Composable
private fun HabitatMapAnimation() {
    val context = LocalContext.current
    Box(
        Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(24.dp))
            .background(MaskotLatar).border(1.dp, GarisKartu, RoundedCornerShape(24.dp))
    ) {
        AndroidView(
            factory = { RiveAnimationView(context).apply { setRiveResource(R.raw.habitat_map) } },
            modifier = Modifier.fillMaxSize().semantics { contentDescription = "Animasi peta habitat Indonesia" }
        )
    }
}

@Composable
fun HabitatCard(habitat: HabitatEntity, onClick: () -> Unit) {
    val (warnaTema, emojiHabitat, deskripsi) = when (habitat.id) {
        "HH" -> Triple(WarnaHutan, "🌳", "Hutan rimbun kaya flora fauna endemik")
        "LT" -> Triple(WarnaLaut, "🌊", "Kedalaman samudra penuh biota menakjubkan")
        "SG" -> Triple(WarnaSungai, "🛶", "Arus air tawar tempat pesut & ikan endemik")
        "RW" -> Triple(WarnaRawa, "🌿", "Lahan basah gambut rumah bagi bekantan")
        "GN" -> Triple(WarnaGunung, "⛰️", "Puncak berkabut dengan satwa unik dataran tinggi")
        else -> Triple(HijauUtama, "🌍", "Habitat alam lestari")
    }

    KartuSiBeka(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.size(60.dp).clip(RoundedCornerShape(18.dp)).background(warnaTema.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) { Text(emojiHabitat, fontSize = 30.sp) }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(habitat.namaHabitat, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                Text(deskripsi, fontSize = 12.sp, lineHeight = 16.sp, color = AbuTua)
                Text("15 spesies", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = warnaTema)
            }

            Box(Modifier.size(34.dp).clip(CircleShape).background(HijauTua), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Buka", tint = PutihBersih, modifier = Modifier.size(22.dp))
            }
        }
    }
}