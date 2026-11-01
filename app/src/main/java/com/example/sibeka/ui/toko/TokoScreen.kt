package com.example.sibeka.ui.toko

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.theme.*
import kotlinx.coroutines.launch

data class AksesoriItem(
    val id: String,
    val nama: String,
    val harga: Int,
    val emoji: String,
    val deskripsi: String
)

@Composable
fun TokoScreen(
    onKembali: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val repository = SiBekaApp.instance.container.repository
    val dataStore = repository.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    val totalKoin by dataStore.totalKoin.collectAsState(initial = 100)
    val aksesoriAktif by dataStore.aksesoriAktif.collectAsState(initial = "default")
    val aksesoriTerbeli by dataStore.aksesoriTerbeli.collectAsState(initial = setOf("default"))

    var snackbarMessage by remember { mutableStateOf("") }

    val daftarAksesori = listOf(
        AksesoriItem("default", "Bulu Alami", 0, "🦜", "Tampilan asli Si Beka yang ramah"),
        AksesoriItem("topi_penjelajah", "Topi Rimba", 100, "🤠", "Siap menjelajah hutan lebat"),
        AksesoriItem("kacamata_keren", "Kacamata Hitam", 150, "😎", "Tampil keren di bawah terik laut"),
        AksesoriItem("mahkota_bunga", "Mahkota Bunga", 200, "🌸", "Harum semerbak dari flora rawa"),
        AksesoriItem("helm_gunung", "Helm Gunung", 250, "🧗", "Pelindung aman di tebing tinggi")
    )

    // Animasi melompat Si Beka
    val infiniteTransition = rememberInfiniteTransition(label = "avatarBounce")
    val bekaY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bekaY"
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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

                    Text(
                        text = "Toko Maskot Si Beka",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoklatTua
                    )

                    // Koin badge
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
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Preview Card Maskot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PutihBersih)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val itemAktif = daftarAksesori.find { it.id == aksesoriAktif } ?: daftarAksesori.first()

                    Box(
                        modifier = Modifier
                            .offset(y = bekaY.dp)
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(KuningMuda),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = itemAktif.emoji, fontSize = 52.sp)
                    }

                    Text(
                        text = "Aksesori Terpasang: ${itemAktif.nama}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HijauTua
                    )
                    Text(
                        text = itemAktif.deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = AbuTua,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Pesan Notifikasi Ringan
            if (snackbarMessage.isNotBlank()) {
                Surface(
                    color = KuningMuda,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = snackbarMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = CoklatTua,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Grid Aksesori
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(daftarAksesori) { item ->
                    val isOwned = aksesoriTerbeli.contains(item.id)
                    val isEquipped = (aksesoriAktif == item.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) HijauBenarMuda else PutihBersih
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = item.emoji, fontSize = 40.sp)
                            Text(
                                text = item.nama,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = HitamLembut,
                                textAlign = TextAlign.Center
                            )

                            if (isEquipped) {
                                Surface(
                                    color = HijauUtama,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Terpasang",
                                        color = PutihBersih,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            } else if (isOwned) {
                                Button(
                                    onClick = {
                                        sfxPlayer.playClick()
                                        coroutineScope.launch {
                                            dataStore.pasangAksesori(item.id)
                                            snackbarMessage = "${item.nama} berhasil dipakai!"
                                        }
                                    },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BiruLaut)
                                ) {
                                    Text("Pakai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val berhasil = dataStore.beliAksesori(item.id, item.harga)
                                            if (berhasil) {
                                                sfxPlayer.playKoin()
                                                snackbarMessage = "Berhasil membeli ${item.nama}! 🎉"
                                            } else {
                                                sfxPlayer.playSalah()
                                                snackbarMessage = "Koinmu belum cukup! Kumpulkan dari kuis ya! 😊"
                                            }
                                        }
                                    },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OranjeAksen)
                                ) {
                                    Text("¢ ${item.harga}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
