package com.example.sibeka.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SambutanScreen(
    onMulaiPetualangan: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dataStore = SiBekaApp.instance.container.dataStore
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer

    var namaInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(LatarKrem).statusBarsPadding().navigationBarsPadding()
            .imePadding().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.Explore, null, tint = HijauTua, modifier = Modifier.size(20.dp))
                Text("JELAJAH ALAM", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua, letterSpacing = 0.5.sp)
            }
            Text("Untuk penjelajah cilik", fontSize = 11.sp, color = AbuTua)
        }

        // Hero Si Beka
        Box(
            Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(28.dp)).background(HijauPucat),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.clip(RoundedCornerShape(20.dp)).background(PutihBersih).padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Halo! Aku Si Beka 👋", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
                }
                MaskotSiBeka(size = 150.dp, melompat = true)
            }
            PillLabel(
                "PETUALANGAN DIMULAI!",
                Modifier.align(Alignment.BottomStart).padding(14.dp),
                container = KuningBintang, contentColor = CoklatTua, icon = Icons.Outlined.Explore
            )
        }

        Text(
            "Alam Indonesia,\nsejuta cerita!",
            fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.ExtraBold,
            color = HijauTua, textAlign = TextAlign.Center
        )
        Text(
            "Kenali flora dan fauna, pecahkan kuis, lalu isi buku petualanganmu.",
            fontSize = 13.sp, color = AbuTua, textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = namaInput,
            onValueChange = {
                namaInput = it
                if (it.isNotBlank()) errorMessage = ""
            },
            label = { Text("Nama kamu") },
            placeholder = { Text("contoh: Budi / Siti") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HijauUtama,
                unfocusedBorderColor = GarisKartu,
                focusedContainerColor = PutihBersih,
                unfocusedContainerColor = PutihBersih
            )
        )

        if (errorMessage.isNotBlank()) {
            Text(errorMessage, fontSize = 12.sp, color = MerahSalah)
        }

        TombolUtama(
            teks = "Mulai menjelajah",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = {
                if (namaInput.isBlank()) {
                    errorMessage = "Tulis namamu dulu ya! 😊"
                    sfxPlayer.playSalah()
                } else {
                    sfxPlayer.playClick()
                    coroutineScope.launch {
                        dataStore.simpanNamaPemain(namaInput.trim())
                        onMulaiPetualangan()
                    }
                }
            }
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FiturKecil(Icons.Outlined.Eco, "5 habitat")
            FiturKecil(Icons.Outlined.Extension, "3 jenis kuis")
            FiturKecil(Icons.Default.Star, "Banyak hadiah")
        }
        Text("Belajar sedikit, takjub banyak. Ayo berangkat!", fontSize = 11.sp, color = AbuTua)
    }
}

@Composable
private fun FiturKecil(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = HijauTua, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = HijauTua)
    }
}