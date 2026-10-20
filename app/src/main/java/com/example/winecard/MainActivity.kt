
package com.example.winecard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winecard.ui.theme.WineCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WineCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🍷 Винна карта",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Відкривай світ вин, досліджуй " +
                                    "регіони та рейтинги й збирай " +
                                    "власну колекцію.",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Екрани застосунку",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("1. Каталог вин")
                        Text("2. Деталі вина")
                        Text("3. Мій погріб")
                        Text("4. Налаштування")

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Джерело даних: SampleAPIs",
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "api.sampleapis.com/wines/reds",
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Обране зберігатиметься на пристрої",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
