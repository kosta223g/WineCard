package com.example.winecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.winecard.ui.theme.WineCardTheme

internal val WineBackground = Color(0xFFF7F3ED)
internal val WineInk = Color(0xFF2B2021)
internal val WineBurgundy = Color(0xFF76283B)
internal val WineMuted = Color(0xFF887D77)
internal val WineGold = Color(0xFFB6813F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineCatalogScreen(
    onItemClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    sortByRating: Boolean,
    listState: LazyListState,
    viewModel: WineCatalogViewModel
) {
    val uiState = viewModel.uiState
    val selectedCategory = uiState.selectedCategory
    val filteredWines = remember(uiState.wines, selectedCategory, uiState.appliedQuery, sortByRating) {
        sortCatalogWines(uiState.filteredWines, sortByRating)
    }

    var previousQuery by rememberSaveable { mutableStateOf(uiState.appliedQuery) }
    LaunchedEffect(uiState.appliedQuery) {
        if (previousQuery != uiState.appliedQuery) {
            listState.scrollToItem(0)
            previousQuery = uiState.appliedQuery
        }
    }

    Scaffold(
        containerColor = WineBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ВИННА КАРТА",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = WineInk
                        )
                        Text(
                            text = "WINE COLLECTION",
                            fontSize = 9.sp,
                            letterSpacing = 2.sp,
                            color = WineMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(painter = painterResource(R.drawable.ic_wine_settings),
                            contentDescription = "Налаштування", tint = WineBurgundy)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF0E7DF),
                        modifier = Modifier.padding(end = 18.dp)
                    ) {
                        Text(
                            text = "№ 11",
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                            color = WineBurgundy,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WineBackground
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::pullToRefresh,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            item(key = "catalog-header", contentType = "header") {
                Column {

            Text(
                text = "КОЛЕКЦІЯ ДЛЯ ЗНАЙОМСТВА",
                color = WineBurgundy,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Смакуй момент.\nОбирай своє вино.",
                color = WineInk,
                fontSize = 31.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Відкрий цікаві сорти, познайомся з виноробнями та знайди нових фаворитів.",
                color = WineMuted,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Пошук вина") },
                placeholder = { Text("Назва, виробник або регіон") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearSearch) { Text("×", fontSize = 24.sp) }
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ОБЕРИ ТИП ВИНА",
                color = WineBurgundy,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            WineCategorySelector(
                categories = uiState.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(23.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (uiState.appliedQuery.isNotBlank()) "Результати пошуку" else if (selectedCategory == "Усі") "Добірка вин" else selectedCategory + " вина",
                    color = WineInk,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = if (uiState.isSearching) "ПОШУК…" else "ПОЗИЦІЙ: ${filteredWines.size}",
                    color = WineMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (sortByRating) "Сортування: від найвищого рейтингу" else "Сортування: порядок каталогу",
                color = WineMuted, fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

                }
            }
            uiState.refreshError?.let { error ->
                item(key = "catalog-refresh-error", contentType = "status") {
                    WineErrorContent(error, viewModel::pullToRefresh)
                }
            }
            when {
                uiState.isLoading -> item(key = "catalog-loading", contentType = "status") {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WineBurgundy)
                    }
                }
                uiState.error != null -> item(key = "catalog-error", contentType = "status") {
                    WineErrorContent(error = uiState.error, onRetry = viewModel::refresh)
                }
                uiState.isSearching -> item(key = "catalog-searching", contentType = "status") {
                    Text("Шукаємо вина…", color = WineMuted)
                }
                uiState.isRefreshing && filteredWines.isEmpty() -> item(key = "catalog-refreshing", contentType = "status") {
                    Text("Оновлюємо каталог…", color = WineMuted)
                }
                filteredWines.isEmpty() && uiState.appliedQuery.isNotBlank() -> item(key = "catalog-search-empty", contentType = "status") {
                    EmptySearchResults(uiState.appliedQuery, viewModel::clearSearch)
                }
                filteredWines.isEmpty() -> item(key = "catalog-empty", contentType = "status") { EmptyWineResults(onRetry = viewModel::refresh) }
                else -> items(filteredWines, key = { it.id }, contentType = { "wine" }) { wine ->
                    WineCard(wine = wine, onClick = { onItemClick(wine.id) })
                }
            }
            item(key = "catalog-footer", contentType = "footer") {
                Text("Дані SampleAPIs · демонстраційні рейтинги · лабораторна робота №11", color = WineMuted,
                    fontSize = 10.sp, modifier = Modifier.padding(vertical = 12.dp))
            }
        }
        }
    }
}

@Composable
private fun WineCategorySelector(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (selectedCategory == category) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = WineMuted,
                    selectedContainerColor = WineBurgundy,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun EmptySearchResults(query: String, onClear: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Нічого не знайдено", color = WineInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("За запитом «$query» у вибраній категорії немає вин.", color = WineMuted)
        OutlinedButton(onClick = onClear) { Text("Очистити пошук") }
    }
}

@Composable
private fun EmptyWineResults(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(WineBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🍷",
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Поки немає вин цієї категорії",
            color = WineInk,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Сервер повернув порожній список. Обери іншу категорію або онови каталог.",
            color = WineMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        OutlinedButton(onClick = onRetry) { Text("Оновити каталог") }
    }
}

@Composable
fun WineCardApp() {
    WineCardTheme {
        // Activity-scoped ViewModel survives navigation and rotation.
        val catalogViewModel: WineCatalogViewModel = viewModel()
        // Hoisted outside NavHost: retained while the details destination is visible.
        val catalogListState = rememberLazyListState()
        val navController = rememberNavController()
        val context = LocalContext.current.applicationContext
        val settings = remember(context) { WineSettingsRepository(context) }
        val sortByRating by settings.sortByRating.collectAsState(initial = null)
        val scope = rememberCoroutineScope()

        NavHost(navController = navController, startDestination = "catalog") {
            composable("catalog") {
                WineCatalogScreen(
                    viewModel = catalogViewModel,
                    listState = catalogListState,
                    sortByRating = sortByRating ?: DEFAULT_SORT_BY_RATING,
                    onSettingsClick = {
                        navController.navigate("settings") { launchSingleTop = true }
                    },
                    onItemClick = { id ->
                        navController.navigate("details/$id") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("settings") {
                WineSettingsScreen(
                    sortByRating = sortByRating,
                    onSortChanged = { enabled ->
                        scope.launch { settings.setSortByRating(enabled) }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id")
                WineDetailsScreen(
                    wine = id?.let { catalogViewModel.findWineById(it) },
                    isLoading = catalogViewModel.uiState.isLoading,
                    error = catalogViewModel.uiState.error,
                    onRetry = catalogViewModel::refresh,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WineCardApp()
        }
    }
}
