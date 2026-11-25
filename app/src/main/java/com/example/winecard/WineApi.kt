package com.example.winecard

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Serializable
data class WineRatingDto(val average: JsonPrimitive? = null, val reviews: JsonPrimitive? = null)

@Serializable
data class WineDto(
    val id: Int? = null,
    val wine: String? = null,
    val winery: String? = null,
    val location: String? = null,
    val image: String? = null,
    val rating: WineRatingDto? = null
)

internal val wineEndpoints = linkedMapOf(
    "Червоне" to "reds", "Біле" to "whites", "Рожеве" to "rose", "Ігристе" to "sparkling"
)

internal fun WineDto.toWine(category: String): Wine? {
    val rawId = id ?: return null
    val index = wineEndpoints.keys.indexOf(category)
    if (index < 0 || rawId !in 0..99_999_999) return null
    return Wine(
        id = (index + 1) * 100_000_000 + rawId,
        apiId = rawId,
        wine = wine?.trim()?.takeIf { it.isNotEmpty() } ?: "Без назви",
        winery = winery?.trim()?.takeIf { it.isNotEmpty() } ?: "Не вказано",
        location = location?.replace(Regex("\\s+"), " ")?.trim()?.takeIf { it.isNotEmpty() } ?: "Не вказано",
        rating = WineRating(
            rating?.average?.contentOrNull?.takeIf { it.toDoubleOrNull()?.isFinite() == true } ?: "—",
            rating?.reviews?.contentOrNull?.takeIf { it.isNotBlank() } ?: "Немає даних"
        ),
        image = image?.trim().orEmpty(),
        category = category
    )
}

interface WineApi {
    @GET("wines/{category}")
    suspend fun getWines(@Path("category") category: String): List<WineDto>
}

object WineNetwork {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sampleapis.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    val api: WineApi = retrofit.create(WineApi::class.java)
}

internal suspend fun loadWines(category: String, api: WineApi = WineNetwork.api): List<Wine> = coroutineScope {
    val endpoints = if (category == "Усі") wineEndpoints else wineEndpoints.filterKeys { it == category }
    endpoints.map { (label, endpoint) ->
        async { api.getWines(endpoint).mapNotNull { it.toWine(label) } }
    }.flatMap { it.await() }.distinctBy { it.id }
}
