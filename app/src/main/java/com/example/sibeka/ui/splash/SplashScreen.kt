package com.example.sibeka.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animasi scale pop-up
    val scale = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(1800) // Tampil 2 detik lalu lanjut
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LatarKrem),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.scale(scale.value)
        ) {
            // Maskot Si Beka Avatar
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(HijauPucat, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🐒", fontSize = 72.sp)
            }

            // Judul Aplikasi
            Text(
                text = "Si Beka",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp),
                color = HijauTua,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            // Subjudul
            Text(
                text = "Alam Indonesia, sejuta cerita!",
                style = MaterialTheme.typography.titleMedium,
                color = CoklatTua,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
