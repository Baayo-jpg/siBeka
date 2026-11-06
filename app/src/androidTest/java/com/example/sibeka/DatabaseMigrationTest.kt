package com.example.sibeka

import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sibeka.data.db.SiBekaDatabaseHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    @Test
    fun versionFourUpgradeRemovesUnusedAudioColumnAndPreservesCollection() {
        val context = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .createDeviceProtectedStorageContext()
        context.deleteDatabase(SiBekaDatabaseHelper.DATABASE_NAME)

        val file = context.getDatabasePath(SiBekaDatabaseHelper.DATABASE_NAME)
        file.parentFile?.mkdirs()
        val oldDatabase = SQLiteDatabase.openOrCreateDatabase(file, null)
        oldDatabase.execSQL(
            """
            CREATE TABLE habitat (
                id TEXT PRIMARY KEY,
                namaHabitat TEXT NOT NULL,
                assetFolder TEXT NOT NULL
            )
            """.trimIndent()
        )
        oldDatabase.execSQL(
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
        oldDatabase.execSQL(
            """
            CREATE TABLE buku_koleksi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idObjek TEXT NOT NULL UNIQUE,
                waktuDitemukan INTEGER NOT NULL,
                FOREIGN KEY (idObjek) REFERENCES objek_flora_fauna (idObjek)
            )
            """.trimIndent()
        )
        oldDatabase.execSQL("INSERT INTO habitat VALUES ('HH', 'Hutan Hujan', 'hutan_hujan_tropis')")
        oldDatabase.execSQL(
            """
            INSERT INTO objek_flora_fauna VALUES (
                'HH-F-001', 'HH', 'fauna', 'Beruang Madu', 'BERUANG',
                'Fakta', 'hutan_hujan_tropis/fauna/beruang_madu.svg',
                'hutan_hujan_tropis/pasangan/pasangan_madu.svg', ''
            )
            """.trimIndent()
        )
        oldDatabase.execSQL("INSERT INTO buku_koleksi (idObjek, waktuDitemukan) VALUES ('HH-F-001', 1)")
        oldDatabase.version = 4
        oldDatabase.close()

        val helper = SiBekaDatabaseHelper(context)
        try {
            val database = helper.writableDatabase
            val columns = mutableSetOf<String>()
            database.rawQuery("PRAGMA table_info(objek_flora_fauna)", null).use { cursor ->
                while (cursor.moveToNext()) columns.add(cursor.getString(1))
            }
            assertFalse("fileSuaraPath column should be removed", "fileSuaraPath" in columns)
            database.rawQuery("SELECT COUNT(*) FROM buku_koleksi", null).use { cursor ->
                assertEquals(true, cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
            }
        } finally {
            helper.close()
            context.deleteDatabase(SiBekaDatabaseHelper.DATABASE_NAME)
        }
    }
}
