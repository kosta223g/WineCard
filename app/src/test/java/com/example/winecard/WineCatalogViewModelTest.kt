package com.example.winecard

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WineCatalogViewModelTest {
    @Test
    fun initialStateShowsAllSixWines() {
        val model = WineCatalogViewModel(SavedStateHandle())
        assertEquals("Усі", model.uiState.selectedCategory)
        assertEquals(6, model.uiState.filteredWines.size)
    }

    @Test
    fun selectingCategoryCreatesNewStateAndFiltersWines() {
        val model = WineCatalogViewModel(SavedStateHandle())
        val previousState = model.uiState
        model.onCategorySelected("Біле")
        assertEquals("Усі", previousState.selectedCategory)
        assertEquals("Біле", model.uiState.selectedCategory)
        assertEquals(2, model.uiState.filteredWines.size)
        assertTrue(model.uiState.filteredWines.all { it.category == "Біле" })
    }

    @Test
    fun emptyCategoryHasNoResults() {
        val model = WineCatalogViewModel(SavedStateHandle())
        model.onCategorySelected("Ігристе")
        assertTrue(model.uiState.filteredWines.isEmpty())
        model.onCategorySelected("Усі")
        assertEquals(6, model.uiState.filteredWines.size)
    }
    @Test
    fun restoresSelectedCategoryFromSavedState() {
        val handle = SavedStateHandle()
        val original = WineCatalogViewModel(handle)
        original.onCategorySelected("Ігристе")
        val restored = WineCatalogViewModel(
            SavedStateHandle(mapOf("selectedCategory" to handle.get<String>("selectedCategory")))
        )
        assertEquals("Ігристе", restored.uiState.selectedCategory)
        assertTrue(restored.uiState.filteredWines.isEmpty())
    }
}
