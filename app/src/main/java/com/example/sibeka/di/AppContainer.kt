package com.example.sibeka.di

import android.content.Context
import com.example.sibeka.data.db.SiBekaDatabaseHelper
import com.example.sibeka.data.repository.DataStoreManager
import com.example.sibeka.data.repository.SiBekaRepository
import com.example.sibeka.util.SfxPlayer

class AppContainer(private val context: Context) {

    val dbHelper: SiBekaDatabaseHelper by lazy {
        SiBekaDatabaseHelper.getInstance(context)
    }

    val dataStore: DataStoreManager by lazy {
        DataStoreManager(context)
    }

    val sfxPlayer: SfxPlayer by lazy {
        SfxPlayer(context)
    }

    val repository: SiBekaRepository by lazy {
        SiBekaRepository(dbHelper, dataStore)
    }
}
