package com.example.sibeka.ui.hasil

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.theme.*

@Composable
fun HasilScreen(
    bintangDidapat: Int,
    skorTotal: Int,
    onMainLagi: () -> Unit,
    onKembaliKePeta: () -> Unit
) {
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    // Animasi Scale Bintang
    val starScale1 = remember { Animatable(0f) }
    val starScale2 = remember { Animatable(0f) }
    val starScale3 = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        sfxPlayer.playBintang()
        if (bintangDidapat >= 1) {
            starScale1.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (bintangDidapat >= 2) {
            starScale2.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (bintangDidapat >= 3) {
            starScale3.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = PutihBersih)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎉 Penjelajah Hebat! 🎉",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = HijauTua,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Kamu berhasil menyelesaikan penjelajahan dengan gemilang!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AbuTua,
                    textAlign = TextAlign.Center
                )

                // 3 Bintang Apresiasi
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bintang 1",
                        tint = if (bintangDidapat >= 1) KuningBintang else AbuSedang,
                        modifier = Modifier
                            .size(54.dp)
                            .scale(if (bintangDidapat >= 1) starScale1.value else 1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bintang 2",
                        tint = if (bintangDidapat >= 2) KuningBintang else AbuSedang,
                        modifier = Modifier
                            .size(68.dp)
                            .scale(if (bintangDidapat >= 2) starScale2.value else 1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bintang 3",
                        tint = if (bintangDidapat >= 3) KuningBintang else AbuSedang,
                        modifier = Modifier
                            .size(54.dp)
                            .scale(if (bintangDidapat >= 3) starScale3.value else 1f)
                    )
                }

                // Skor & Koin Badge
                Surface(
                    color = KuningMuda,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Skor", style = MaterialTheme.typography.bodySmall, color = AbuTua)
                            Text(
                                text = "$skorTotal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = CoklatTua
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp)
                                .background(AbuSedang)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Koin Diperoleh", style = MaterialTheme.typography.bodySmall, color = AbuTua)
                            Text(
                                text = "+${bintangDidapat * 20} ¢",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = OranjeAksen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tombol Main Lagi
                Button(
                    onClick = {
                        sfxPlayer.playClick()
                        onMainLagi()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HijauUtama)
                ) {
                    Text(
                        text = "🔄 Mainkan Lagi",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PutihBersih
                    )
                }

                // Tombol Kembali ke Peta
                OutlinedButton(
                    onClick = {
                        sfxPlayer.playClick()
                        onKembaliKePeta()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "🗺️ Kembali ke Peta",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CoklatTua
                    )
                }
            }
        }
    }
}
