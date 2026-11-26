package com.example.winecard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painterResource(if (isFavorite) R.drawable.ic_wine_heart_filled else R.drawable.ic_wine_heart),
            if (isFavorite) "Видалити з обраного" else "Додати до обраного",
            tint = WineBurgundy
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WineCellarScreen(viewModel: WineCellarViewModel, onCatalogClick: () -> Unit, onItemClick: (Int) -> Unit) {
    val ids by viewModel.favoriteIds.collectAsState()
    val state = viewModel.uiState
    val wines = state.wines.filter { it.id in ids.orEmpty() }
    LaunchedEffect(viewModel) { viewModel.refresh() }
    Scaffold(
        containerColor = WineBackground,
        topBar = {
            TopAppBar(title = { Text("Мій погріб", color = WineInk) },
                actions = { TextButton(onClick = viewModel::refresh) { Text("Оновити") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WineBackground))
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            item(key = "cellar-header") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ОСОБИСТА КОЛЕКЦІЯ", color = WineBurgundy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Твої улюблені вина", color = WineInk, fontSize = 29.sp, fontFamily = FontFamily.Serif)
                    Text("Збережено: ${ids?.size ?: 0}", color = WineMuted)
                }
            }
            when {
                ids == null || state.isLoading -> item(key = "cellar-loading") {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WineBurgundy)
                    }
                }
                ids.orEmpty().isEmpty() -> item(key = "cellar-empty") {
                    Column(Modifier.fillMaxWidth().padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource(R.drawable.ic_wine_heart), null, tint = WineBurgundy, modifier = Modifier.size(48.dp))
                        Text("Твій погріб ще порожній", fontSize = 22.sp, color = WineInk)
                        Text("Познач вина сердечком, щоб зберегти їх тут.", color = WineMuted)
                        OutlinedButton(onClick = onCatalogClick) { Text("До каталогу") }
                    }
                }
                state.error != null -> item(key = "cellar-error") { WineErrorContent(state.error, viewModel::refresh) }
                else -> {
                    items(wines, key = { it.id }) { wine ->
                        WineCard(wine, { onItemClick(wine.id) }, true, { viewModel.toggleFavorite(wine.id) })
                    }
                    if (wines.size < ids.orEmpty().size) item(key = "cellar-missing") {
                        Text("Деякі збережені вина зараз відсутні у джерелі. Їхні ID залишаються збереженими.", color = WineMuted)
                    }
                }
            }
        }
    }
}
