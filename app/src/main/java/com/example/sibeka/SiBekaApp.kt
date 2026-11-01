package com.example.sibeka

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.sibeka.di.AppContainer
import com.example.sibeka.util.AssetImageLoader

class SiBekaApp : Application(), ImageLoaderFactory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
    }

    override fun newImageLoader(): ImageLoader {
        return AssetImageLoader.createImageLoader(this)
    }

    companion object {
        lateinit var instance: SiBekaApp
            private set
    }
}
