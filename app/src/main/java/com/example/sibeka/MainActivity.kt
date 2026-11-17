package com.example.sibeka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.about.AboutScreen
import com.example.sibeka.ui.koleksi.FILTER_SEMUA_HABITAT
import com.example.sibeka.ui.koleksi.KoleksiScreen
import com.example.sibeka.ui.kuis.KuisScreen
import com.example.sibeka.ui.level.LevelSelectScreen
import com.example.sibeka.ui.peta.PetaScreen
import com.example.sibeka.ui.splash.SambutanScreen
import com.example.sibeka.ui.splash.SplashScreen
import com.example.sibeka.ui.theme.SiBekaTheme
import com.example.sibeka.ui.toko.TokoScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiBekaTheme {
                val navController = rememberNavController()
                val dataStore = SiBekaApp.instance.container.dataStore
                val coroutineScope = rememberCoroutineScope()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    coroutineScope.launch {
                                        val nama = dataStore.namaPemain.first()
                                        val destination = if (nama.isBlank()) "sambutan" else "peta"
                                        navController.navigate(destination) {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable("sambutan") {
                            SambutanScreen(
                                onMulaiPetualangan = {
                                    navController.navigate("peta") {
                                        popUpTo("sambutan") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("peta") {
                            PetaScreen(
                                onPilihHabitat = { habitatId ->
                                    navController.navigate("level/$habitatId")
                                },
                                onMulaiKuis = { habitatId, mode ->
                                    navController.navigate("kuis/$habitatId/${mode.name}")
                                },
                                onBukaKoleksi = { habitatId -> navController.keTab(ruteKoleksi(habitatId)) },
                                onBukaToko = { navController.keTab("toko") }
                            )
                        }

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

                        composable(
                            route = "kuis/{habitatId}/{modeKuis}",
                            arguments = listOf(
                                navArgument("habitatId") { type = NavType.StringType },
                                navArgument("modeKuis") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val habitatId = backStackEntry.arguments?.getString("habitatId") ?: "HH"
                            val modeStr = backStackEntry.arguments?.getString("modeKuis")
                                ?: ModeKuis.SUSUN_HURUF.name
                            val modeKuis = ModeKuis.entries.firstOrNull { it.name == modeStr }
                                ?: ModeKuis.SUSUN_HURUF

                            KuisScreen(
                                habitatId = habitatId,
                                modeKuis = modeKuis,
                                onKeluarKuis = { navController.popBackStack() },
                                onPilihModeLain = {
                                    navController.navigate("level/$habitatId") {
                                        popUpTo("peta") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(
                            route = "koleksi?habitatId={habitatId}",
                            arguments = listOf(
                                navArgument("habitatId") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { backStackEntry ->
                            val filterHabitat = backStackEntry.arguments?.getString("habitatId").orEmpty()
                            KoleksiScreen(
                                filterAwal = filterHabitat.ifBlank { FILTER_SEMUA_HABITAT },
                                onBukaPeta = { navController.keTab("peta") },
                                onBukaToko = { navController.keTab("toko") }
                            )
                        }

                        composable("toko") {
                            TokoScreen(
                                onBukaPeta = { navController.keTab("peta") },
                                onBukaKoleksi = { navController.keTab("koleksi") },
                                onBukaTentang = { navController.navigate("about") }
                            )
                        }

                        composable("about") {
                            AboutScreen(onKembali = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        // onTerminate tidak pernah dipanggil di perangkat, jadi SoundPool dibebaskan di sini.
        if (isFinishing) SiBekaApp.instance.container.sfxPlayer.release()
        super.onDestroy()
    }
}

/** Rute koleksi, terfilter ke satu habitat bila id diberikan. */
private fun ruteKoleksi(habitatId: String?): String =
    if (habitatId.isNullOrBlank()) "koleksi" else "koleksi?habitatId=$habitatId"

/** Pindah antar tab bawah (Peta / Koleksi / Toko) tanpa menumpuk back stack. */
private fun NavController.keTab(route: String) {
    navigate(route) {
        popUpTo("peta") { inclusive = false }
        launchSingleTop = true
    }
}
