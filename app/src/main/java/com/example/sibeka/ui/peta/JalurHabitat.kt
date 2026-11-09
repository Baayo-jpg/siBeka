package com.example.sibeka.ui.peta

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.ui.components.PillLabel
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset
import com.example.sibeka.util.cariAsetPermainan

/** Tema visual tiap habitat: warna utama, emoji, dan deskripsi singkat. */
data class TemaHabitat(
    val warna: Color,
    val emoji: String,
    val deskripsi: String,
    /** Nama file ikon di assets/ikon/ tanpa ekstensi. */
    val aset: String = ""
)

fun temaHabitat(habitatId: String): TemaHabitat = when (habitatId) {
    "HH" -> TemaHabitat(WarnaHutan, "🌳", "Hutan rimbun kaya flora fauna endemik", "habitat_hh")
    "LT" -> TemaHabitat(WarnaLaut, "🌊", "Kedalaman samudra penuh biota menakjubkan", "habitat_lt")
    "SG" -> TemaHabitat(WarnaSungai, "🛶", "Arus air tawar tempat pesut & ikan endemik", "habitat_sg")
    "RW" -> TemaHabitat(WarnaRawa, "🌿", "Lahan basah gambut rumah bagi bekantan", "habitat_rw")
    "GN" -> TemaHabitat(WarnaGunung, "⛰️", "Puncak berkabut dengan satwa unik dataran tinggi", "habitat_gn")
    else -> TemaHabitat(HijauUtama, "🌍", "Habitat alam lestari")
}

/** Satu simpul pada jalur belajar. */
data class SimpulBelajar(
    val id: String,
    val ikon: String,
    val judul: String,
    val selesai: Boolean,
    val aktif: Boolean,
    /** Nama file ikon di assets/ikon/ tanpa ekstensi. */
    val aset: String = ""
)

private val UKURAN_SIMPUL = 70.dp
private val TINGGI_SISI = 6.dp
private val JARAK_SIMPUL = 118.dp
private val Y_AWAL = 38.dp
private val RUANG_BAWAH = 30.dp

/** Posisi horizontal simpul (fraksi lebar layar) agar jalur berkelok seperti halaman utama Duolingo. */
private val FRAKSI_X = listOf(0.5f, 0.74f, 0.5f, 0.26f)

/** Format nama resmi habitat untuk header & pemisah jalur. */
fun namaTampilHabitat(habitatId: String, defaultNama: String = ""): String = when (habitatId) {
    "GN" -> "Gunung"
    "HH" -> "Hutan Hujan Tropis"
    "RW" -> "Rawa"
    "SG" -> "Sungai"
    "LT" -> "Lautan"
    else -> defaultNama.ifBlank { "Habitat" }
}

/**
 * Garis pemisah antar habitat dengan teks nama habitat di tengah (gaya Duolingo).
 */
@Composable
fun PemisahHabitat(
    namaHabitat: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.4f to PutihBersih.copy(alpha = 0.45f),
                        1f to PutihBersih.copy(alpha = 0.75f)
                    ),
                    RoundedCornerShape(1.dp)
                )
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.22f))
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = namaHabitat,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PutihBersih,
                letterSpacing = 0.6.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        0f to PutihBersih.copy(alpha = 0.75f),
                        0.6f to PutihBersih.copy(alpha = 0.45f),
                        1f to Color.Transparent
                    ),
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

/**
 * Banner habitat melayang (floating card di depan, seperti gaya Duolingo).
 * Memakai kartu visual header bertema (ikon_header_*) dengan efek shadow/melayang,
 * judul habitat, deskripsi/progres, serta tombol pintasan buku panduan di sisi kanan.
 */
