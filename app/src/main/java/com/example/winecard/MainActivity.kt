package com.example.winecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.winecard.ui.theme.WineCardTheme

private val WineBackground = Color(0xFFF7F3ED)
private val WineInk = Color(0xFF2B2021)
private val WineBurgundy = Color(0xFF76283B)
private val WineMuted = Color(0xFF887D77)
private val WineGold = Color(0xFFB6813F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineCatalogScreen(
    viewModel: WineCatalogViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val selectedCategory = uiState.selectedCategory
    val filteredWines = uiState.filteredWines

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
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF0E7DF),
                        modifier = Modifier.padding(end = 18.dp)
                    ) {
                        Text(
                            text = "№ 04",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

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
                    text = if (selectedCategory == "Усі") "Добірка вин" else selectedCategory + " вина",
                    color = WineInk,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "ПОЗИЦІЙ: ${filteredWines.size}",
                    color = WineMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredWines.isEmpty()) {
                EmptyWineResults()
            } else {
                filteredWines.forEachIndexed { index, wine ->
                    WineCard(wine = wine)
                    if (index != filteredWines.lastIndex) {
                        Spacer(modifier = Modifier.height(13.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(WineGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Демонстраційний каталог · лабораторна робота №4",
                    color = WineMuted,
                    fontSize = 10.sp
                )
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
private fun EmptyWineResults() {
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
            text = "Обери інший тип вина, щоб переглянути доступні позиції.",
            color = WineMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun WineCard(wine: Wine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(112.dp)
                .height(148.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5EDE6),
                            Color(0xFFEDE0D6)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = wineImageResource(wine.image)),
                contentDescription = "Пляшка вина ${wine.wine}",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 13.dp, vertical = 8.dp)
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(7.dp),
                color = Color(0xFFFDF9F4),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = wine.category.uppercase(),
                    color = WineBurgundy,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = wine.winery.uppercase(),
                color = WineBurgundy,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = wine.wine,
                color = WineInk,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(11.dp))

            Text(
                text = "РЕГІОН",
                color = WineMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = wine.location,
                color = WineInk,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★ ${wine.rating.average}",
                    color = WineGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = wine.rating.reviews,
                    color = WineMuted,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun wineImageResource(image: String): Int {
    return when (image) {
        "wine_cabernet" -> R.drawable.wine_cabernet
        "wine_pinot" -> R.drawable.wine_pinot
        "wine_malbec" -> R.drawable.wine_malbec
        else -> R.drawable.wine_cabernet
    }
}

@Composable
fun WineCardApp() {
    WineCardTheme {
        WineCatalogScreen()
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
