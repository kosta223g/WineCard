package com.example.winecard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
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
    val id: Int,
    val wine: String,
    val winery: String,
    val location: String,
    val rating: WineRating,
    val image: String,
    val category: String,
    val grapes: String,
    val taste: String,
    val pairing: String
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
        id = 1,
        wine = "Cabernet Sauvignon",
        winery = "Caymus Vineyards",
        location = "Napa Valley, США",
        rating = WineRating(average = "4.6", reviews = "736 оцінок"),
        image = "wine_cabernet",
        category = "Червоне",
        grapes = "Cabernet Sauvignon",
        taste = "Чорна смородина, темні ягоди, виразні таніни.",
        pairing = "Стейк, запечене м'ясо, витримані сири."
    ),
    Wine(
        id = 2,
        wine = "Pinot Noir",
        winery = "La Crema",
        location = "Sonoma Coast, США",
        rating = WineRating(average = "4.4", reviews = "512 оцінок"),
        image = "wine_pinot",
        category = "Червоне",
        grapes = "Pinot Noir",
        taste = "Вишня, малина, легкі землисті відтінки.",
        pairing = "Качка, гриби, запечена птиця."
    ),
    Wine(
        id = 3,
        wine = "Chardonnay",
        winery = "Kendall-Jackson",
        location = "California, США",
        rating = WineRating(average = "4.3", reviews = "428 оцінок"),
        image = "wine_pinot",
        category = "Біле",
        grapes = "Chardonnay",
        taste = "Яблуко, цитрусові, можливі вершкові нотки.",
        pairing = "Риба, курка, вершкові соуси."
    ),
    Wine(
        id = 4,
        wine = "Sauvignon Blanc",
        winery = "Cloudy Bay",
        location = "Marlborough, Нова Зеландія",
        rating = WineRating(average = "4.7", reviews = "891 оцінка"),
        image = "wine_pinot",
        category = "Біле",
        grapes = "Sauvignon Blanc",
        taste = "Цитрусові, зелене яблуко, трав'янисті нотки.",
        pairing = "Морепродукти, салати, козячий сир."
    ),
    Wine(
        id = 5,
        wine = "Whispering Angel Rosé",
        winery = "Château d'Esclans",
        location = "Provence, Франція",
        rating = WineRating(average = "4.5", reviews = "653 оцінки"),
        image = "wine_malbec",
        category = "Рожеве",
        grapes = "Grenache та інші сорти",
        taste = "Полуниця, персик, свіжі фруктові нотки.",
        pairing = "Легкі салати, морепродукти, овочі."
    ),
    Wine(
        id = 6,
        wine = "Miraval Rosé",
        winery = "Château Miraval",
        location = "Provence, Франція",
        rating = WineRating(average = "4.4", reviews = "587 оцінок"),
        image = "wine_malbec",
        category = "Рожеве",
        grapes = "Grenache та інші сорти",
        taste = "Червоні ягоди, цитрусові, свіжа кислотність.",
        pairing = "Риба, овочі на грилі, легкі закуски."
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

class WineCatalogViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var uiState by mutableStateOf(
        WineCatalogUiState(
            selectedCategory = savedStateHandle.get<String>("selectedCategory")
                ?.takeIf { it in wineCategories } ?: "Усі"
        )
    )
        private set

    fun findWineById(id: Int): Wine? = uiState.wines.firstOrNull { it.id == id }

    fun onCategorySelected(category: String) {
        if (category in uiState.categories) {
            uiState = uiState.copy(selectedCategory = category)
            savedStateHandle["selectedCategory"] = category
        }
    }
}
