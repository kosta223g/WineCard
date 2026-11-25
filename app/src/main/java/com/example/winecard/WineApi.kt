package com.example.winecard

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import kotlinx.coroutines.CancellationException
import retrofit2.http.Url
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

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
        image = normalizeWineImageUrl(image),
        category = category
    )
}

@Serializable
data class WineDatasetDto(
    val reds: List<WineDto>? = null,
    val whites: List<WineDto>? = null,
    val rose: List<WineDto>? = null,
    val sparkling: List<WineDto>? = null
) {
    fun category(endpoint: String): List<WineDto> = (when (endpoint) {
        "reds" -> reds
        "whites" -> whites
        "rose" -> rose
        "sparkling" -> sparkling
        else -> null
    }) ?: throw SerializationException("Missing category: $endpoint")
}

interface WineApi {
    @GET
    suspend fun getDataset(@Url url: String): WineDatasetDto

    @GET
    suspend fun getCategory(@Url url: String): List<WineDto>
}

object WineNetwork {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    val api: WineApi = retrofit.create(WineApi::class.java)
}

// Resolve and validate image addresses before they reach Compose.
internal fun normalizeWineImageUrl(value: String?): String {
    val raw = value?.trim().orEmpty()
    if (raw.isEmpty()) return ""
    val candidate = when {
        raw.startsWith("//") -> "https:$raw"
        raw.startsWith("http://", ignoreCase = true) -> "https://" + raw.substring(7)
        raw.startsWith("https://", ignoreCase = true) -> raw
        raw.startsWith("/thumbs/") -> "https://images.vivino.com$raw"
        raw.startsWith("thumbs/") -> "https://images.vivino.com/$raw"
        else -> return ""
    }
    val url = candidate.toHttpUrlOrNull() ?: return ""
    return if (url.isHttps && url.username.isEmpty() && url.password.isEmpty()) url.toString() else ""
}

// Separate remote category resources; at most two requests at a time globally.
internal const val WINE_CATEGORY_BASE = "https://raw.githubusercontent.com/kosta223g/WineCard/main/data/"
internal const val WINE_CATEGORY_MIRROR = "https://cdn.jsdelivr.net/gh/kosta223g/WineCard@main/data/"
private val catalogRequestLimit = Semaphore(2)

internal suspend fun loadWines(category: String, api: WineApi = WineNetwork.api): List<Wine> = coroutineScope {
    val endpoints = if (category == "Усі") wineEndpoints else wineEndpoints.filterKeys { it == category }
    endpoints.map { (label, endpoint) ->
        async {
            val rows = catalogRequestLimit.withPermit {
                try {
                    api.getCategory("$WINE_CATEGORY_BASE$endpoint.json")
                } catch (cancel: CancellationException) {
                    throw cancel
                } catch (error: Exception) {
                    try {
                        api.getCategory("$WINE_CATEGORY_MIRROR$endpoint.json")
                    } catch (cancel: CancellationException) {
                        throw cancel
                    } catch (mirror: Exception) {
                        if (mirror is IOException && (error is HttpException || error is SerializationException)) throw error
                        throw mirror
                    }
                }
            }
            val wines = rows.mapNotNull { it.toWine(label) }
            if (rows.isNotEmpty() && wines.isEmpty()) throw SerializationException("No valid wine IDs")
            wines
        }
    }.awaitAll().flatten().let(::uniqueCatalogWines)
}

// IDs are stable within category; repeated source IDs must never reach LazyColumn.
internal fun uniqueCatalogWines(wines: List<Wine>): List<Wine> = wines.distinctBy { it.id }

