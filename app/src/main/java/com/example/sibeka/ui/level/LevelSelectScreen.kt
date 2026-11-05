package com.example.sibeka.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*

data class MenuKuisItem(
    val mode: ModeKuis,
    val judul: String,
    val emoji: String,
    val deskripsi: String,
    val warna: Color
)

@Composable
fun LevelSelectScreen(
    habitatId: String,
    onKembali: () -> Unit,
    onMulaiModeKuis: (habitatId: String, mode: ModeKuis) -> Unit
) {
    val repository = SiBekaApp.instance.container.repository
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val totalKoin by repository.dataStore.totalKoin.collectAsState(initial = 100)
    val totalBintang by repository.dataStore.totalBintang.collectAsState(initial = 0)
    var namaHabitat by remember { mutableStateOf("Habitat") }

    LaunchedEffect(habitatId) {
        namaHabitat = repository.getHabitatById(habitatId)?.namaHabitat ?: "Habitat"
    }

    val daftarMode = listOf(
        MenuKuisItem(ModeKuis.SUSUN_HURUF, "Susun Huruf", "🔤", "Susun huruf teracak menjadi nama flora atau fauna yang tepat", HijauUtama),
        MenuKuisItem(ModeKuis.PASANGKAN, "Pasangkan Gambar", "🔗", "Cocokkan hewan/tumbuhan dengan pasangan makanannya atau karakternya", BiruLaut),
        MenuKuisItem(ModeKuis.POTONGAN_GAMBAR, "Potongan Gambar", "🍃", "Buka sedikit potongan penutup untuk menebak gambarnya", OranjeAksen),
    )

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TombolIkonBulat(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", { sfxPlayer.playClick(); onKembali() })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipBintang(totalBintang)
                    ChipKoin(totalKoin)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(namaHabitat, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                    Text("Dengar, lihat, dan temukan keajaibannya.", fontSize = 13.sp, color = AbuTua)
                }
            }
            item {
                Text(
                    "PILIH TANTANGAN KUIS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color = AbuTua, letterSpacing = 0.6.sp, modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(daftarMode.size) { index ->
                val item = daftarMode[index]
                KartuSiBeka(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { sfxPlayer.playClick(); onMulaiModeKuis(habitatId, item.mode) }
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(item.warna.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) { Text(item.emoji, fontSize = 26.sp) }

                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(item.judul, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                            Text(item.deskripsi, fontSize = 12.sp, lineHeight = 16.sp, color = AbuTua)
                        }

                        Box(Modifier.size(34.dp).clip(CircleShape).background(HijauTua), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayArrow, "Mulai", tint = PutihBersih, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}