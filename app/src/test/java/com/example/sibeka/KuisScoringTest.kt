package com.example.sibeka

import com.example.sibeka.data.db.SeedDataWrapper
import com.example.sibeka.data.model.AturanPermainan
import com.example.sibeka.data.model.ModeKuis
import com.example.sibeka.ui.kuis.URUTAN_MODE
import com.example.sibeka.ui.kuis.info
import com.example.sibeka.ui.kuis.poinSetelahBukaPotongan
import com.example.sibeka.ui.kuis.skorJawaban
import com.google.gson.Gson
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KuisScoringTest {
    @Test
    fun potonganGambarUsesRemainingPoints() {
        assertEquals(100, skorJawaban(ModeKuis.POTONGAN_GAMBAR, 100))
        assertEquals(55, skorJawaban(ModeKuis.POTONGAN_GAMBAR, 55))
        assertEquals(20, skorJawaban(ModeKuis.POTONGAN_GAMBAR, 20))
    }

    @Test
    fun otherQuizModesKeepFixedScore() {
        assertEquals(100, skorJawaban(ModeKuis.PASANGKAN, 55))
        assertEquals(100, skorJawaban(ModeKuis.SUSUN_HURUF, 20))
    }

    /** Aturan Potongan Gambar: −15 per ubin, berhenti di poin minimal. */
    @Test
    fun potonganPointsStepDownFifteenAndNeverBelowMinimum() {
        assertEquals(85, poinSetelahBukaPotongan(AturanPermainan.POIN_POTONGAN_AWAL))

        var poin = AturanPermainan.POIN_POTONGAN_AWAL
        repeat(AturanPermainan.JUMLAH_UBIN_POTONGAN) { poin = poinSetelahBukaPotongan(poin) }
        assertEquals(AturanPermainan.POIN_POTONGAN_MINIMAL, poin)
        assertEquals(
            AturanPermainan.POIN_POTONGAN_MINIMAL,
            poinSetelahBukaPotongan(AturanPermainan.POIN_POTONGAN_MINIMAL)
        )
    }

    /** Setiap mode wajib punya metadata lengkap dan muncul sekali di urutan tampil. */
    @Test
    fun everyModeHasCompleteInfoAndAppearsOnceInOrder() {
        val terdaftar = mutableSetOf<ModeKuis>()
        URUTAN_MODE.forEach { mode ->
            assertTrue("Mode ${mode.name} muncul dua kali", terdaftar.add(mode))
            val info = mode.info
            assertTrue(info.judul.isNotBlank())
            assertTrue(info.deskripsi.isNotBlank())
            assertTrue(info.inisial.isNotBlank())
            assertTrue(info.judulKuis.isNotBlank())
            assertTrue(info.instruksi.isNotBlank())
            assertTrue(info.hintBubble.isNotBlank())
            assertTrue(info.hintTombol.isNotBlank())
        }
        assertEquals(ModeKuis.entries.size, URUTAN_MODE.size)
    }

    @Test
    fun seedHasNoBomAndEveryObjectHasExistingImageAssetsAndUniqueKeyword() {
        val seedFile = File("src/main/assets/data/seed_data.json")
        val bytes = seedFile.readBytes()
        assertFalse(bytes.take(3) == listOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
        val seed = Gson().fromJson(bytes.toString(Charsets.UTF_8), SeedDataWrapper::class.java)
        val keywords = mutableSetOf<String>()

        assertEquals(64, seed.objek.size)
        seed.objek.forEach { objek ->
            assertTrue("Duplicate keyword: ${objek.kataKunci}", keywords.add(objek.kataKunci))
            assertFalse(objek.gambarUtamaPath.startsWith("placeholder"))
            assertTrue(File("src/main/assets", objek.gambarUtamaPath).isFile)
            assertTrue(File("src/main/assets", objek.gambarPasanganPath).isFile)
        }
    }
}
