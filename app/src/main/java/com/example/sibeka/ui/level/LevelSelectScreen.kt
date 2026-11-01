package com.example.sibeka.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.model.ModeKuis
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
    var namaHabitat by remember { mutableStateOf("Habitat") }

    LaunchedEffect(habitatId) {
        val h = repository.getHabitatById(habitatId)
        namaHabitat = h?.namaHabitat ?: "Habitat"
    }

    val daftarMode = listOf(
        MenuKuisItem(
            mode = ModeKuis.SUSUN_HURUF,
            judul = "Susun Huruf",
            emoji = "🔤",
            deskripsi = "Susun huruf teracak menjadi nama flora atau fauna yang tepat",
            warna = HijauUtama
        ),
        MenuKuisItem(
            mode = ModeKuis.PASANGKAN,
            judul = "Pasangkan Gambar",
            emoji = "🔗",
            deskripsi = "Cocokkan hewan/tumbuhan dengan pasangan makanannya atau karakternya",
            warna = BiruLaut
        ),
        MenuKuisItem(
            mode = ModeKuis.TEBAK_SILUET,
            judul = "Tebak Siluet",
            emoji = "🕵️",
            deskripsi = "Kenali bayangan hitam misterius fauna & flora endemik",
            warna = CoklatKayu
        ),
        MenuKuisItem(
            mode = ModeKuis.POTONGAN_GAMBAR,
            judul = "Potongan Gambar",
            emoji = "🍃",
            deskripsi = "Buka helai daun penutup seminimal mungkin untuk menebak gambarnya",
            warna = OranjeAksen
        ),
        MenuKuisItem(
            mode = ModeKuis.TEBAK_SUARA,
            judul = "Tebak Suara",
            emoji = "🔊",
            deskripsi = "Dengarkan suara asli hewan dan tebak nama faunanya",
            warna = Color(0xFF6A1B9A)
        )
    )

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

                    Column {
                        Text(
                            text = "Jalur Level: $namaHabitat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = HijauTua
                        )
                        Text(
                            text = "Pilih Tantangan Kuis",
                            style = MaterialTheme.typography.bodySmall,
                            color = AbuTua
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(daftarMode.size) { index ->
                val item = daftarMode[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sfxPlayer.playClick()
                            onMulaiModeKuis(habitatId, item.mode)
                        }
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PutihBersih)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(item.warna.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.emoji, fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.judul,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = item.warna
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.deskripsi,
                                style = MaterialTheme.typography.bodySmall,
                                color = AbuTua,
                                lineHeight = 16.sp
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = item.warna,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "▶", color = PutihBersih, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