@Composable
fun BannerHabitat(
    nomor: Int,
    totalHabitat: Int,
    namaHabitat: String,
    tema: TemaHabitat,
    ditemukan: Int,
    jumlahSpesies: Int,
    modifier: Modifier = Modifier,
    habitatId: String = "",
    onBukaPanduan: (() -> Unit)? = null
) {
    val fraksi = if (jumlahSpesies > 0) {
        (ditemukan.toFloat() / jumlahSpesies).coerceIn(0.001f, 1f)
    } else {
        0.001f
    }

    val isGunung = habitatId == "GN"
    val warnaTeksUtama = if (isGunung) Color(0xFF142E3D) else PutihBersih
    val warnaTeksSub = if (isGunung) Color(0xFF234C62) else PutihBersih.copy(alpha = 0.90f)
    val warnaDivider = if (isGunung) Color(0xFF142E3D).copy(alpha = 0.22f) else PutihBersih.copy(alpha = 0.35f)
    val warnaTrackBg = if (isGunung) Color(0xFF142E3D).copy(alpha = 0.15f) else PutihBersih.copy(alpha = 0.24f)

    val namaAsetHeader = remember(habitatId) {
        when (habitatId) {
            "GN" -> listOf("ikon_header_gunung", "header_gunung", "habitat_gn")
            "HH" -> listOf("ikon_header_hutanhujantropis", "ikon_header_hutan", "header_hutan", "habitat_hh")
            "RW" -> listOf("ikon_header_rawa", "header_rawa", "habitat_rw")
            "SG" -> listOf("ikon_header_sungai", "header_sungai", "habitat_sg")
            "LT" -> listOf("ikon_header_lautan", "ikon_header_laut", "header_laut", "habitat_lt")
            else -> listOf("ikon_header_${habitatId.lowercase()}")
        }
    }

    val namaTampil = remember(habitatId, namaHabitat) {
        namaTampilHabitat(habitatId, namaHabitat)
    }

    // Outer container transparan (TIDAK ADA background solid agar benar-benar melayang di atas peta)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Floating Card dengan bayangan (elevation) nyata di atas peta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(20.dp))
                .background(tema.warna)
        ) {
            // 1. Gambar kartu ilustrasi header bertema
            GambarAset(
                nama = namaAsetHeader,
                cadangan = "",
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            // 2. Konten kartu (gaya unit Duolingo)
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sisi Kiri: Label habitat & judul/deskripsi
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "HABITAT $nomor · ${namaTampil.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = warnaTeksSub,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = tema.deskripsi.ifBlank { "Jelajah Alam Nusantara" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = warnaTeksUtama,
                            maxLines = 1
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Garis Pemisah Vertikal
                    Box(
                        Modifier
                            .width(1.5.dp)
                            .height(38.dp)
                            .background(warnaDivider, RoundedCornerShape(1.dp))
                    )

                    Spacer(Modifier.width(8.dp))

                    // Sisi Kanan: Tombol Buku Panduan / Koleksi
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (onBukaPanduan != null) {
                                    Modifier.clickable(onClick = onBukaPanduan)
                                } else Modifier
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = "Buku Koleksi",
                            tint = warnaTeksUtama,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "$ditemukan/$jumlahSpesies",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = warnaTeksUtama
                        )
                    }
                }

                // 3. Slim Progress bar menyatu di dasar kartu
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .background(warnaTrackBg)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraksi)
                            .fillMaxHeight()
                            .background(KuningBintang)
                    )
                }
            }
        }
    }
}

/**
 * Jalur belajar berkelok: satu garis melengkung dengan simpul-simpul mode kuis di atasnya.
 */
@Composable
fun JalurBelajar(
    simpul: List<SimpulBelajar>,
    warna: Color,
    modifier: Modifier = Modifier,
    /** Kode habitat (HH/LT/SG/RW/GN) untuk memilih pemandangan latar. */
    idHabitat: String = "",
    /** Nama habitat bila perlu menampilkan pemisah yang menyatu di atas background. */
    namaHabitat: String? = null,
    onKlikSimpul: (SimpulBelajar) -> Unit
) {
    if (simpul.isEmpty()) return

    val yOffsetAwal = if (namaHabitat != null) 68.dp else Y_AWAL
    val tinggiMinimum = LocalConfiguration.current.screenHeightDp.dp * 0.86f
    val tinggiIsi = yOffsetAwal + JARAK_SIMPUL * (simpul.size - 1) + UKURAN_SIMPUL / 2 + RUANG_BAWAH
    val tinggi = maxOf(tinggiIsi, tinggiMinimum)
    val jarak = if (simpul.size > 1) {
        (tinggi - yOffsetAwal - UKURAN_SIMPUL / 2 - RUANG_BAWAH) / (simpul.size - 1)
    } else {
        JARAK_SIMPUL
    }

    BoxWithConstraints(modifier.fillMaxWidth().height(tinggi)) {
        val lebar = maxWidth

        // Background pemandangan penuh 100% tanpa batas
        LatarBagian(
            slugHabitat(idHabitat).flatMap { listOf("latar_$it", "background_$it") },
            idHabitat,
            warna,
            Modifier.matchParentSize()
        )
        // Lapisan tipis agar simpul dan garis jalur tetap terbaca di atas latar.
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to PutihBersih.copy(alpha = 0.12f),
                        0.40f to PutihBersih.copy(alpha = 0.02f),
                        1f to PutihBersih.copy(alpha = 0.20f)
                    )
                )
        )

        // Bila ada pemisah antar-habitat, tampilkan langsung di atas background (menyatu mulus)
        if (namaHabitat != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.12f),
                            1f to Color.Transparent
                        )
                    )
            )
            PemisahHabitat(
                namaHabitat = namaHabitat,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            )
        }

        Canvas(Modifier.fillMaxSize()) {
            if (simpul.size < 2) return@Canvas
            val x = simpul.indices.map { size.width * FRAKSI_X[it % FRAKSI_X.size] }
            val y = simpul.indices.map { yOffsetAwal.toPx() + jarak.toPx() * it }

            val jalur = Path().apply {
                moveTo(x[0], y[0])
                for (i in 1 until simpul.size) {
                    val tengahY = (y[i - 1] + y[i]) / 2f
                    cubicTo(x[i - 1], tengahY, x[i], tengahY, x[i], y[i])
                }
            }
            drawPath(jalur, warna.copy(alpha = 0.13f), style = Stroke(28.dp.toPx(), cap = StrokeCap.Round))
            drawPath(jalur, warna.copy(alpha = 0.28f), style = Stroke(17.dp.toPx(), cap = StrokeCap.Round))
        }

        simpul.forEachIndexed { index, item ->
            val x = lebar * FRAKSI_X[index % FRAKSI_X.size]
            val y = yOffsetAwal + jarak * index

            Box(Modifier.offset(x = x - UKURAN_SIMPUL / 2, y = y - UKURAN_SIMPUL / 2)) {
                SimpulLingkaran(item, warna, idHabitat) { onKlikSimpul(item) }
            }

            if (item.aktif) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .offset(y = y + UKURAN_SIMPUL / 2 + 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PillLabel("MULAI", container = HijauTua, contentColor = PutihBersih)
                }
            }
        }
    }
}

