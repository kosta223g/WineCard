package com.example.winecard

import java.text.Normalizer
import java.util.Locale

private fun searchableText(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)

internal fun filterCatalogWines(wines: List<Wine>, category: String, query: String): List<Wine> {
    val words = searchableText(query.trim()).split(Regex("\\s+")).filter { it.isNotEmpty() }
    return wines.filter { wine ->
        (category == "Усі" || wine.category == category) &&
            (words.isEmpty() || searchableText("${wine.wine} ${wine.winery} ${wine.location}")
                .let { text -> words.all { text.contains(it) } })
    }
}
