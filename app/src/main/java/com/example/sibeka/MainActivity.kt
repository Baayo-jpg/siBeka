package com.example.sibeka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.koleksi.KoleksiScreen
import com.example.sibeka.ui.kuis.KuisScreen
import com.example.sibeka.ui.level.LevelSelectScreen
import com.example.sibeka.ui.peta.PetaScreen
import com.example.sibeka.ui.splash.SambutanScreen
import com.example.sibeka.ui.splash.SplashScreen
import com.example.sibeka.ui.theme.SiBekaTheme
import com.example.sibeka.ui.toko.TokoScreen
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiBekaTheme {
                val navController = rememberNavController()
                val dataStore = SiBekaApp.instance.container.dataStore

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Splash Screen
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                // Cek apakah nama sudah pernah disimpan
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).run {
                                    val nama = dataStore.namaPemain
                                    navController.navigate("peta") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // 2. Sambutan / Input Nama
                    composable("sambutan") {
                        SambutanScreen(
                            onMulaiPetualangan = {
                                navController.navigate("peta") {
                                    popUpTo("sambutan") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Peta 5 Habitat
                    composable("peta") {
                        PetaScreen(
                            onPilihHabitat = { habitatId ->
                                navController.navigate("level/$habitatId")
                            },
                            onBukaKoleksi = {
                                navController.navigate("koleksi")
                            },
                            onBukaToko = {
                                navController.navigate("toko")
                            }
                        )
                    }

                    // 4. Jalur Level / Pilihan Mode Kuis
                    composable(
                        route = "level/{habitatId}",
                        arguments = listOf(navArgument("habitatId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val habitatId = backStackEntry.arguments?.getString("habitatId") ?: "HH"
                        LevelSelectScreen(
                            habitatId = habitatId,
                            onKembali = { navController.popBackStack() },
                            onMulaiModeKuis = { habId, mode ->
                                navController.navigate("kuis/$habId/${mode.name}")
                            }
                        )
                    }

                    // 5. Layar Kuis
                    composable(
                        route = "kuis/{habitatId}/{modeKuis}",
                        arguments = listOf(
                            navArgument("habitatId") { type = NavType.StringType },
                            navArgument("modeKuis") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val habitatId = backStackEntry.arguments?.getString("habitatId") ?: "HH"
                        val modeStr = backStackEntry.arguments?.getString("modeKuis") ?: ModeKuis.SUSUN_HURUF.name
                        val modeKuis = try {
                            ModeKuis.valueOf(modeStr)
                        } catch (e: Exception) {
                            ModeKuis.SUSUN_HURUF
                        }

                        KuisScreen(
                            habitatId = habitatId,
                            modeKuis = modeKuis,
                            onKeluarKuis = { navController.popBackStack() }
                        )
                    }

                    // 6. Buku Koleksi
                    composable("koleksi") {
                        KoleksiScreen(
                            onKembali = { navController.popBackStack() }
                        )
                    }

                    // 7. Toko Maskot
                    composable("toko") {
                        TokoScreen(
                            onKembali = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
