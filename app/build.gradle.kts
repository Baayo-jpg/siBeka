plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sibeka"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sibeka"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    // Paksa portrait mode — kunci orientasi layar
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM — sinkronisasi versi semua lib Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation Compose — navigasi antar layar
    implementation(libs.navigation.compose)

    // Coil — load gambar dari assets/ dengan memory management optimal
    implementation(libs.coil.compose)

    // DataStore — simpan progres & preferensi ringan (nama pemain, mute state)
    implementation(libs.datastore.preferences)

    // Gson — parse seed_data.json saat pre-populate database
    implementation(libs.gson)

    // Splash Screen API
    implementation(libs.splashscreen)

    // Rive habitat map animation
    implementation(libs.rive.android)
    implementation(libs.androidx.startup.runtime)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
