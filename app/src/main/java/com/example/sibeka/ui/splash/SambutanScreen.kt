package com.example.sibeka.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun SambutanScreen(
    onMulaiPetualangan: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dataStore = SiBekaApp.instance.container.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    var namaInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "avatarBounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LatarKrem)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = LatarKrem)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar Maskot melompat lembut
                Box(
                    modifier = Modifier
                        .offset(y = offsetY.dp)
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(HijauPucat),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌿  🐒  🌿", fontSize = 38.sp)
                        Text("Halo! Aku Si Beka 👋", color = HijauTua, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Alam Indonesia,\nsejuta cerita!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = HijauTua,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Kenali flora dan fauna, pecahkan kuis, lalu isi buku petualanganmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AbuTua,
                    textAlign = TextAlign.Center
                )

                // Input Nama
                OutlinedTextField(
                    value = namaInput,
                    onValueChange = {
                        namaInput = it
                        if (it.isNotBlank()) errorMessage = ""
                    },
                    label = { Text("Nama Kamu") },
                    placeholder = { Text("contoh: Budi / Siti") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HijauUtama,
                        unfocusedBorderColor = GarisKartu
                    )
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MerahSalah
                    )
                }

                // Tombol Mulai
                Button(
                    onClick = {
                        if (namaInput.isBlank()) {
                            errorMessage = "Tulis namamu dulu ya! 😊"
                            sfxPlayer.playSalah()
                        } else {
                            sfxPlayer.playClick()
                            coroutineScope.launch {
                                dataStore.simpanNamaPemain(namaInput.trim())
                                onMulaiPetualangan()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauUtama),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Mulai menjelajah  →",
                        style = MaterialTheme.typography.titleMedium,
                        color = PutihBersih,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
