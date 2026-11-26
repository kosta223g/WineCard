package com.example.winecard

import org.junit.Assert.assertEquals
import org.junit.Test

class WineFavoritesTest {
    @Test fun idsResolveCategories() {
        assertEquals(listOf("Червоне", "Рожеве"), favoriteCategories(setOf(100000001, 300000002)))
        assertEquals(emptyList<String>(), favoriteCategories(emptySet()))
    }
    @Test fun selectsByCompositeIdNotRawId() {
        val red = WineDto(id = 1).toWine("Червоне")!!
        val white = WineDto(id = 1).toWine("Біле")!!
        assertEquals(listOf(white), selectFavoriteWines(listOf(red, white), setOf(white.id)))
    }
}
