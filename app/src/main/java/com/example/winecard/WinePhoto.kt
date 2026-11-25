package com.example.winecard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
internal fun WinePhoto(wine: Wine, modifier: Modifier = Modifier) {
    val fallback = painterResource(R.drawable.ic_wine_photo_placeholder)
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(wine.image.takeIf { it.isNotBlank() })
            .crossfade(true)
            .build(),
        contentDescription = "Фото вина: ${wine.wine}",
        modifier = modifier,
        contentScale = ContentScale.Fit,
        placeholder = fallback,
        error = fallback,
        fallback = fallback
    )
}
