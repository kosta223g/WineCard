package com.example.winecard

import androidx.compose.foundation.Image
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
fun WineDetailsScreen(wine: Wine?, onBackClick: () -> Unit) {
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
            if (wine == null) {
                Text("Вино не знайдено", fontSize = 24.sp, color = WineInk,
                    fontWeight = FontWeight.SemiBold)
                Text("Цієї позиції немає в каталозі. Повернися до списку та обери інше вино.",
                    color = WineMuted)
                OutlinedButton(onClick = onBackClick) { Text("До каталогу") }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                        .clip(RoundedCornerShape(24.dp)).background(Color(0xFFEDE0D6)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(wineImageResource(wine.image)),
                        contentDescription = "Пляшка вина ${wine.wine}",
                        modifier = Modifier.fillMaxHeight().padding(20.dp)
                    )
                }
                Text(wine.category.uppercase(), color = WineBurgundy,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
                Text(wine.wine, color = WineInk, fontSize = 32.sp, lineHeight = 38.sp,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold)
                Text(wine.winery, color = WineMuted, fontSize = 16.sp)
                Text("★ ${wine.rating.average} · ${wine.rating.reviews}",
                    color = WineGold, fontWeight = FontWeight.Bold)
                WineDetailSection("Регіон", wine.location)
                WineDetailSection("Сорт винограду", wine.grapes)
                WineDetailSection("Типовий смаковий профіль", wine.taste)
                WineDetailSection("З чим поєднувати", wine.pairing)
                Text("Демонстраційні характеристики та ілюстрація. Смакові нотки є орієнтовними, а не перевіреними даними виробника.",
                    color = WineMuted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun WineDetailSection(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(label.uppercase(), color = WineBurgundy, fontSize = 11.sp,
            letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        Text(value, color = WineInk, fontSize = 16.sp, lineHeight = 23.sp)
    }
}
