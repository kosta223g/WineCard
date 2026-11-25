package com.example.winecard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WineSortingTest {
    @Test fun defaultPreservesCatalogOrder() {
        val wines = WineCatalogUiState().wines
        assertFalse(DEFAULT_SORT_BY_RATING)
        assertEquals(wines, sortCatalogWines(wines, DEFAULT_SORT_BY_RATING))
    }

    @Test fun enabledSortsByDescendingNumericRating() {
        val wines = WineCatalogUiState().wines
        assertEquals(listOf(4, 1, 5, 2, 6, 3), sortCatalogWines(wines, true).map { it.id })
    }

    @Test fun sortingRespectsCategoryAndEmptyResults() {
        val state = WineCatalogUiState(selectedCategory = "Біле")
        assertEquals(listOf(4, 3), sortCatalogWines(state.filteredWines, true).map { it.id })
        assertEquals(emptyList<Wine>(), sortCatalogWines(emptyList(), true))
    }
}
