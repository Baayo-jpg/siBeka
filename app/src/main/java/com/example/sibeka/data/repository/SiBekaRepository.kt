package com.example.sibeka.data.repository

import com.example.sibeka.data.db.HabitatEntity
import com.example.sibeka.data.db.KoleksiEntity
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.db.SiBekaDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class SiBekaRepository(
    private val dbHelper: SiBekaDatabaseHelper,
    val dataStore: DataStoreManager
) {
    private val habitatsState = MutableStateFlow<List<HabitatEntity>?>(null)
    private val koleksiState = MutableStateFlow<List<KoleksiEntity>?>(null)

    private val habitatsFlow: Flow<List<HabitatEntity>> = habitatsState
        .filterNotNull()
        .onStart {
            if (habitatsState.value == null) {
                habitatsState.value = withContext(Dispatchers.IO) { dbHelper.getAllHabitatsSync() }
            }
        }

    private val koleksiFlow: Flow<List<KoleksiEntity>> = koleksiState
        .filterNotNull()
        .onStart {
            if (koleksiState.value == null) {
                koleksiState.value = withContext(Dispatchers.IO) { dbHelper.getAllKoleksiSync() }
            }
        }

    fun getAllHabitats(): Flow<List<HabitatEntity>> = habitatsFlow

    fun getAllKoleksi(): Flow<List<KoleksiEntity>> = koleksiFlow

    private suspend fun refreshKoleksi() {
        koleksiState.value = withContext(Dispatchers.IO) { dbHelper.getAllKoleksiSync() }
    }

    suspend fun getHabitatById(id: String): HabitatEntity? =
        withContext(Dispatchers.IO) { dbHelper.getHabitatById(id) }

    suspend fun getSoalByHabitat(habitatId: String, count: Int = 5): List<ObjekEntity> =
        withContext(Dispatchers.IO) { dbHelper.getRandomByHabitat(habitatId, count) }

    suspend fun getAllObjek(): List<ObjekEntity> =
        withContext(Dispatchers.IO) { dbHelper.getAllObjekSync() }

    suspend fun getDecoyAnswers(habitatId: String, excludeIds: List<String>, count: Int = 2): List<ObjekEntity> =
        withContext(Dispatchers.IO) { dbHelper.getDecoys(habitatId, excludeIds, count) }

    suspend fun getObjekById(id: String): ObjekEntity? =
        withContext(Dispatchers.IO) { dbHelper.getObjekById(id) }

    suspend fun catatPenemuan(idObjek: String): Boolean {
        val ditemukanBaru = withContext(Dispatchers.IO) { dbHelper.catatPenemuan(idObjek) }
        if (ditemukanBaru) refreshKoleksi()
        return ditemukanBaru
    }

    suspend fun isCollected(idObjek: String): Boolean =
        withContext(Dispatchers.IO) { dbHelper.isCollected(idObjek) }
}
