package com.example.sibeka.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.ui.components.MaskotSiBeka
import com.example.sibeka.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        delay(1800)
        onSplashFinished()
    }

    Box(Modifier.fillMaxSize().background(LatarKrem), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.scale(scale.value)
        ) {
            Box(Modifier.size(140.dp).background(HijauPucat, CircleShape), contentAlignment = Alignment.Center) {
                MaskotSiBeka(size = 100.dp)
            }
            Text("Si Beka", fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, textAlign = TextAlign.Center)
            Text("Alam Indonesia, sejuta cerita!", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CoklatTua, textAlign = TextAlign.Center)
        }
    }
}