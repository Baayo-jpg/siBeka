package com.example.sibeka.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sibeka.SiBekaApp
import com.example.sibeka.data.model.AturanPermainan
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.data.repository.DataStoreManager
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.kuis.URUTAN_MODE
import com.example.sibeka.ui.kuis.info
import com.example.sibeka.ui.theme.*

@Composable
fun LevelSelectScreen(
    habitatId: String,
    onKembali: () -> Unit,
    onMulaiModeKuis: (habitatId: String, mode: ModeKuis) -> Unit
) {
    val repository = SiBekaApp.instance.container.repository
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val totalKoin by repository.dataStore.totalKoin
        .collectAsStateWithLifecycle(initialValue = AturanPermainan.KOIN_AWAL)
    val totalBintang by repository.dataStore.totalBintang
        .collectAsStateWithLifecycle(initialValue = 0)
    val skorTerbaik by repository.dataStore.skorTerbaik
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    var namaHabitat by remember { mutableStateOf("Habitat") }

    LaunchedEffect(habitatId) {
        namaHabitat = repository.getHabitatById(habitatId)?.namaHabitat ?: "Habitat"
    }

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TombolIkonBulat(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", { sfxPlayer.playClick(); onKembali() })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipBintang(totalBintang)
                    ChipKoin(totalKoin)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(namaHabitat, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                    Text("Dengar, lihat, dan temukan keajaibannya.", fontSize = 13.sp, color = AbuTua)
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "PILIH TANTANGAN KUIS", fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold, color = AbuTua, letterSpacing = 0.6.sp
                    )
                    Text(
                        "${URUTAN_MODE.size} MODE", fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold, color = AbuTua, letterSpacing = 0.6.sp
                    )
                }
            }
            items(URUTAN_MODE.size, key = { URUTAN_MODE[it].name }) { index ->
                val mode = URUTAN_MODE[index]
                val info = mode.info
                val terbaik = skorTerbaik[DataStoreManager.kunciSkor(habitatId, mode)]

                KartuSiBeka(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { sfxPlayer.playClick(); onMulaiModeKuis(habitatId, mode) }
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Ikon mode di atas bidang warna tipis, bukan emoji.
                        Box(
                            Modifier.size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(info.warna.copy(alpha = 0.13f))
                                .border(1.dp, info.warna.copy(alpha = 0.22f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                info.ikon, null, tint = info.warna,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                info.judul, fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold, color = HijauTua
                            )
                            Text(
                                info.deskripsi, fontSize = 12.sp,
                                lineHeight = 16.sp, color = AbuTua
                            )
                            // Rekor per mode: pemain tahu mana yang sudah dimainkan.
                            val adaRekor = terbaik != null
                            val teksRekor = when {
                                !adaRekor -> "BELUM DIMAINKAN"
                                terbaik!! > 0 -> "TERBAIK $terbaik POIN"
                                else -> "SUDAH DIMAINKAN"
                            }
                            PillLabel(
                                teksRekor,
                                container = if (adaRekor) info.warna.copy(alpha = 0.12f) else GarisKartu.copy(alpha = 0.35f),
                                contentColor = if (adaRekor) info.warna else AbuTua
                            )
                        }

                        Box(
                            Modifier.size(34.dp).clip(CircleShape).background(HijauTua),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow, "Mulai ${info.judul}", tint = PutihBersih,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Sekali saja, bukan diulang di tiap kartu.
                Text(
                    "Setiap sesi berisi ${AturanPermainan.JUMLAH_SOAL_PER_SESI} soal. " +
                        "Koin dan bintang didapat dari spesies baru.",
                    fontSize = 11.sp, lineHeight = 15.sp, color = AbuTua,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }
    }
}