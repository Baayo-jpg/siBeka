package com.example.sibeka

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.sibeka.di.AppContainer
import com.example.sibeka.util.AssetImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SiBekaApp : Application(), ImageLoaderFactory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            container.sfxPlayer.setMuted(container.dataStore.sfxMuted.first())
        }
    }

    override fun onTerminate() {
        container.sfxPlayer.release()
        super.onTerminate()
    }

    override fun newImageLoader(): ImageLoader {
        return AssetImageLoader.createImageLoader(this)
    }

    companion object {
        lateinit var instance: SiBekaApp
            private set
    }
}
