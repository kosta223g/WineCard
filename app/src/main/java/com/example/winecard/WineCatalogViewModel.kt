package com.example.winecard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class WineRating(val average: String, val reviews: String)
data class Wine(
    val id: Int, val apiId: Int, val wine: String, val winery: String,
    val location: String, val rating: WineRating, val image: String, val category: String
)

private val wineCategories = listOf("Усі") + wineEndpoints.keys

data class WineCatalogUiState(
    val selectedCategory: String = "Усі",
    val categories: List<String> = wineCategories,
    val wines: List<Wine> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false
) {
    val filteredWines: List<Wine>
        get() = if (selectedCategory == "Усі") wines else wines.filter { it.category == selectedCategory }
}

class WineCatalogViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var uiState by mutableStateOf(WineCatalogUiState(
        selectedCategory = savedStateHandle.get<String>("selectedCategory")
            ?.takeIf { it in wineCategories } ?: "Усі"
    ))
        private set
    private var loadingJob: Job? = null

    init { refresh() }

    fun findWineById(id: Int): Wine? = uiState.wines.firstOrNull { it.id == id }

    fun onCategorySelected(category: String) {
        if (category !in uiState.categories || category == uiState.selectedCategory) return
        savedStateHandle["selectedCategory"] = category
        uiState = uiState.copy(selectedCategory = category)
        refresh()
    }

    fun refresh() {
        loadingJob?.cancel()
        val category = uiState.selectedCategory
        uiState = uiState.copy(wines = emptyList(), isLoading = true, hasError = false)
        loadingJob = viewModelScope.launch {
            try {
                val wines = loadWines(category)
                uiState = uiState.copy(wines = wines, isLoading = false)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                uiState = uiState.copy(isLoading = false, hasError = true)
            }
        }
    }
}
