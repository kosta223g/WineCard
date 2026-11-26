package com.example.winecard

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// One delegate for the entire application.
private val Context.wineSettingsDataStore by preferencesDataStore(name = "wine_settings")
private val SORT_BY_RATING = booleanPreferencesKey("sort_by_rating")
private val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
private val THEME_MODE = stringPreferencesKey("theme_mode")
const val DEFAULT_SORT_BY_RATING = false

enum class WineThemeMode(val label: String, val description: String) {
    SYSTEM("Системна", "Як у налаштуваннях телефону"),
    LIGHT("Світла", "Кремовий фон і бордові акценти"),
    DARK("Темна", "Темний фон і ніжні винні акценти");

    companion object {
        fun fromStored(value: String?): WineThemeMode = entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

class WineSettingsRepository(context: Context) {
    private val store = context.applicationContext.wineSettingsDataStore

    val sortByRating: Flow<Boolean> = store.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences[SORT_BY_RATING] ?: DEFAULT_SORT_BY_RATING }

    val themeMode: Flow<WineThemeMode> = store.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { preferences -> WineThemeMode.fromStored(preferences[THEME_MODE]) }

    suspend fun setThemeMode(mode: WineThemeMode) {
        store.edit { preferences -> preferences[THEME_MODE] = mode.name }
    }

    val favoriteIds: Flow<Set<Int>> = store.data
        .map { preferences -> preferences[FAVORITE_IDS].orEmpty().mapNotNull { it.toIntOrNull() }.toSet() }

    suspend fun toggleFavorite(id: Int) {
        store.edit { preferences ->
            val ids = preferences[FAVORITE_IDS].orEmpty().toMutableSet()
            if (!ids.add(id.toString())) ids.remove(id.toString())
            preferences[FAVORITE_IDS] = ids
        }
    }

    suspend fun setSortByRating(enabled: Boolean) {
        store.edit { preferences -> preferences[SORT_BY_RATING] = enabled }
    }
}

internal fun sortCatalogWines(wines: List<Wine>, enabled: Boolean): List<Wine> =
    if (enabled) wines.sortedByDescending { it.rating.average.toDoubleOrNull() ?: Double.NEGATIVE_INFINITY }
    else wines
