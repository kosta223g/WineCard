package com.example.winecard

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import java.net.ConnectException
import java.net.SocketTimeoutException

enum class WineLoadError(val title: String, val message: String) {
    NoNetwork("Немає підключення до мережі", "Увімкни Wi-Fi або мобільний інтернет і спробуй ще раз."),
    Server("Сервер недоступний", "Джерело даних не відповідає або повернуло помилку. Спробуй ще раз пізніше."),
    Format("Некоректна відповідь", "Отримані дані мають неочікуваний формат. Спробуй повторити завантаження."),
    Other("Не вдалося завантажити вина", "Спробуй ще раз.")
}

internal fun hasNetworkConnection(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = manager.activeNetwork ?: return false
    return manager.getNetworkCapabilities(network)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

internal fun classifyWineError(error: Throwable, connected: Boolean): WineLoadError = when {
    !connected -> WineLoadError.NoNetwork
    error is SerializationException -> WineLoadError.Format
    error is HttpException -> WineLoadError.Server
    error is UnknownHostException || error is ConnectException || error is SocketTimeoutException || error is IOException -> WineLoadError.Server
    else -> WineLoadError.Other
}
