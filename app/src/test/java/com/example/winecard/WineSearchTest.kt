package com.example.winecard

import org.junit.Assert.assertEquals
import org.junit.Test

class WineSearchTest {
    private val red = WineDto(id = 1, wine = "Cabernet Sauvignon", winery = "Schrader", location = "Napa Valley").toWine("Червоне")!!
    private val white = WineDto(id = 1, wine = "Chardonnay", winery = "Château", location = "France").toWine("Біле")!!
    private val wines = listOf(red, white)

    @Test fun blankQueryReturnsCategory() {
        assertEquals(wines, filterCatalogWines(wines, "Усі", "  "))
        assertEquals(listOf(white), filterCatalogWines(wines, "Біле", ""))
    }
    @Test fun searchesNameProducerRegionIgnoringCaseAndAccents() {
        assertEquals(listOf(red), filterCatalogWines(wines, "Усі", "CABERNET napa"))
        assertEquals(listOf(white), filterCatalogWines(wines, "Усі", "chateau"))
    }
    @Test fun categoryAndSearchWorkTogether() {
        assertEquals(emptyList<Wine>(), filterCatalogWines(wines, "Біле", "Cabernet"))
        assertEquals(listOf(red), filterCatalogWines(wines, "Червоне", "Schrader"))
    }
    @Test fun noMatches() {
        assertEquals(emptyList<Wine>(), filterCatalogWines(wines, "Усі", "xyznotfound"))
    }
}
