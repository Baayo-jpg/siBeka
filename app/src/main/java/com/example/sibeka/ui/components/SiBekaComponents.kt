package com.example.sibeka.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset

enum class TabUtama { PETA, KOLEKSI, TOKO }

// ── Maskot ──────────────────────────────────────────────────────────────────
/**
 * Maskot Si Beka. Saat ini masih emoji.
 * Ilustrasi SVG dari assets dapat ditampilkan melalui Coil AsyncImage.
 */
@Composable
fun MaskotSiBeka(modifier: Modifier = Modifier, size: Dp = 48.dp, melompat: Boolean = false) {
    val transisi = rememberInfiniteTransition(label = "maskot")
    val naik by transisi.animateFloat(
        initialValue = 0f,
        targetValue = if (melompat) -8f else 0f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "naik"
    )
    Box(modifier.size(size).offset(y = naik.dp), contentAlignment = Alignment.Center) {
        GambarAset("maskot", "🐒", Modifier.fillMaxSize(), ukuranCadangan = (size.value * 0.78f).sp)
    }
}

// ── Kartu & label dasar ─────────────────────────────────────────────────────
@Composable
fun KartuSiBeka(
    modifier: Modifier = Modifier,
    container: Color = PutihBersih,
    borderColor: Color = GarisKartu,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dasar = modifier.clip(shape).background(container).border(borderWidth, borderColor, shape)
    Column(modifier = if (onClick != null) dasar.clickable(onClick = onClick) else dasar, content = content)
}

@Composable
fun PillLabel(
    teks: String,
    modifier: Modifier = Modifier,
    container: Color = HijauBenarMuda,
    contentColor: Color = HijauTua,
    icon: ImageVector? = null
) {
    Row(
        modifier.clip(RoundedCornerShape(50)).background(container).padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (icon != null) Icon(icon, null, tint = contentColor, modifier = Modifier.size(13.dp))
        Text(teks, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = contentColor, letterSpacing = 0.6.sp)
    }
}

// ── Chip koin & bintang ─────────────────────────────────────────────────────
@Composable
private fun ChipPil(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    Surface(modifier, shape = RoundedCornerShape(50), color = PutihBersih, border = BorderStroke(1.dp, GarisKartu)) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            content = content
        )
    }
}

@Composable
fun ChipKoin(jumlah: Int, modifier: Modifier = Modifier) = ChipPil(modifier) {
    Box(Modifier.size(18.dp).clip(CircleShape).background(KuningBintang), contentAlignment = Alignment.Center) {
        Text("J", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = CoklatTua)
    }
    Text("$jumlah", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut)
}

@Composable
fun ChipBintang(jumlah: Int, modifier: Modifier = Modifier) = ChipPil(modifier) {
    Icon(Icons.Default.Star, null, tint = KuningBintang, modifier = Modifier.size(16.dp))
    Text("$jumlah", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut)
}

/** Bintang dan koin dalam satu pil ringkas, dipakai header yang ingin tampil minimalis. */
@Composable
fun ChipStatistik(bintang: Int, koin: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier, shape = RoundedCornerShape(50), color = PutihBersih,
        border = BorderStroke(1.dp, GarisKartu)
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(Icons.Default.Star, null, tint = KuningBintang, modifier = Modifier.size(14.dp))
            Text("$bintang", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut)
            Box(Modifier.padding(horizontal = 2.dp).width(1.dp).height(11.dp).background(GarisKartu))
            Box(
                Modifier.size(15.dp).clip(CircleShape).background(KuningBintang),
                contentAlignment = Alignment.Center
            ) {
                Text("J", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = CoklatTua)
            }
            Text("$koin", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut)
        }
    }
}

// ── Header ──────────────────────────────────────────────────────────────────
@Composable
/**
 * Header layar: judul di kiri, chip bintang/koin di kanan, subjudul di bawahnya.
 *
 * Tanpa [judul] yang dipakai adalah label "JELAJAH ALAM". Di layar yang punya
 * judul sendiri (Koleksi, Toko), isi [judul] dan [subjudul] supaya judul layar
 * menempati posisi header ini, bukan tampil lagi di dalam konten.
 */
