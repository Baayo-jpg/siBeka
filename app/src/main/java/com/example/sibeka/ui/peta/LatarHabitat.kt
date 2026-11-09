package com.example.sibeka.ui.peta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.sibeka.ui.theme.LatarKrem
import com.example.sibeka.ui.theme.PutihBersih
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.sibeka.util.cariAsetPermainan

/**
 * Latar satu bagian jalur habitat.
 *
 * Bila `assets/latar/<namaAset>.<webp|png|svg>` ada, gambar itu yang dipakai.
 * Bila belum, latar digambar langsung dengan Compose: gradasi warna habitat
 * ditambah siluet pemandangan tipis agar jalur tidak terlihat kosong.
 */
@Composable
fun LatarBagian(
    namaAset: List<String>,
    idHabitat: String,
    warna: Color,
    modifier: Modifier = Modifier
) {
    val path = remember(namaAset) {
        namaAset.firstNotNullOfOrNull { cariAsetPermainan(it, folder = "latar") }
    }
    if (path != null) {
        AsyncImage(
            model = path,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        return
    }
    Canvas(modifier.fillMaxSize()) { gambarPemandangan(idHabitat, warna) }
}

private fun DrawScope.gambarPemandangan(idHabitat: String, warna: Color) {
    drawRect(
        Brush.verticalGradient(
            0f to warna.copy(alpha = 0.18f),
            0.65f to warna.copy(alpha = 0.05f),
            1f to LatarKrem
        )
    )
    when (idHabitat) {
        "LT" -> pemandanganLaut(warna)
        "SG" -> pemandanganSungai(warna)
        "RW" -> pemandanganRawa(warna)
        "GN" -> pemandanganGunung(warna)
        else -> pemandanganHutan(warna)
    }
}

/** Perbukitan berlapis dengan siluet pohon. */
private fun DrawScope.pemandanganHutan(warna: Color) {
    val w = size.width
    val h = size.height

    val bukitBelakang = Path().apply {
        moveTo(0f, h * 0.60f)
        cubicTo(w * 0.22f, h * 0.52f, w * 0.34f, h * 0.64f, w * 0.52f, h * 0.58f)
        cubicTo(w * 0.72f, h * 0.52f, w * 0.84f, h * 0.62f, w, h * 0.56f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(bukitBelakang, warna.copy(alpha = 0.09f))

    val bukitDepan = Path().apply {
        moveTo(0f, h * 0.80f)
        cubicTo(w * 0.26f, h * 0.70f, w * 0.44f, h * 0.84f, w * 0.64f, h * 0.76f)
        cubicTo(w * 0.80f, h * 0.70f, w * 0.92f, h * 0.78f, w, h * 0.74f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(bukitDepan, warna.copy(alpha = 0.12f))

    val dasar = h * 0.82f
    repeat(7) { i ->
        val x = w * (0.06f + i * 0.145f)
        val tinggi = h * (0.09f + (i % 3) * 0.025f)
        val pohon = Path().apply {
            moveTo(x, dasar - tinggi)
            lineTo(x - tinggi * 0.42f, dasar)
            lineTo(x + tinggi * 0.42f, dasar)
            close()
        }
        drawPath(pohon, warna.copy(alpha = 0.13f))
    }
}

/** Cakrawala laut, matahari, dan tiga lapis gelombang. */
private fun DrawScope.pemandanganLaut(warna: Color) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = PutihBersih.copy(alpha = 0.55f),
        radius = w * 0.085f,
        center = Offset(w * 0.80f, h * 0.10f)
    )

    repeat(3) { lapis ->
        val y = h * (0.58f + lapis * 0.13f)
        val ombak = Path().apply {
            moveTo(-w * 0.05f, y)
            cubicTo(w * 0.25f, y - 16f, w * 0.55f, y + 16f, w * 1.05f, y)
        }
        drawPath(
            ombak,
            warna.copy(alpha = 0.13f - lapis * 0.02f),
            style = Stroke(width = 8.dp.toPx())
        )
    }

    val buih = Path().apply {
        moveTo(0f, h * 0.55f)
        cubicTo(w * 0.30f, h * 0.53f, w * 0.62f, h * 0.57f, w, h * 0.545f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(buih, warna.copy(alpha = 0.05f))
}

/** Pita sungai berkelok dengan rumpun alang-alang. */
private fun DrawScope.pemandanganSungai(warna: Color) {
    val w = size.width
    val h = size.height

    val aliran = Path().apply {
        moveTo(-w * 0.10f, h * 0.30f)
        cubicTo(w * 0.30f, h * 0.42f, w * 0.70f, h * 0.55f, w * 1.10f, h * 0.72f)
    }
    drawPath(aliran, warna.copy(alpha = 0.10f), style = Stroke(width = 46.dp.toPx()))
    drawPath(aliran, warna.copy(alpha = 0.08f), style = Stroke(width = 20.dp.toPx()))

    val dasar = h * 0.90f
    repeat(9) { i ->
        val x = w * (0.05f + i * 0.115f)
        val tinggi = h * (0.06f + (i % 4) * 0.015f)
        drawLine(
            color = warna.copy(alpha = 0.16f),
            start = Offset(x, dasar),
            end = Offset(x + tinggi * 0.35f, dasar - tinggi),
            strokeWidth = 3.dp.toPx()
        )
    }
}

/** Genangan air dan daun teratai. */
private fun DrawScope.pemandanganRawa(warna: Color) {
    val w = size.width
    val h = size.height

    repeat(3) { i ->
        val y = h * (0.62f + i * 0.11f)
        drawOval(
            color = warna.copy(alpha = 0.07f),
            topLeft = Offset(w * (0.05f + i * 0.06f), y),
            size = androidx.compose.ui.geometry.Size(w * 0.55f, h * 0.045f)
        )
    }

    val posisiDaun = listOf(0.18f to 0.68f, 0.62f to 0.76f, 0.36f to 0.86f, 0.80f to 0.90f)
    posisiDaun.forEach { (fx, fy) ->
        drawCircle(warna.copy(alpha = 0.12f), radius = w * 0.045f, center = Offset(w * fx, h * fy))
        drawCircle(PutihBersih.copy(alpha = 0.35f), radius = w * 0.012f, center = Offset(w * fx, h * fy))
    }
}

/** Dua puncak gunung dan kabut tipis. */
private fun DrawScope.pemandanganGunung(warna: Color) {
    val w = size.width
    val h = size.height

    val puncakJauh = Path().apply {
        moveTo(w * 0.10f, h * 0.62f)
        lineTo(w * 0.38f, h * 0.26f)
        lineTo(w * 0.64f, h * 0.62f)
        close()
    }
    drawPath(puncakJauh, warna.copy(alpha = 0.08f))

    val puncakDekat = Path().apply {
        moveTo(w * 0.42f, h * 0.70f)
        lineTo(w * 0.72f, h * 0.34f)
        lineTo(w * 1.02f, h * 0.70f)
        close()
    }
    drawPath(puncakDekat, warna.copy(alpha = 0.12f))

    val kabut = Path().apply {
        moveTo(0f, h * 0.66f)
        cubicTo(w * 0.28f, h * 0.61f, w * 0.58f, h * 0.70f, w, h * 0.64f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(kabut, PutihBersih.copy(alpha = 0.22f))
}
