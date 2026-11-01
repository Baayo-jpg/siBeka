package com.example.sibeka.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografi Si Beka — menggunakan Nunito untuk kesan bulat dan ramah anak.
 * Fallback ke system default jika font belum ditambahkan.
 *
 * Untuk menambahkan Nunito:
 * 1. Download dari https://fonts.google.com/specimen/Nunito
 * 2. Letakkan di res/font/ (nunito_regular.ttf, nunito_bold.ttf, dll.)
 * 3. Uncomment baris FontFamily di bawah.
 */

// val NunitoFamily = FontFamily(
//     Font(R.font.nunito_regular, FontWeight.Normal),
//     Font(R.font.nunito_semibold, FontWeight.SemiBold),
//     Font(R.font.nunito_bold, FontWeight.Bold),
//     Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
// )

val SiBekaTypography = Typography(
    // Judul besar — nama habitat, hasil kuis
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp
    ),
    // Judul sedang — nama layar, judul kuis
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Judul kecil — nama habitat di card
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // Teks soal utama
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // Teks instruksi kuis
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    // Label tombol, chip
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Fakta unik, konten ensiklopedia
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Teks keterangan, metadata
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Caption kecil — skor, waktu
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
