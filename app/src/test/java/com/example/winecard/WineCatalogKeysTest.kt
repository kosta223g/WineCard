package com.example.winecard

import org.junit.Assert.*
import org.junit.Test

class WineCatalogKeysTest {
    @Test fun sameSourceIdInDifferentCategoriesHasDifferentKey() {
        val dto = WineDto(id = 42)
        val wines = wineEndpoints.keys.map { dto.toWine(it)!! }
        assertEquals(wines.size, wines.map { it.id }.toSet().size)
        assertEquals(wines.map { it.id }, wineEndpoints.keys.map { dto.toWine(it)!!.id })
    }

    @Test fun duplicateRowsAreRemovedWithoutReordering() {
        val red = WineDto(id = 42).toWine("Червоне")!!
        val white = WineDto(id = 42).toWine("Біле")!!
        val another = WineDto(id = 43).toWine("Червоне")!!
        val result = uniqueCatalogWines(listOf(red, white, red.copy(wine = "Duplicate"), another))
        assertEquals(listOf(red, white, another), result)
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }
}
