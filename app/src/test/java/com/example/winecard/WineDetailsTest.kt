package com.example.winecard

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.*
import org.junit.Test

class WineDetailsTest {
    @Test
    fun idsAreUniqueAndLookupUsesFullCatalog() {
        val vm = WineCatalogViewModel(SavedStateHandle())
        val wines = vm.uiState.wines
        assertEquals(6, wines.map { it.id }.distinct().size)
        vm.onCategorySelected("Ігристе")
        assertTrue(vm.uiState.filteredWines.isEmpty())
        wines.forEach { assertEquals(it, vm.findWineById(it.id)) }
    }

    @Test
    fun unknownIdReturnsNull() {
        val vm = WineCatalogViewModel(SavedStateHandle())
        assertNull(vm.findWineById(-1))
    }
}
