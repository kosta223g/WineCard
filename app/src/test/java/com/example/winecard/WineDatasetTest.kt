package com.example.winecard

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class WineDatasetTest {
    @Test fun datasetSeparatesCategories() {
        val json = Json { ignoreUnknownKeys = true }
        val data = json.decodeFromString(WineDatasetDto.serializer(),
            """{"metaData":[],"reds":[{"id":1,"wine":"Test red"}],"whites":[{"id":1,"wine":"Test white"}],"sparkling":[]}""")
        assertEquals("Test red", data.category("reds").single().wine)
        assertEquals("Test white", data.category("whites").single().wine)
        assertTrue(data.category("sparkling").isEmpty())
        assertNotEquals(data.reds!!.single().toWine("Червоне")!!.id, data.whites!!.single().toWine("Біле")!!.id)
    }
}
