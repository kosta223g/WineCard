package com.example.winecard

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.winecard.ui.theme.WineCardTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineCatalogScreen(
    onItemClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    sortByRating: Boolean,
    listState: LazyListState,
    viewModel: WineCatalogViewModel,
    favoriteIds: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onCellarClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val selectedCategory = uiState.selectedCategory
    val filteredWines = remember(uiState.wines, selectedCategory, uiState.appliedQuery, sortByRating) {
        sortCatalogWines(uiState.filteredWines, sortByRating)
    }

    val focusManager = LocalFocusManager.current
    var searchFocused by remember { mutableStateOf(false) }
    val suggestions = remember(uiState.wines, selectedCategory, uiState.searchQuery) {
        suggestCatalogWines(uiState.wines, selectedCategory, uiState.searchQuery)
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = WineInk
                        )
                        Text(
                            text = "WINE COLLECTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = WineMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCellarClick) {
                        Icon(painterResource(R.drawable.ic_wine_heart), "Мій погріб", tint = WineBurgundy)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(painter = painterResource(R.drawable.ic_wine_settings),
                            contentDescription = "Налаштування", tint = WineBurgundy)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(end = 18.dp)
                    ) {
                        Text(
                            text = "№ 13",
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                            color = WineBurgundy,
                            style = MaterialTheme.typography.labelMedium
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
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Смакуй момент.\nОбирай своє вино.",
                color = WineInk,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Відкрий цікаві сорти, познайомся з виноробнями та знайди нових фаворитів.",
                color = WineMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth().onFocusChanged { searchFocused = it.isFocused },
                label = { Text("Пошук вина") },
                placeholder = { Text("Назва, виробник або регіон") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearSearch) { Text("×", style = MaterialTheme.typography.headlineSmall) }
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
            if (searchFocused && suggestions.isNotEmpty() && !uiState.isLoading && uiState.error == null) {
                Spacer(Modifier.height(6.dp))
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface).padding(8.dp)
                ) {
                    Text("ПІДКАЗКИ", color = WineMuted, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    suggestions.forEach { wine ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                focusManager.clearFocus()
                                onItemClick(wine.id)
                            }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WinePhoto(wine, Modifier.width(32.dp).height(48.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(wine.wine, color = WineInk, style = MaterialTheme.typography.bodyMedium)
                                Text(wine.winery, color = WineMuted, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ОБЕРИ ТИП ВИНА",
                color = WineBurgundy,
                style = MaterialTheme.typography.labelSmall
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
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (uiState.isSearching) "ПОШУК…" else "ПОЗИЦІЙ: ${filteredWines.size}",
                    color = WineMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (sortByRating) "Сортування: від найвищого рейтингу" else "Сортування: порядок каталогу",
                color = WineMuted, style = MaterialTheme.typography.bodySmall
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
                    WineCard(wine = wine, onClick = { onItemClick(wine.id) }, isFavorite = wine.id in favoriteIds, onToggleFavorite = { onToggleFavorite(wine.id) })
                }
            }
            item(key = "catalog-footer", contentType = "footer") {
                Text("Дані SampleAPIs · демонстраційні рейтинги · лабораторна робота №13", color = WineMuted,
                    style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(vertical = 12.dp))
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
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = WineMuted,
                    selectedContainerColor = WineBurgundy,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun EmptySearchResults(query: String, onClear: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Нічого не знайдено", color = WineInk, style = MaterialTheme.typography.titleLarge)
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
            .background(MaterialTheme.colorScheme.surface)
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
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Поки немає вин цієї категорії",
            color = WineInk,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Сервер повернув порожній список. Обери іншу категорію або онови каталог.",
            color = WineMuted,
            style = MaterialTheme.typography.bodySmall
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

        val cellarViewModel: WineCellarViewModel = viewModel()
        val favoriteIds = cellarViewModel.favoriteIds.collectAsState().value
        val entry by navController.currentBackStackEntryAsState()
        val route = entry?.destination?.route
        val snackbar = remember { SnackbarHostState() }
        LaunchedEffect(cellarViewModel) {
            cellarViewModel.messages.collect { snackbar.showSnackbar(it) }
        }
        val openTab: (String) -> Unit = { tab ->
            if (route != tab) {
                navController.navigate(tab) {
                    popUpTo("catalog") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                if (route == "catalog" || route == "cellar" || route == "settings") {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(selected = route == "catalog", onClick = { openTab("catalog") },
                            icon = { Icon(painterResource(R.drawable.ic_wine_catalog), null) }, label = { Text("Каталог") })
                        NavigationBarItem(selected = route == "cellar", onClick = { openTab("cellar") },
                            icon = { Icon(painterResource(R.drawable.ic_wine_heart), null) }, label = { Text("Мій погріб") })
                        NavigationBarItem(selected = route == "settings", onClick = { openTab("settings") },
                            icon = { Icon(painterResource(R.drawable.ic_wine_settings), null) }, label = { Text("Налаштування") })
                    }
                }
            }
        ) { outerPadding ->
            NavHost(navController = navController, startDestination = "catalog", modifier = Modifier.padding(outerPadding)) {
                composable("catalog") {
                    WineCatalogScreen(
                        viewModel = catalogViewModel,
                        listState = catalogListState,
                        sortByRating = sortByRating ?: DEFAULT_SORT_BY_RATING,
                        favoriteIds = favoriteIds.orEmpty(),
                        onToggleFavorite = cellarViewModel::toggleFavorite,
                        onCellarClick = { openTab("cellar") },
                        onSettingsClick = { openTab("settings") },
                        onItemClick = { id -> navController.navigate("details/$id") { launchSingleTop = true } }
                    )
                }
                composable("cellar") {
                    WineCellarScreen(
                        viewModel = cellarViewModel,
                        onCatalogClick = { openTab("catalog") },
                        onItemClick = { id -> navController.navigate("cellar-details/$id") { launchSingleTop = true } }
                    )
                }
                composable("settings") {
                    WineSettingsScreen(
                        sortByRating = sortByRating,
                        onSortChanged = { enabled -> scope.launch { settings.setSortByRating(enabled) } },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable("details/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { detailEntry ->
                    val id = detailEntry.arguments?.getInt("id")
                    WineDetailsScreen(
                        wine = id?.let { catalogViewModel.findWineById(it) },
                        isLoading = catalogViewModel.uiState.isLoading,
                        error = catalogViewModel.uiState.error,
                        onRetry = catalogViewModel::refresh,
                        onBackClick = { navController.popBackStack() },
                        isFavorite = id != null && id in favoriteIds.orEmpty(),
                        onToggleFavorite = id?.let { selected -> { cellarViewModel.toggleFavorite(selected) } }
                    )
                }
                composable("cellar-details/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { detailEntry ->
                    val id = detailEntry.arguments?.getInt("id")
                    val state = cellarViewModel.uiState
                    WineDetailsScreen(
                        wine = state.wines.firstOrNull { it.id == id },
                        isLoading = state.isLoading,
                        error = state.error,
                        onRetry = cellarViewModel::refresh,
                        onBackClick = { navController.popBackStack() },
                        isFavorite = id != null && id in favoriteIds.orEmpty(),
                        onToggleFavorite = id?.let { selected -> { cellarViewModel.toggleFavorite(selected) } }
                    )
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            WineCardApp()
        }
    }
}
