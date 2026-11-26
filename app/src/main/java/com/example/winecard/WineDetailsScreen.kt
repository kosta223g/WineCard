package com.example.winecard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineDetailsScreen(wine: Wine?, isLoading: Boolean = false, error: WineLoadError? = null, onRetry: () -> Unit = {}, onBackClick: () -> Unit, isFavorite: Boolean = false, onToggleFavorite: (() -> Unit)? = null) {
    Scaffold(
        containerColor = WineBackground,
        topBar = {
            TopAppBar(
                title = { Text("Деталі вина", color = WineInk) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_wine_back),
                            contentDescription = "Назад",
                            tint = WineBurgundy
                        )
                    }
                },
                actions = { if (wine != null && onToggleFavorite != null) FavoriteButton(isFavorite, onToggleFavorite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WineBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = WineBurgundy)
            } else if (error != null) {
                WineErrorContent(error, onRetry)
            } else if (wine == null) {
                Text("Вино не знайдено", style = MaterialTheme.typography.headlineSmall, color = WineInk)
                Text("Цієї позиції немає в каталозі. Повернися до списку та обери інше вино.",
                    color = WineMuted)
                OutlinedButton(onClick = onBackClick) { Text("До каталогу") }
            } else {
                WinePhoto(wine, Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp))
                Text(wine.category.uppercase(), color = WineBurgundy, style = MaterialTheme.typography.bodySmall)
                Text(wine.wine, color = WineInk, style = MaterialTheme.typography.headlineLarge)
                Text(wine.winery, color = WineMuted, style = MaterialTheme.typography.bodyLarge)
                Text("★ ${wine.rating.average} · Демо-рейтинг",
                    color = WineGold)
                WineDetailSection("Регіон", wine.location)
                WineDetailSection("Виробник", wine.winery)
                WineDetailSection("Кількість оцінок у джерелі", wine.rating.reviews)
                WineDetailSection("ID у джерелі", wine.apiId.toString())
                Text("Джерело: SampleAPIs. Рейтинг демонстраційний. Фото з адреси джерела.",
                    color = WineMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun WineDetailSection(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(label.uppercase(), color = WineBurgundy, style = MaterialTheme.typography.labelMedium)
        Text(value, color = WineInk, style = MaterialTheme.typography.bodyLarge)
    }
}
