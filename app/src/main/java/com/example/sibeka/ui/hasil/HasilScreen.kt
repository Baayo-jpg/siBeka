package com.example.sibeka.ui.hasil

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*

@Composable
fun HasilScreen(
    jawabanBenar: Int,
    bintangDidapat: Int,
    skorTotal: Int,
    onMainLagi: () -> Unit,
    onKembaliKePeta: () -> Unit,
    totalSoal: Int = 5
) {
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    val skalaBintang = remember { List(5) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        sfxPlayer.playBintang()
        val pegas = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy)
        repeat(bintangDidapat.coerceIn(0, skalaBintang.size)) { index ->
            skalaBintang[index].animateTo(1f, pegas)
        }
    }

    Column(
        Modifier.fillMaxSize().background(LatarKrem).statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PillLabel("LEVEL SELESAI!", icon = Icons.Outlined.Flag)

        Text("Hebat, penjelajah!", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, textAlign = TextAlign.Center)
        Text("Kamu telah menyelesaikan tantangan habitat.", fontSize = 13.sp, color = AbuTua, textAlign = TextAlign.Center)

        // Bintang di atas maskot
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
            skalaBintang.forEachIndexed { index, skala ->
                BintangHasil(index + 1, bintangDidapat, skala.value, 36)
            }
        }
        Text("Bintang spesies baru: $bintangDidapat", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AbuTua)
        Box(Modifier.size(190.dp).clip(CircleShape).background(HijauBenarMuda), contentAlignment = Alignment.Center) {
            MaskotSiBeka(size = 150.dp, melompat = true)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KartuStat("$jawabanBenar / $totalSoal", "Jawaban benar", PutihBersih, HitamLembut, GarisKartu, Modifier.weight(1f))
            KartuStat("$skorTotal", "Skor sesi (poin)", KuningMuda, CoklatTua, KuningMuda, Modifier.weight(1f))
        }

        Spacer(Modifier.height(4.dp))

        TombolUtama("Mainkan lagi", onClick = { sfxPlayer.playClick(); onMainLagi() }, icon = Icons.Outlined.Refresh)
        TombolSekunder("Kembali ke peta", onClick = { sfxPlayer.playClick(); onKembaliKePeta() }, icon = Icons.Outlined.Map)
    }
}

@Composable
private fun BintangHasil(nomor: Int, didapat: Int, skala: Float, ukuran: Int) {
    val aktif = didapat >= nomor
    Icon(
        Icons.Default.Star, "Bintang $nomor",
        tint = if (aktif) KuningBintang else AbuSedang.copy(alpha = 0.5f),
        modifier = Modifier.size(ukuran.dp).scale(if (aktif) skala else 1f)
    )
}

@Composable
private fun KartuStat(
    nilai: String, label: String,
    latar: androidx.compose.ui.graphics.Color, warnaNilai: androidx.compose.ui.graphics.Color,
    garis: androidx.compose.ui.graphics.Color, modifier: Modifier
) {
    KartuSiBeka(modifier, container = latar, borderColor = garis, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(nilai, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = warnaNilai)
            Text(label, fontSize = 11.sp, color = AbuTua)
        }
    }
}