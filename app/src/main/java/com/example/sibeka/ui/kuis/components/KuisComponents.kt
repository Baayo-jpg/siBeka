package com.example.sibeka.ui.kuis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.data.model.StatusJawaban
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*

/** Status visual satu pilihan jawaban. */
enum class StatusOpsi { NETRAL, BENAR, SALAH }

fun warnaLatarOpsi(s: StatusOpsi): Color = when (s) {
    StatusOpsi.BENAR -> HijauBenarMuda
    StatusOpsi.SALAH -> MerahMuda
    StatusOpsi.NETRAL -> PutihBersih
}

fun warnaGarisOpsi(s: StatusOpsi): Color = when (s) {
    StatusOpsi.BENAR -> HijauBenar
    StatusOpsi.SALAH -> MerahSalah
    StatusOpsi.NETRAL -> GarisKartu
}

@Composable
fun KuisTopBar(
    namaHabitat: String,
    totalKoin: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TombolIkonBulat(Icons.Default.Close, "Tutup kuis", onCloseClick)
        Text(namaHabitat.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = AbuTua, letterSpacing = 0.6.sp)
        ChipKoin(totalKoin)
    }
}

@Composable
fun ProgresSoalBar(
    currentStep: Int,
    totalSteps: Int,
    bintangTerkumpul: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (i in 1..totalSteps) {
                Box(
                    Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                        .background(if (i <= currentStep) HijauBenar else TrackProgres)
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("SOAL $currentStep DARI $totalSteps", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AbuTua, letterSpacing = 0.5.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Default.Star, null, tint = KuningBintang, modifier = Modifier.size(13.dp))
                Text("$bintangTerkumpul bintang terkumpul", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AbuTua)
            }
        }
    }
}

@Composable
fun KuisHeader(judul: String, instruksi: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(judul, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
        Text(instruksi, fontSize = 14.sp, color = HitamLembut)
    }
}

/** Satu baris pilihan jawaban berhuruf (A / B / C). */
@Composable
fun OpsiBerhuruf(
    huruf: String,
    teks: String,
    status: StatusOpsi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val netral = status == StatusOpsi.NETRAL
    KartuSiBeka(
        modifier = modifier.fillMaxWidth(),
        container = warnaLatarOpsi(status),
        borderColor = warnaGarisOpsi(status),
        borderWidth = if (netral) 1.dp else 1.5.dp,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                    .background(when (status) { StatusOpsi.BENAR -> HijauBenar; StatusOpsi.SALAH -> MerahSalah; else -> MaskotLatar }),
                contentAlignment = Alignment.Center
            ) {
                Text(huruf, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (netral) HijauTua else PutihBersih)
            }
            Text(teks, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = HitamLembut)
            when (status) {
                StatusOpsi.BENAR -> Icon(Icons.Outlined.CheckCircle, null, tint = HijauBenar, modifier = Modifier.size(22.dp))
                StatusOpsi.SALAH -> Icon(Icons.Outlined.Cancel, null, tint = MerahSalah, modifier = Modifier.size(22.dp))
                else -> {}
            }
        }
    }
}

/** Kartu pilihan berisi gambar (dipakai mode Pasangkan). */
@Composable
fun KartuPilihan(
    status: StatusOpsi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) = KartuSiBeka(
    modifier = modifier,
    container = warnaLatarOpsi(status),
    borderColor = warnaGarisOpsi(status),
    borderWidth = if (status == StatusOpsi.NETRAL) 1.dp else 1.5.dp,
    shape = shape,
    onClick = onClick,
    content = content
)

/** Kartu hasil jawaban (Benar / Salah) + fakta dari Si Beka. */
@Composable
fun KartuUmpanBalik(
    status: StatusJawaban,
    jawabanmu: String?,
    jawabanBenar: String?,
    faktaUnik: String,
    modifier: Modifier = Modifier
) {
    val benar = status == StatusJawaban.BENAR
    KartuSiBeka(
        modifier = modifier.fillMaxWidth(),
        container = if (benar) HijauBenarMuda else MerahMuda,
        borderColor = if (benar) HijauBenar else MerahSalah,
        borderWidth = 1.5.dp,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val warnaJudul = if (benar) HijauTua else MerahSalah
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(if (benar) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel, null, tint = warnaJudul, modifier = Modifier.size(20.dp))
                Text(
                    if (benar) "BENAR! Hebat, penjelajah!" else "SALAH · Semangat, ya!",
                    fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = warnaJudul
                )
            }
            if (jawabanmu != null) {
                Text("Jawabanmu: $jawabanmu ${if (benar) "✓" else "✗"}", fontSize = 12.sp, color = HitamLembut)
            }
            if (!benar && jawabanBenar != null) {
                Text("Jawaban yang benar: $jawabanBenar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HijauTua)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                MaskotSiBeka(size = 44.dp, melompat = benar)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("SI BEKA", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = HijauBenar, letterSpacing = 0.6.sp)
                    Text(faktaUnik, fontSize = 12.5.sp, lineHeight = 17.sp, color = HitamLembut)
                }
            }
            if (benar) {
                Text("★ +1 bintang · Disimpan saat level selesai", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HijauBenar)
            }
        }
    }
}

/** Tombol di bawah layar kuis: hint (nonaktif) sebelum menjawab, "Lanjut" sesudahnya. */
@Composable
fun KuisAksiBawah(
    sudahMenjawab: Boolean,
    teksHint: String,
    teksLanjut: String,
    onLanjutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxWidth().background(LatarKrem).navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        if (sudahMenjawab) {
            TombolUtama(teksLanjut, onLanjutClick, icon = Icons.AutoMirrored.Filled.ArrowForward)
        } else {
            TombolSekunder(teksHint, onClick = {}, enabled = false)
        }
    }
}