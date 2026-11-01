package com.example.sibeka.ui.kuis.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.data.model.StatusJawaban
import com.example.sibeka.ui.theme.*

@Composable
fun KuisTopBar(
    namaHabitat: String,
    totalKoin: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tombol Close kuis
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
                .shadow(2.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup Kuis",
                tint = HitamLembut
            )
        }

        // Judul Habitat
        Text(
            text = namaHabitat,
            style = MaterialTheme.typography.titleLarge,
            color = HitamLembut,
            fontWeight = FontWeight.ExtraBold
        )

        // Indikator Koin
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = KuningMuda,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, KuningBintang),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(KuningBintang),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¢",
                        color = PutihBersih,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$totalKoin",
                    style = MaterialTheme.typography.labelLarge,
                    color = CoklatTua,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun ProgresSoalBar(
    currentStep: Int,
    totalSteps: Int,
    bintangTerkumpul: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Segmented Step Bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 1..totalSteps) {
                val isCompleted = i < currentStep
                val isCurrent = i == currentStep
                val barColor = when {
                    isCompleted -> HijauUtama
                    isCurrent -> HijauBenar
                    else -> GarisKartu
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }
        }

        // Keterangan teks & bintang
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Soal $currentStep dari $totalSteps",
                style = MaterialTheme.typography.bodySmall,
                color = AbuTua,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Bintang",
                    tint = KuningBintang,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$bintangTerkumpul Bintang",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoklatTua,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KuisHeader(
    jenisKuis: String,
    instruksi: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = jenisKuis,
            style = MaterialTheme.typography.headlineSmall,
            color = HijauUtama,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start
        )
        Text(
            text = instruksi,
            style = MaterialTheme.typography.bodyMedium,
            color = AbuTua,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UmpanBalikPanel(
    status: StatusJawaban,
    faktaUnik: String,
    onLanjutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBenar = status == StatusJawaban.BENAR
    val bgColor = if (isBenar) HijauBenarMuda else MerahMuda
    val borderColor = if (isBenar) HijauBenar else MerahSalah
    val titleText = if (isBenar) "Hebat! Jawabanmu Benar! 🎉" else "Yah, Kurang Tepat! Semangat! 💪"

    // Animasi lompat Si Beka
    val infiniteTransition = rememberInfiniteTransition(label = "BekaJump")
    val bekaOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bekaBounce"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header status
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isBenar) HijauTua else MerahSalah
            )

            // Pesan Si Beka & Fakta Unik
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar Maskot Si Beka sederhana dengan animasi bouncing
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .offset(y = bekaOffsetY.dp),
                    shape = CircleShape,
                    color = MaskotLatar,
                    shadowElevation = 3.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isBenar) "🐒" else "🐒",
                            fontSize = 28.sp
                        )
                    }
                }

                // Fakta Unik Card
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Tahukah Kamu?",
                        style = MaterialTheme.typography.labelLarge,
                        color = CoklatTua,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = faktaUnik,
                        style = MaterialTheme.typography.bodySmall,
                        color = HitamLembut,
                        lineHeight = 18.sp
                    )
                }
            }

            // Tombol Aksi Lanjut
            Button(
                onClick = onLanjutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBenar) HijauUtama else OranjeAksen
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Lanjut Soal Berikutnya",
                    style = MaterialTheme.typography.labelLarge,
                    color = PutihBersih,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
