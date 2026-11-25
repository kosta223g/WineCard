package com.example.winecard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/*
 * Користувач обирає тип вина у верхньому рядку фільтрів,
 * після чого каталог показує лише вина вибраного типу
 * або повідомлення, якщо таких вин немає.
 *
 * Поля wine, winery, location, rating та image відповідають
 * структурі об'єктів SampleAPIs. category — локальне поле
 * класифікації для фільтрації каталогу за типом вина.
 */
data class WineRating(
    val average: String,
    val reviews: String
)

data class Wine(
    val wine: String,
    val winery: String,
    val location: String,
    val rating: WineRating,
    val image: String,
    val category: String
)

// Варіанти фільтра. Для "Ігристого" поки немає демонстраційних об'єктів,
// щоб можна було перевірити стан порожнього результату.
private val wineCategories = listOf(
    "Усі",
    "Червоне",
    "Біле",
    "Рожеве",
    "Ігристе"
)

// Демонстраційний список із шести вин зберігається окремо від верстки.
private val sampleWines = listOf(
    Wine(
        wine = "Cabernet Sauvignon",
        winery = "Caymus Vineyards",
        location = "Napa Valley, США",
        rating = WineRating(average = "4.6", reviews = "736 оцінок"),
        image = "wine_cabernet",
        category = "Червоне"
    ),
    Wine(
        wine = "Pinot Noir",
        winery = "La Crema",
        location = "Sonoma Coast, США",
        rating = WineRating(average = "4.4", reviews = "512 оцінок"),
        image = "wine_pinot",
        category = "Червоне"
    ),
    Wine(
        wine = "Chardonnay",
        winery = "Kendall-Jackson",
        location = "California, США",
        rating = WineRating(average = "4.3", reviews = "428 оцінок"),
        image = "wine_pinot",
        category = "Біле"
    ),
    Wine(
        wine = "Sauvignon Blanc",
        winery = "Cloudy Bay",
        location = "Marlborough, Нова Зеландія",
        rating = WineRating(average = "4.7", reviews = "891 оцінка"),
        image = "wine_pinot",
        category = "Біле"
    ),
    Wine(
        wine = "Whispering Angel Rosé",
        winery = "Château d'Esclans",
        location = "Provence, Франція",
        rating = WineRating(average = "4.5", reviews = "653 оцінки"),
        image = "wine_malbec",
        category = "Рожеве"
    ),
    Wine(
        wine = "Miraval Rosé",
        winery = "Château Miraval",
        location = "Provence, Франція",
        rating = WineRating(average = "4.4", reviews = "587 оцінок"),
        image = "wine_malbec",
        category = "Рожеве"
    )
)

data class WineCatalogUiState(
    val selectedCategory: String = "Усі",
    val categories: List<String> = wineCategories,
    val wines: List<Wine> = sampleWines
) {
    val filteredWines: List<Wine>
        get() = if (selectedCategory == "Усі") {
            wines
        } else {
            wines.filter { it.category == selectedCategory }
        }
}

class WineCatalogViewModel : ViewModel() {
    var uiState by mutableStateOf(WineCatalogUiState())
        private set

    fun onCategorySelected(category: String) {
        if (category in uiState.categories) {
            uiState = uiState.copy(selectedCategory = category)
        }
    }
}
