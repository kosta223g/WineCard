package com.example.winecard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class WineCellarUiState(
    val wines: List<Wine> = emptyList(),
    val isLoading: Boolean = false,
    val error: WineLoadError? = null
)

class WineCellarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WineSettingsRepository(application)
    private val _favoriteIds = MutableStateFlow<Set<Int>?>(null)
    val favoriteIds = _favoriteIds.asStateFlow()
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()
    var uiState by mutableStateOf(WineCellarUiState())
        private set
    private var job: Job? = null
    private var opened = false
    private var loaded = false
    val hasLoadedOrLoading: Boolean get() = loaded || uiState.isLoading || uiState.error != null
    var storageError by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            try {
                repository.favoriteIds.collect { ids ->
                    val previous = _favoriteIds.value
                    _favoriteIds.value = ids
                    if (opened) {
                        // Removed items disappear immediately. Newly selected IDs need a fresh response.
                        if (previous != null && loaded && !uiState.isLoading && ids.all { id -> uiState.wines.any { it.id == id } }) {
                            if (ids.isEmpty()) uiState = uiState.copy(error = null)
                        } else refresh()
                    }
                }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                storageError = true
                _messages.emit("Не вдалося прочитати обране")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            try { repository.toggleFavorite(id) }
            catch (cancel: CancellationException) { throw cancel }
            catch (error: Exception) { _messages.emit("Не вдалося зберегти обране") }
        }
    }

    fun refresh() {
        opened = true
        job?.cancel()
        val ids = _favoriteIds.value ?: return
        if (ids.isEmpty()) {
            loaded = true
            uiState = WineCellarUiState()
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)
        job = viewModelScope.launch {
            try {
                if (!hasNetworkConnection(getApplication())) {
                    uiState = uiState.copy(isLoading = false, error = WineLoadError.NoNetwork)
                    return@launch
                }
                val wines = loadFavoriteWines(ids)
                loaded = true
                uiState = WineCellarUiState(wines = wines)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                uiState = uiState.copy(isLoading = false, error = classifyWineError(error, hasNetworkConnection(getApplication())))
            }
        }
    }
}
