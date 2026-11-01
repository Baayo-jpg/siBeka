package com.example.sibeka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Skema warna Si Beka — tema terang statis (tidak pakai dynamic color).
 * Dynamic color dinonaktifkan agar tampilan konsisten di semua perangkat
 * dan tidak bertabrakan dengan palet alam yang sudah dirancang.
 */
private val SiBekaColorScheme = lightColorScheme(
    primary          = HijauUtama,
    onPrimary        = PutihBersih,
    primaryContainer = HijauPucat,
    onPrimaryContainer = HijauTua,

    secondary        = CoklatKayu,
    onSecondary      = PutihBersih,
    secondaryContainer = OranjeMuda,
    onSecondaryContainer = CoklatTua,

    tertiary         = BiruLaut,
    onTertiary       = PutihBersih,
    tertiaryContainer = BiruPucat,
    onTertiaryContainer = BiruLaut,

    background       = LatarKrem,
    onBackground     = HitamLembut,

    surface          = PutihBersih,
    onSurface        = HitamLembut,
    surfaceVariant   = HijauPucat,
    onSurfaceVariant = AbuTua,

    error            = MerahSalah,
    onError          = PutihBersih,
    errorContainer   = MerahMuda,
    onErrorContainer = MerahSalah,

    outline          = AbuSedang,
    outlineVariant   = GarisKartu
)

@Composable
fun SiBekaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SiBekaColorScheme,
        typography  = SiBekaTypography,
        content     = content
    )
}
