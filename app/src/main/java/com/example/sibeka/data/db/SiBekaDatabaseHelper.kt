package com.example.sibeka.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStreamReader

data class SeedDataWrapper(
    val habitats: List<HabitatEntity>,
    val objek: List<ObjekEntity>
)

/**
 * Native Android SQLite Database Helper.
 * Sepenuhnya offline, mengimplementasikan skema tabel db_apk.sql secara langsung
 * tanpa ketergantungan pada KSP/Annotation Processor yang rentan bermasalah pada versi AGP.
 */
class SiBekaDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "sibeka_offline.db"
        const val DATABASE_VERSION = 2

        @Volatile
        private var INSTANCE: SiBekaDatabaseHelper? = null

        fun getInstance(context: Context): SiBekaDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SiBekaDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _habitatsFlow = MutableStateFlow<List<HabitatEntity>>(emptyList())
    val habitatsFlow: Flow<List<HabitatEntity>> = _habitatsFlow.asStateFlow()

    private val _koleksiFlow = MutableStateFlow<List<KoleksiEntity>>(emptyList())
    val koleksiFlow: Flow<List<KoleksiEntity>> = _koleksiFlow.asStateFlow()

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Buat Tabel HABITAT
        db.execSQL(
            """
            CREATE TABLE habitat (
                id TEXT PRIMARY KEY,
                namaHabitat TEXT NOT NULL,
                assetFolder TEXT NOT NULL,
                isUnlocked INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        // 2. Buat Tabel OBJEK_FLORA_FAUNA
        db.execSQL(
            """
            CREATE TABLE objek_flora_fauna (
                idObjek TEXT PRIMARY KEY,
                idHabitat TEXT NOT NULL,
                kategori TEXT NOT NULL,
                namaObjek TEXT NOT NULL,
                kataKunci TEXT NOT NULL,
                faktaUnik TEXT NOT NULL,
                gambarUtamaPath TEXT NOT NULL,
                gambarPasanganPath TEXT NOT NULL,
                fileSuaraPath TEXT NOT NULL,
                FOREIGN KEY (idHabitat) REFERENCES habitat (id)
            )
            """.trimIndent()
        )

        // 3. Buat Tabel BUKU_KOLEKSI
        db.execSQL(
            """
            CREATE TABLE buku_koleksi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idObjek TEXT NOT NULL UNIQUE,
                waktuDitemukan INTEGER NOT NULL,
                FOREIGN KEY (idObjek) REFERENCES objek_flora_fauna (idObjek)
            )
            """.trimIndent()
        )

        // Pre-populate data awal dari assets/data/seed_data.json
        populateFromSeedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            updateMainImagePathsFromSeedData(db)
        }
    }

    private fun updateMainImagePathsFromSeedData(db: SQLiteDatabase) {
        val seed = context.assets.open("data/seed_data.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                Gson().fromJson(reader, SeedDataWrapper::class.java)
            }
        } ?: throw IllegalStateException("Data seed tidak boleh kosong.")

        db.beginTransaction()
        try {
            for (objek in seed.objek) {
                val values = ContentValues().apply {
                    put("gambarUtamaPath", objek.gambarUtamaPath)
                }
                db.update(
                    "objek_flora_fauna",
                    values,
                    "idObjek = ?",
                    arrayOf(objek.idObjek)
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun populateFromSeedData(db: SQLiteDatabase) {
        try {
            context.assets.open("data/seed_data.json").use { stream ->
                InputStreamReader(stream).use { reader ->
                    val seed = Gson().fromJson(reader, SeedDataWrapper::class.java)
                    if (seed != null) {
                        db.beginTransaction()
                        try {
                            for (h in seed.habitats) {
                                val values = ContentValues().apply {
                                    put("id", h.id)
                                    put("namaHabitat", h.namaHabitat)
                                    put("assetFolder", h.assetFolder)
                                    put("isUnlocked", if (h.isUnlocked) 1 else 0)
                                }
                                db.insertWithOnConflict("habitat", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                            }

                            for (o in seed.objek) {
                                val values = ContentValues().apply {
                                    put("idObjek", o.idObjek)
                                    put("idHabitat", o.idHabitat)
                                    put("kategori", o.kategori)
                                    put("namaObjek", o.namaObjek)
                                    put("kataKunci", o.kataKunci)
                                    put("faktaUnik", o.faktaUnik)
                                    put("gambarUtamaPath", o.gambarUtamaPath)
                                    put("gambarPasanganPath", o.gambarPasanganPath)
                                    put("fileSuaraPath", o.fileSuaraPath)
                                }
                                db.insertWithOnConflict("objek_flora_fauna", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                            }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshFlows() {
        _habitatsFlow.value = getAllHabitatsSync()
        _koleksiFlow.value = getAllKoleksiSync()
    }

    // ── Operasi Habitat ──────────────────────────────────────────────────────

    fun getAllHabitatsSync(): List<HabitatEntity> {
        val list = mutableListOf<HabitatEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, namaHabitat, assetFolder, isUnlocked FROM habitat ORDER BY id ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    HabitatEntity(
                        id = it.getString(0),
                        namaHabitat = it.getString(1),
                        assetFolder = it.getString(2),
                        isUnlocked = it.getInt(3) == 1
                    )
                )
            }
        }
        return list
    }

    fun getHabitatById(id: String): HabitatEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, namaHabitat, assetFolder, isUnlocked FROM habitat WHERE id = ?", arrayOf(id))
        return cursor.use {
            if (it.moveToFirst()) {
                HabitatEntity(
                    id = it.getString(0),
                    namaHabitat = it.getString(1),
                    assetFolder = it.getString(2),
                    isUnlocked = it.getInt(3) == 1
                )
            } else null
        }
    }

    fun unlockHabitat(id: String) {
        val db = writableDatabase
        val values = ContentValues().apply { put("isUnlocked", 1) }
        db.update("habitat", values, "id = ?", arrayOf(id))
        _habitatsFlow.value = getAllHabitatsSync()
    }

    // ── Operasi Objek Flora Fauna ────────────────────────────────────────────

    fun getRandomByHabitat(habitatId: String, count: Int): List<ObjekEntity> {
        val list = mutableListOf<ObjekEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT idObjek, idHabitat, kategori, namaObjek, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath, fileSuaraPath FROM objek_flora_fauna WHERE idHabitat = ? ORDER BY RANDOM() LIMIT ?",
            arrayOf(habitatId, count.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToObjek(it))
            }
        }
        return list
    }

    fun getDecoys(habitatId: String, excludeIds: List<String>, count: Int): List<ObjekEntity> {
        val list = mutableListOf<ObjekEntity>()
        val db = readableDatabase
        val placeholders = excludeIds.joinToString(",") { "?" }
        val args = (listOf(habitatId) + excludeIds + listOf(count.toString())).toTypedArray()
        val query = if (excludeIds.isEmpty()) {
            "SELECT idObjek, idHabitat, kategori, namaObjek, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath, fileSuaraPath FROM objek_flora_fauna WHERE idHabitat = ? ORDER BY RANDOM() LIMIT ?"
        } else {
            "SELECT idObjek, idHabitat, kategori, namaObjek, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath, fileSuaraPath FROM objek_flora_fauna WHERE idHabitat = ? AND idObjek NOT IN ($placeholders) ORDER BY RANDOM() LIMIT ?"
        }
        val cursor = db.rawQuery(query, args)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToObjek(it))
            }
        }
        return list
    }

    fun getObjekById(id: String): ObjekEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT idObjek, idHabitat, kategori, namaObjek, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath, fileSuaraPath FROM objek_flora_fauna WHERE idObjek = ?",
            arrayOf(id)
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToObjek(it) else null
        }
    }

    private fun cursorToObjek(cursor: Cursor): ObjekEntity {
        return ObjekEntity(
            idObjek = cursor.getString(0),
            idHabitat = cursor.getString(1),
            kategori = cursor.getString(2),
            namaObjek = cursor.getString(3),
            kataKunci = cursor.getString(4),
            faktaUnik = cursor.getString(5),
            gambarUtamaPath = cursor.getString(6),
            gambarPasanganPath = cursor.getString(7),
            fileSuaraPath = cursor.getString(8)
        )
    }

    // ── Operasi Buku Koleksi ─────────────────────────────────────────────────

    fun getAllKoleksiSync(): List<KoleksiEntity> {
        val list = mutableListOf<KoleksiEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, idObjek, waktuDitemukan FROM buku_koleksi ORDER BY waktuDitemukan DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    KoleksiEntity(
                        id = it.getLong(0),
                        idObjek = it.getString(1),
                        waktuDitemukan = it.getLong(2)
                    )
                )
            }
        }
        return list
    }

    fun isCollected(idObjek: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM buku_koleksi WHERE idObjek = ?", arrayOf(idObjek))
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) > 0 else false
        }
    }

    fun catatPenemuan(idObjek: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idObjek", idObjek)
            put("waktuDitemukan", System.currentTimeMillis())
        }
        db.insertWithOnConflict("buku_koleksi", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        _koleksiFlow.value = getAllKoleksiSync()
    }
}