@Composable
private fun SimpulLingkaran(item: SimpulBelajar, warna: Color, idHabitat: String, onClick: () -> Unit) {
    val interaksi = remember { MutableInteractionSource() }
    val ditekan by interaksi.collectIsPressedAsState()
    val skala = if (ditekan) 0.93f else 1f

    val denyut by rememberInfiniteTransition(label = "denyut-simpul").animateFloat(
        initialValue = 0.25f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "alpha-cincin"
    )

    val terisi = item.selesai || item.aktif

    val kandidatIkon = remember(item, idHabitat) { kandidatAsetSimpul(item, idHabitat) }
    val adaIkon = remember(kandidatIkon) { kandidatIkon.any { cariAsetPermainan(it) != null } }

    Box(Modifier.size(UKURAN_SIMPUL, UKURAN_SIMPUL + TINGGI_SISI).scale(skala)) {
        Box(
            Modifier
                .offset(y = TINGGI_SISI)
                .size(UKURAN_SIMPUL)
                .clip(CircleShape)
                .background(
                    when {
                        adaIkon -> Color.Black.copy(alpha = 0.22f)
                        terisi -> warna.copy(alpha = 0.45f)
                        else -> GarisKartu
                    }
                )
        )
        Box(
            Modifier
                .size(UKURAN_SIMPUL)
                .clip(CircleShape)
                .then(
                    if (adaIkon) Modifier else Modifier.background(if (terisi) warna else PutihBersih)
                )
                .then(
                    if (!terisi && !adaIkon) {
                        Modifier.border(2.dp, warna.copy(alpha = 0.35f), CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(interactionSource = interaksi, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (item.selesai && !adaIkon) {
                Icon(Icons.Default.Check, "Selesai", tint = PutihBersih, modifier = Modifier.size(30.dp))
            } else if (adaIkon) {
                // Lencana habitat sudah berbentuk bulat: dipakai sebagai muka simpul penuh.
                GambarAset(
                    kandidatIkon,
                    item.ikon,
                    Modifier
                        .fillMaxSize()
                        .padding(1.5.dp)
                        .clip(CircleShape),
                    ukuranCadangan = 26.sp
                )
            } else {
                GambarAset(kandidatIkon, item.ikon, Modifier.size(40.dp), ukuranCadangan = 26.sp)
            }

            // Habitat tuntas: ikon aset tetap tampil, status selesai ditandai lencana kecil.
            if (item.selesai && adaIkon) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 3.dp, y = 3.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(HijauTua)
                        .border(2.dp, PutihBersih, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, "Selesai", tint = PutihBersih, modifier = Modifier.size(13.dp))
                }
            }
        }
        if (item.aktif) {
            Box(
                Modifier
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(UKURAN_SIMPUL + 8.dp)
                    .clip(CircleShape)
                    .border(3.dp, KuningBintang.copy(alpha = denyut), CircleShape)
            )
        }
    }
}

/** Alias nama habitat untuk pencarian file aset (folder aset & nama file yang sudah dipakai). */
private fun slugHabitat(idHabitat: String): List<String> = when (idHabitat) {
    "HH" -> listOf("hutan_hujan_tropis", "hutanhujantropis", "hutan", "hh")
    "LT" -> listOf("laut", "lt")
    "SG" -> listOf("sungai", "sg", "laut", "lt")
    "RW" -> listOf("rawa", "rw")
    "GN" -> listOf("gunung", "gn")
    else -> listOf(idHabitat.lowercase())
}

/**
 * Urutan prioritas ikon simpul:
 * 1. `ikon_level_<habitat>` untuk simpul kuis, `ikon_last_<habitat>` untuk simpul terakhir;
 * 2. ikon khusus mode per habitat (`mode_<mode>_<habitat>`);
 * 3. ikon umum (`mode_<mode>` / `simpul_hadiah` / `simpul_trofi`);
 * 4. emoji sebagai cadangan.
 */
private fun kandidatAsetSimpul(item: SimpulBelajar, idHabitat: String): List<String> {
    val slug = slugHabitat(idHabitat)
    if (!item.id.startsWith("mode:")) {
        return slug.map { "ikon_last_$it" } + listOf(item.aset)
    }
    val mode = item.id.removePrefix("mode:").lowercase()
    return slug.map { "ikon_level_$it" } + slug.map { "mode_${mode}_$it" } + listOf(item.aset)
}
