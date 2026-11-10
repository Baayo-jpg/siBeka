package com.example.sibeka.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
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
        const val DATABASE_VERSION = 7
        private const val TAG = "SiBekaDatabase"

        @Volatile
        private var INSTANCE: SiBekaDatabaseHelper? = null

        fun getInstance(context: Context): SiBekaDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SiBekaDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Buat Tabel HABITAT
        db.execSQL(
            """
            CREATE TABLE habitat (
                id TEXT PRIMARY KEY,
                namaHabitat TEXT NOT NULL,
                assetFolder TEXT NOT NULL
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
                namaLatin TEXT NOT NULL,
                namaInternasional TEXT NOT NULL,
                ciriCiri TEXT NOT NULL,
                sebaran TEXT NOT NULL,
                statusKonservasi TEXT NOT NULL,
                kataKunci TEXT NOT NULL,
                faktaUnik TEXT NOT NULL,
                gambarUtamaPath TEXT NOT NULL,
                gambarPasanganPath TEXT NOT NULL,
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
        if (oldVersion < 3) {
            updateImagePathsFromSeedData(db)
        }
        if (oldVersion < 4) {
            migrateToVersionFour(db)
        }
        if (oldVersion < 5) {
            migrateToVersionFive(db)
        }
        if (oldVersion < 6) {
            migrateToVersionSix(db)
        }
        if (oldVersion < 7) {
            migrateToVersionSeven(db)
        }
    }

    private fun updateImagePathsFromSeedData(db: SQLiteDatabase) {
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
                    put("gambarPasanganPath", objek.gambarPasanganPath)
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
                                }
                                db.insertWithOnConflict("habitat", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                            }

                            for (o in seed.objek) {
                                val values = ContentValues().apply {
                                    put("idObjek", o.idObjek)
                                    put("idHabitat", o.idHabitat)
                                    put("kategori", o.kategori)
                                    put("namaObjek", o.namaObjek)
                                    put("namaLatin", o.namaLatin)
                                    put("namaInternasional", o.namaInternasional)
                                    put("ciriCiri", o.ciriCiri)
                                    put("sebaran", o.sebaran)
                                    put("statusKonservasi", o.statusKonservasi)
                                    put("kataKunci", o.kataKunci)
                                    put("faktaUnik", o.faktaUnik)
                                    put("gambarUtamaPath", o.gambarUtamaPath)
                                    put("gambarPasanganPath", o.gambarPasanganPath)
                                }
                                db.insertWithOnConflict("objek_flora_fauna", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                            }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    } else {
                        Log.e(TAG, "Data seed dari assets/data/seed_data.json bernilai null")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memuat data seed dari assets/data/seed_data.json", e)
        }
    }

    private fun migrateToVersionFour(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.delete(
                "buku_koleksi",
                "idObjek IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                arrayOf(
                    "LT-F-001", "LT-F-002", "LT-F-003", "LT-F-004", "LT-F-005",
                    "LT-F-007", "LT-F-008", "LT-F-009", "LT-F-010", "LT-F-011", "LT-F-013"
                )
            )
            db.delete(
                "objek_flora_fauna",
                "idObjek IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                arrayOf(
                    "LT-F-001", "LT-F-002", "LT-F-003", "LT-F-004", "LT-F-005",
                    "LT-F-007", "LT-F-008", "LT-F-009", "LT-F-010", "LT-F-011", "LT-F-013"
                )
            )
            db.execSQL("UPDATE objek_flora_fauna SET kataKunci = 'KUCINGMERAH' WHERE idObjek = 'HH-F-019'")
            db.execSQL("UPDATE objek_flora_fauna SET kataKunci = 'KUCINGTANDANG' WHERE idObjek = 'RW-F-037'")
            db.execSQL("UPDATE objek_flora_fauna SET kataKunci = 'KATAKRAWA' WHERE idObjek = 'RW-F-035'")
            db.execSQL("UPDATE objek_flora_fauna SET kataKunci = 'KATAKPOHON' WHERE idObjek = 'GN-F-065'")
            db.execSQL("UPDATE objek_flora_fauna SET kataKunci = 'ELANGULAR' WHERE idObjek = 'GN-F-064'")

            db.execSQL(
                """
                CREATE TABLE habitat_new (
                    id TEXT PRIMARY KEY,
                    namaHabitat TEXT NOT NULL,
                    assetFolder TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "INSERT INTO habitat_new (id, namaHabitat, assetFolder) " +
                    "SELECT id, namaHabitat, assetFolder FROM habitat"
            )
            db.execSQL("DROP TABLE habitat")
            db.execSQL("ALTER TABLE habitat_new RENAME TO habitat")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun migrateToVersionFive(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL(
                """
                CREATE TABLE objek_flora_fauna_new (
                    idObjek TEXT PRIMARY KEY,
                    idHabitat TEXT NOT NULL,
                    kategori TEXT NOT NULL,
                    namaObjek TEXT NOT NULL,
                    kataKunci TEXT NOT NULL,
                    faktaUnik TEXT NOT NULL,
                    gambarUtamaPath TEXT NOT NULL,
                    gambarPasanganPath TEXT NOT NULL,
                    FOREIGN KEY (idHabitat) REFERENCES habitat (id)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO objek_flora_fauna_new (
                    idObjek, idHabitat, kategori, namaObjek, kataKunci,
                    faktaUnik, gambarUtamaPath, gambarPasanganPath
                )
                SELECT idObjek, idHabitat, kategori, namaObjek, kataKunci,
                    faktaUnik, gambarUtamaPath, gambarPasanganPath
                FROM objek_flora_fauna
                """.trimIndent()
            )
            db.execSQL("DROP TABLE objek_flora_fauna")
            db.execSQL("ALTER TABLE objek_flora_fauna_new RENAME TO objek_flora_fauna")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun migrateToVersionSix(db: SQLiteDatabase) {
        val seed = context.assets.open("data/seed_data.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                Gson().fromJson(reader, SeedDataWrapper::class.java)
            }
        } ?: throw IllegalStateException("Data seed tidak boleh kosong.")

        db.beginTransaction()
        try {
            db.execSQL("ALTER TABLE objek_flora_fauna ADD COLUMN namaLatin TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE objek_flora_fauna ADD COLUMN namaInternasional TEXT NOT NULL DEFAULT ''")
            for (objek in seed.objek) {
                val values = ContentValues().apply {
                    put("namaObjek", objek.namaObjek)
                    put("namaLatin", objek.namaLatin)
                    put("namaInternasional", objek.namaInternasional)
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

    private fun migrateToVersionSeven(db: SQLiteDatabase) {
        val seed = context.assets.open("data/seed_data.json").use { stream ->
            InputStreamReader(stream).use { reader ->
                Gson().fromJson(reader, SeedDataWrapper::class.java)
            }
        } ?: throw IllegalStateException("Data seed tidak boleh kosong.")

        db.beginTransaction()
        try {
            db.execSQL("ALTER TABLE objek_flora_fauna ADD COLUMN ciriCiri TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE objek_flora_fauna ADD COLUMN sebaran TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE objek_flora_fauna ADD COLUMN statusKonservasi TEXT NOT NULL DEFAULT ''")
            for (objek in seed.objek) {
                val values = ContentValues().apply {
                    put("ciriCiri", objek.ciriCiri)
                    put("sebaran", objek.sebaran)
                    put("statusKonservasi", objek.statusKonservasi)
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

    // ── Operasi Habitat ──────────────────────────────────────────────────────

    fun getAllHabitatsSync(): List<HabitatEntity> {
        val list = mutableListOf<HabitatEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, namaHabitat, assetFolder FROM habitat ORDER BY id ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    HabitatEntity(
                        id = it.getString(0),
                        namaHabitat = it.getString(1),
                        assetFolder = it.getString(2)
                    )
                )
            }
        }
        return list
    }

    fun getHabitatById(id: String): HabitatEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, namaHabitat, assetFolder FROM habitat WHERE id = ?", arrayOf(id))
        return cursor.use {
            if (it.moveToFirst()) {
                HabitatEntity(
                    id = it.getString(0),
                    namaHabitat = it.getString(1),
                    assetFolder = it.getString(2)
                )
            } else null
        }
    }

    // ── Operasi Objek Flora Fauna ────────────────────────────────────────────

    fun getRandomByHabitat(habitatId: String, count: Int): List<ObjekEntity> {
        val list = mutableListOf<ObjekEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT idObjek, idHabitat, kategori, namaObjek, namaLatin, namaInternasional, ciriCiri, sebaran, statusKonservasi, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath FROM objek_flora_fauna WHERE idHabitat = ? ORDER BY RANDOM() LIMIT ?",
            arrayOf(habitatId, count.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToObjek(it))
            }
        }
        return list
    }

    fun getAllObjekSync(): List<ObjekEntity> {
        val list = mutableListOf<ObjekEntity>()
        val cursor = readableDatabase.rawQuery(
            "SELECT idObjek, idHabitat, kategori, namaObjek, namaLatin, namaInternasional, ciriCiri, sebaran, statusKonservasi, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath FROM objek_flora_fauna ORDER BY idHabitat, namaObjek",
            null
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
            "SELECT idObjek, idHabitat, kategori, namaObjek, namaLatin, namaInternasional, ciriCiri, sebaran, statusKonservasi, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath FROM objek_flora_fauna WHERE idHabitat = ? ORDER BY RANDOM() LIMIT ?"
        } else {
            "SELECT idObjek, idHabitat, kategori, namaObjek, namaLatin, namaInternasional, ciriCiri, sebaran, statusKonservasi, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath FROM objek_flora_fauna WHERE idHabitat = ? AND idObjek NOT IN ($placeholders) ORDER BY RANDOM() LIMIT ?"
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
            "SELECT idObjek, idHabitat, kategori, namaObjek, namaLatin, namaInternasional, ciriCiri, sebaran, statusKonservasi, kataKunci, faktaUnik, gambarUtamaPath, gambarPasanganPath FROM objek_flora_fauna WHERE idObjek = ?",
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
            namaLatin = cursor.getString(4),
            namaInternasional = cursor.getString(5),
            ciriCiri = cursor.getString(6),
            sebaran = cursor.getString(7),
            statusKonservasi = cursor.getString(8),
            kataKunci = cursor.getString(9),
            faktaUnik = cursor.getString(10),
            gambarUtamaPath = cursor.getString(11),
            gambarPasanganPath = cursor.getString(12)
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

    fun catatPenemuan(idObjek: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idObjek", idObjek)
            put("waktuDitemukan", System.currentTimeMillis())
        }
        val inserted = db.insertWithOnConflict("buku_koleksi", null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1L
        return inserted
    }
}
