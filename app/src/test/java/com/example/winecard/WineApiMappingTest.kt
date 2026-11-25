package com.example.winecard

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class WineApiMappingTest {
    @Test fun missingFieldsAreSafe() {
        val dto = Json.decodeFromString(WineDto.serializer(), """{"id":1}""")
        val wine = dto.toWine("Червоне")!!
        assertEquals("Без назви", wine.wine)
        assertEquals("—", wine.rating.average)
        assertNull(WineDto().toWine("Червоне"))
    }
    @Test fun numericRatingAndDistinctCategoryIds() {
        val dto = Json.decodeFromString(WineDto.serializer(), """{"id":1,"rating":{"average":4.7,"reviews":"88 ratings"}}""")
        val red = dto.toWine("Червоне")!!
        val white = dto.toWine("Біле")!!
        assertNotEquals(red.id, white.id)
        assertEquals("4.7", red.rating.average)
        assertEquals(listOf(white, red), sortCatalogWines(listOf(white, red), true))
    }
}
