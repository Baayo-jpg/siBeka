package com.example.sibeka.ui.splash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.ui.theme.HijauUtama
import com.example.sibeka.ui.theme.PutihBersih
import kotlinx.coroutines.delay

/**
 * Layar pembuka: maskot melambai di atas latar hijau, lalu isinya memudar
 * sebelum aplikasi dibuka.
 *
 * Maskot disimpan sebagai dua gambar: [badan] utuh dan [tangan] yang dipotong
 * di pergelangan. Tangan diputar sedikit bolak-balik pada poros pergelangan,
 * jadi gerakannya seperti melambai tanpa perlu animasi rangka.
 */
private const val LEBAR_MASKOT_DP = 232f
private const val TINGGI_MASKOT_DP = LEBAR_MASKOT_DP * 765f / 741f

/** Potongan tangan di dalam gambar maskot 741 x 765 px: 152 x 196 px di (81, 257). */
private const val TANGAN_X = 81f / 741f
private const val TANGAN_Y = 257f / 765f
private const val TANGAN_LEBAR = 152f / 741f
private const val TANGAN_TINGGI = 196f / 765f

/** Poros putaran (pergelangan) di dalam gambar tangan, ternormalisasi. */
private const val POROS_X = 0.8026316f
private const val POROS_Y = 0.872449f

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    val badan = remember { muatGambar(context, "maskot/badan.png") }
    val tangan = remember { muatGambar(context, "maskot/tangan.png") }

    val transisi = rememberInfiniteTransition(label = "lambai")
    val sudut by transisi.animateFloat(
        initialValue = -11f,
        targetValue = 11f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 640, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tangan"
    )
    val goyang by transisi.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1280, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badan"
    )

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
            Box(Modifier.offset(y = goyang.dp).size(LEBAR_MASKOT_DP.dp, TINGGI_MASKOT_DP.dp)) {
                badan?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                tangan?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(
                                x = (LEBAR_MASKOT_DP * TANGAN_X).dp,
                                y = (TINGGI_MASKOT_DP * TANGAN_Y).dp
                            )
                            .size(
                                width = (LEBAR_MASKOT_DP * TANGAN_LEBAR).dp,
                                height = (TINGGI_MASKOT_DP * TANGAN_TINGGI).dp
                            )
                            .graphicsLayer {
                                transformOrigin = TransformOrigin(POROS_X, POROS_Y)
                                rotationZ = sudut
                            }
                    )
                }
            }
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

private fun muatGambar(context: Context, path: String): Bitmap? =
    runCatching { context.assets.open(path).use { BitmapFactory.decodeStream(it) } }.getOrNull()
