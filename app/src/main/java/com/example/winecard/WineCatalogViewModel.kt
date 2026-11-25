package com.example.winecard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

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
    val isRefreshing: Boolean = false,
    val refreshError: WineLoadError? = null,
    val error: WineLoadError? = null
) {
    val filteredWines: List<Wine>
        get() = if (selectedCategory == "Усі") wines else wines.filter { it.category == selectedCategory }
}

class WineCatalogViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
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

    fun refresh() = requestCatalog(keepContent = false)

    fun pullToRefresh() {
        if (uiState.isLoading || uiState.isRefreshing) return
        requestCatalog(keepContent = true)
    }

    private fun requestCatalog(keepContent: Boolean) {
        loadingJob?.cancel()
        val category = uiState.selectedCategory
        uiState = uiState.copy(
            wines = if (keepContent) uiState.wines else emptyList(),
            isLoading = !keepContent, isRefreshing = keepContent,
            error = null, refreshError = null
        )
        loadingJob = viewModelScope.launch {
            try {
                if (!hasNetworkConnection(getApplication())) {
                    finishFailure(WineLoadError.NoNetwork, keepContent)
                    return@launch
                }
                val wines = loadWines(category)
                uiState = uiState.copy(wines = wines, isLoading = false, isRefreshing = false, error = null, refreshError = null)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                finishFailure(classifyWineError(error, hasNetworkConnection(getApplication())), keepContent)
            }
        }
    }
    private fun finishFailure(error: WineLoadError, keepContent: Boolean) {
        val retain = keepContent && uiState.wines.isNotEmpty()
        uiState = uiState.copy(
            isLoading = false, isRefreshing = false,
            error = if (retain) null else error,
            refreshError = if (retain) error else null
        )
    }
}
