package com.example.sibeka.data.repository

import com.example.sibeka.data.db.HabitatEntity
import com.example.sibeka.data.db.KoleksiEntity
import com.example.sibeka.data.db.ObjekEntity
import com.example.sibeka.data.db.SiBekaDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SiBekaRepository(
    private val dbHelper: SiBekaDatabaseHelper,
    val dataStore: DataStoreManager
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            dbHelper.refreshFlows()
        }
    }

    fun getAllHabitats(): Flow<List<HabitatEntity>> = dbHelper.habitatsFlow

    fun unlockHabitat(habitatId: String) {
        dbHelper.unlockHabitat(habitatId)
    }

    fun getHabitatById(id: String): HabitatEntity? = dbHelper.getHabitatById(id)

    fun getSoalByHabitat(habitatId: String, count: Int = 5): List<ObjekEntity> {
        return dbHelper.getRandomByHabitat(habitatId, count)
    }

    fun getDecoyAnswers(habitatId: String, excludeIds: List<String>, count: Int = 2): List<ObjekEntity> {
        return dbHelper.getDecoys(habitatId, excludeIds, count)
    }

    fun getObjekById(id: String): ObjekEntity? = dbHelper.getObjekById(id)

    fun getAllKoleksi(): Flow<List<KoleksiEntity>> = dbHelper.koleksiFlow

    fun catatPenemuan(idObjek: String) {
        dbHelper.catatPenemuan(idObjek)
    }

    fun isCollected(idObjek: String): Boolean {
        return dbHelper.isCollected(idObjek)
    }

    fun getTotalKoleksiCount(): Flow<Int> = dbHelper.koleksiFlow.map { it.size }
}