fun HeaderJelajahAlam(
    modifier: Modifier = Modifier,
    judul: String? = null,
    subjudul: String? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (judul != null) {
                Text(
                    judul, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua,
                    maxLines = 1, modifier = Modifier.weight(1f)
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.Explore, null, tint = HijauTua, modifier = Modifier.size(20.dp))
                    Text("JELAJAH ALAM", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, letterSpacing = 0.5.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), content = trailing)
        }
        if (judul != null && subjudul != null) {
            Text(subjudul, fontSize = 12.sp, color = AbuTua)
        }
    }
}

@Composable
fun TombolIkonBulat(icon: ImageVector, deskripsi: String, onClick: () -> Unit, modifier: Modifier = Modifier, ukuran: Dp = 40.dp) {
    Box(
        modifier.size(ukuran).clip(CircleShape).background(PutihBersih)
            .border(1.dp, GarisKartu, CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, deskripsi, tint = HitamLembut, modifier = Modifier.size(ukuran * 0.5f))
    }
}

// ── Tombol "tebal" (ada sisi bawah) ─────────────────────────────────────────
@Composable
private fun TombolTebal(
    teks: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean,
    container: Color, edge: Color, contentColor: Color, outline: Color?, icon: ImageVector?
) {
    val interaksi = remember { MutableInteractionSource() }
    val ditekan by interaksi.collectIsPressedAsState()
    val bentuk = RoundedCornerShape(16.dp)
    Box(modifier.fillMaxWidth().height(56.dp)) {
        Box(Modifier.fillMaxWidth().height(52.dp).offset(y = 4.dp).clip(bentuk).background(edge))
        Box(
            Modifier.fillMaxWidth().height(52.dp)
                .offset(y = if (ditekan && enabled) 3.dp else 0.dp)
                .clip(bentuk).background(container)
                .then(if (outline != null) Modifier.border(1.dp, outline, bentuk) else Modifier)
                .clickable(interactionSource = interaksi, indication = null, enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(teks, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = contentColor)
                if (icon != null) Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun TombolUtama(teks: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null, enabled: Boolean = true) =
    TombolTebal(teks, onClick, modifier, enabled, HijauTua, BayanganTombol, PutihBersih, null, icon)

@Composable
fun TombolSekunder(teks: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null, enabled: Boolean = true) =
    TombolTebal(
        teks, onClick, modifier, enabled, PutihBersih, BayanganKartu,
        if (enabled) HijauTua else AbuTua, GarisKartu, icon
    )

// ── Bubble Si Beka ──────────────────────────────────────────────────────────
@Composable
fun BubbleSiBeka(teks: String, modifier: Modifier = Modifier, latar: Color = MaskotLatar) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        MaskotSiBeka(size = 52.dp)
        Column(
            Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(latar).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text("SI BEKA", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = HijauBenar, letterSpacing = 0.6.sp)
            Text(teks, fontSize = 12.5.sp, lineHeight = 17.sp, color = HitamLembut)
        }
    }
}

// ── Bottom navigation (Peta · Buku Koleksi · Toko) ──────────────────────────
@Composable
fun SiBekaBottomBar(terpilih: TabUtama, onPeta: () -> Unit, onKoleksi: () -> Unit, onToko: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(PutihBersih)) {
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(GarisKartu))
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().height(52.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemTab(Icons.Outlined.Map, "Peta", terpilih == TabUtama.PETA, onPeta)
            ItemTab(Icons.Outlined.MenuBook, "Koleksi", terpilih == TabUtama.KOLEKSI, onKoleksi)
            ItemTab(Icons.Outlined.ShoppingBag, "Toko", terpilih == TabUtama.TOKO, onToko)
        }
    }
}

/**
 * Tab ringkas: hanya ikon dan label kecil. Tab aktif dibedakan lewat warna dan
 * ketebalan huruf, plus garis tipis di bawah label — tanpa kotak latar supaya
 * bar tetap ringan.
 */
@Composable
private fun RowScope.ItemTab(icon: ImageVector, label: String, terpilih: Boolean, onClick: () -> Unit) {
    val warna = if (terpilih) HijauTua else AbuTua
    Column(
        Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)
            .padding(vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Icon(icon, label, tint = warna, modifier = Modifier.size(21.dp))
        Text(
            label, fontSize = 10.sp, lineHeight = 12.sp, maxLines = 1,
            fontWeight = if (terpilih) FontWeight.ExtraBold else FontWeight.Medium,
            color = warna
        )
        Box(
            Modifier.padding(top = 1.dp).width(14.dp).height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (terpilih) HijauTua else Color.Transparent)
        )
    }
}