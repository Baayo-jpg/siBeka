package com.example.sibeka.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sibeka.SiBekaApp
import com.example.sibeka.ui.components.*
import com.example.sibeka.ui.theme.*
import com.example.sibeka.util.GambarAset

private const val NAMA_KELOMPOK = "Kelompok 1"
private const val MATA_KULIAH = "Pemrograman Bergerak"

private data class AnggotaKelompok(val nim: String, val nama: String)

/**
 * Anggota Kelompok 1. Tampilan mengurutkan ulang lewat `sortedBy { it.nim }`,
 * jadi urutan penulisan di sini tidak menentukan urutan di layar.
 */
private val ANGGOTA_KELOMPOK_1: List<AnggotaKelompok> = listOf(
    AnggotaKelompok("246651031", "Muhammad Farhan Judda"),
    AnggotaKelompok("246651033", "Prio Jatmiko"),
    AnggotaKelompok("246651037", "Rizky Indra Pratama"),
    AnggotaKelompok("246651042", "Calvin Harry Paska"),
    AnggotaKelompok("246651051", "Adiel Job Hutagalung"),
)

private data class Kredit(
    val judul: String,
    val isi: String,
    val tautan: String? = null
)

private val DAFTAR_KREDIT = listOf(
    Kredit(
        judul = "iNaturalist",
        isi = "Foto pengamatan flora dan fauna yang menjadi acuan gambar di aplikasi ini " +
            "berasal dari iNaturalist. Setiap foto tetap milik pengamat yang mengunggahnya " +
            "dan mengikuti lisensi pada halaman pengamatan; foto tersebut kami olah menjadi " +
            "ilustrasi agar seragam dengan gaya visual aplikasi.",
        tautan = "https://www.inaturalist.org"
    ),
    Kredit(
        judul = "Status konservasi",
        isi = "Keterangan Risiko Rendah, Rentan, Terancam, dan Kritis pada kartu spesies " +
            "mengacu pada penilaian IUCN Red List.",
        tautan = "https://www.iucnredlist.org"
    ),
    Kredit(
        judul = "Aset tim",
        isi = "Maskot Si Beka, lencana simpul habitat, latar peta, gambar aksesori Toko, dan " +
            "efek suara dibuat oleh tim $NAMA_KELOMPOK. Ilustrasi disusun dengan bantuan Canva " +
            "lalu diolah menjadi WebP dan SVG; bila berkas suara belum tersedia, aplikasi " +
            "memakai nada sintetis Android."
    ),
    Kredit(
        judul = "Huruf, ikon, dan pustaka",
        isi = "Teks memakai huruf Nunito (SIL Open Font License 1.1) dan ikon Material Icons. " +
            "Aplikasi dibangun dengan Jetpack Compose, Navigation Compose, Coil, DataStore, " +
            "dan Gson (Apache License 2.0)."
    )
)

@Composable
fun AboutScreen(onKembali: () -> Unit) {
    val sfxPlayer = SiBekaApp.instance.container.sfxPlayer
    val pembukaTautan = LocalUriHandler.current

    Scaffold(
        containerColor = LatarKrem,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(LatarKrem).statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .clickable { sfxPlayer.playClick(); onKembali() }
                        .padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = HijauTua,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "Tentang Si Beka", fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold, color = HijauTua
                    )
                }
                PillLabel("VERSI 1.2")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                KartuSiBeka(
                    Modifier.fillMaxWidth(),
                    container = HijauBenarMuda,
                    borderColor = HijauBenarMuda,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Maskot Si Beka memakai kacamata hitam, sama seperti di Toko.
                        GambarAset(
                            nama = "pakai_kacamata",
                            cadangan = "😎",
                            modifier = Modifier.size(width = 76.dp, height = 96.dp),
                            ukuranCadangan = 44.sp,
                            folder = "maskot"
                        )
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Si Beka", fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold, color = HijauTua
                            )
                            Text(
                                "Belajar flora dan fauna Nusantara sambil bermain.",
                                fontSize = 12.sp, lineHeight = 16.sp, color = HitamLembut
                            )
                        }
                    }
                }
            }

            item {
                KartuSiBeka(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Text(
                        "Si Beka adalah permainan edukasi pengenalan ekosistem Indonesia. " +
                            "Penjelajah kecil diajak menelusuri lima habitat — hutan hujan tropis, " +
                            "laut, sungai, rawa, dan gunung — lewat tiga mode kuis. Spesies yang " +
                            "ditemukan tersimpan di Buku Koleksi, dan koin dari bermain bisa " +
                            "ditukar aksesori di Toko.",
                        fontSize = 12.5.sp, lineHeight = 18.sp, color = HitamLembut,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            item { JudulBagian("KREDIT GAMBAR & ASET") }

            items(DAFTAR_KREDIT.size) { index ->
                KartuKredit(DAFTAR_KREDIT[index], pembukaTautan, sfxPlayer::playClick)
            }

            item { JudulBagian("TIM PENGEMBANG") }

            item { KartuTimPengembang() }

            item {
                Text(
                    "© 2026 $NAMA_KELOMPOK · Si Beka",
                    fontSize = 11.sp, color = AbuTua,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun KartuKredit(
    kredit: Kredit,
    pembukaTautan: androidx.compose.ui.platform.UriHandler,
    bunyiKlik: () -> Unit
) {
    KartuSiBeka(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                kredit.judul, fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold, color = HijauTua
            )
            Text(kredit.isi, fontSize = 12.sp, lineHeight = 17.sp, color = HitamLembut)
            kredit.tautan?.let { tautan ->
                Text(
                    tautan,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = HijauBenar,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            bunyiKlik()
                            runCatching { pembukaTautan.openUri(tautan) }
                        }
                )
            }
        }
    }
}

@Composable
private fun KartuTimPengembang() {
    val anggota = ANGGOTA_KELOMPOK_1.sortedBy { it.nim }

    KartuSiBeka(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                NAMA_KELOMPOK, fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold, color = HijauTua
            )
            Text(
                "Mata kuliah $MATA_KULIAH",
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HijauBenar
            )

            if (anggota.isEmpty()) {
                Text(
                    "Daftar anggota kelompok belum diisi.",
                    fontSize = 11.sp, color = AbuTua,
                    modifier = Modifier.padding(top = 6.dp)
                )
            } else {
                Text(
                    "${anggota.size} anggota · urut NIM terkecil ke terbesar",
                    fontSize = 11.sp, color = AbuTua, modifier = Modifier.padding(bottom = 4.dp)
                )
                anggota.forEachIndexed { index, orang ->
                    if (index > 0) {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                .height(1.dp).background(GarisKartu)
                        )
                    }
                    Column(Modifier.padding(vertical = 2.dp)) {
                        Text(
                            orang.nama, fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold, color = HijauTua
                        )
                        Text("NIM ${orang.nim}", fontSize = 11.sp, color = AbuTua)
                    }
                }
            }
        }
    }
}

@Composable
private fun JudulBagian(teks: String) {
    Text(
        teks, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AbuTua,
        letterSpacing = 0.6.sp, modifier = Modifier.padding(top = 6.dp)
    )
}
