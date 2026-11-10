package com.example.sibeka.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.sibeka.SiBekaApp

/**
 * Pemuatan ikon/latar permainan dari assets.
 *
 * Letak file: `assets/ikon/<nama>.<ext>` dan `assets/latar/<nama>.<ext>` dengan
 * ekstensi yang didukung: webp, png, svg (urutan prioritas). Bila file belum ada,
 * komponen menampilkan cadangan (emoji) sehingga aplikasi tetap berjalan.
 */
private val EKSTENSI_DIDUKUNG = listOf("webp", "png", "svg")

/** Mengembalikan URI aset bila file ada, atau null bila belum disediakan. */
fun cariAsetPermainan(nama: String, folder: String = "ikon"): String? {
    if (nama.isBlank()) return null
    val aset = SiBekaApp.instance.assets
    return EKSTENSI_DIDUKUNG.firstNotNullOfOrNull { ekstensi ->
        val path = "$folder/$nama.$ekstensi"
        if (runCatching { aset.open(path).close() }.isSuccess) "file:///android_asset/$path" else null
    }
}

/**
 * Menampilkan ikon aset; memakai [cadangan] (emoji) saat file belum ada atau gagal dimuat.
 */
@Composable
fun GambarAset(
    nama: String,
    cadangan: String,
    modifier: Modifier = Modifier,
    ukuranCadangan: TextUnit = 26.sp,
    contentScale: ContentScale = ContentScale.Fit,
    folder: String = "ikon"
) = GambarAset(listOf(nama), cadangan, modifier, ukuranCadangan, contentScale, folder)

/**
 * Versi berlapis: memakai nama pertama yang filenya tersedia.
 * Urutan menentukan prioritas, mis. ikon khusus habitat lalu ikon umum.
 */
@Composable
fun GambarAset(
    nama: List<String>,
    cadangan: String,
    modifier: Modifier = Modifier,
    ukuranCadangan: TextUnit = 26.sp,
    contentScale: ContentScale = ContentScale.Fit,
    folder: String = "ikon"
) {
    val path = remember(nama, folder) { nama.firstNotNullOfOrNull { cariAsetPermainan(it, folder) } }
    var gagal by remember(path) { mutableStateOf(false) }

    Box(modifier, contentAlignment = Alignment.Center) {
        if (path != null && !gagal) {
            AsyncImage(
                model = path,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) gagal = true
                }
            )
        } else {
            Text(cadangan, fontSize = ukuranCadangan)
        }
    }
}
