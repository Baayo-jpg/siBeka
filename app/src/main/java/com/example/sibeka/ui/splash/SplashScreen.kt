package com.example.sibeka.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.sibeka.R
import com.example.sibeka.ui.theme.HijauUtama
import com.example.sibeka.ui.theme.PutihBersih
import kotlinx.coroutines.delay

/**
 * Layar pembuka: maskot melambai di atas latar hijau, lalu isinya memudar
 * sebelum aplikasi dibuka.
 *
 * Animasinya berasal dari berkas Rive `res/raw/splash_melambai.riv`, hasil ekspor
 * proyek `rive/splash` (artboard `Splash`, animasi `Melambai`, 150 frame, loop).
 * Rig Compose yang lama sudah diganti karena Rive membawa gerakan yang sama
 * dengan berkas yang bisa disunting di editor Rive.
 */
private const val LEBAR_MASKOT_DP = 232f
private const val TINGGI_MASKOT_DP = LEBAR_MASKOT_DP * 765f / 741f

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val alfa = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        delay(1750)
        alfa.animateTo(0f, tween(durationMillis = 380, easing = EaseInOut))
        onSplashFinished()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(HijauUtama),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { alpha = alfa.value }
        ) {
            AndroidView(
                modifier = Modifier.size(LEBAR_MASKOT_DP.dp, TINGGI_MASKOT_DP.dp),
                factory = { ctx ->
                    RiveAnimationView(ctx).apply {
                        setRiveResource(
                            R.raw.splash_melambai,
                            artboardName = "Splash",
                            animationName = "Melambai",
                            autoplay = true
                        )
                    }
                }
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "siBeka",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PutihBersih,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Alam Indonesia, sejuta cerita!",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PutihBersih.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}
