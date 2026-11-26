package com.example.winecard

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun WineSettingsScreen(
    sortByRating: Boolean?,
    onSortChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = WineBackground,
        topBar = {
            TopAppBar(
                title = { Text("Налаштування", color = WineInk) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_wine_back), "Назад", tint = WineBurgundy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WineBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Твій каталог", color = WineInk, style = MaterialTheme.typography.headlineMedium)
            Text("Налаштуй порядок відображення вин. Вибір зберігається на пристрої.",
                color = WineMuted)
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Сортувати за рейтингом", color = WineInk)
                    Text("Найвищі оцінки першими. Якщо вимкнено — початковий порядок каталогу.",
                        color = WineMuted, style = MaterialTheme.typography.bodySmall)
                }
                if (sortByRating == null) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = WineBurgundy)
                } else {
                    Switch(
                        checked = sortByRating,
                        onCheckedChange = onSortChanged,
                        colors = SwitchDefaults.colors(checkedTrackColor = WineBurgundy)
                    )
                }
            }
            Text("Зміни застосовуються автоматично.", color = WineMuted, style = MaterialTheme.typography.bodySmall)
            WineUpdateNotificationControl()
        }
    }
}
