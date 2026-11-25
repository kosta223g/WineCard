package com.example.winecard

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val CHANNEL = "wine_catalog_updates"

private fun canNotify(context: Context): Boolean =
    (Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) &&
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()

private fun showWineUpdate(context: Context, count: Int) {
    if (!canNotify(context)) return
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder = if (Build.VERSION.SDK_INT >= 26) {
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Оновлення каталогу", NotificationManager.IMPORTANCE_DEFAULT))
        Notification.Builder(context, CHANNEL)
    } else {
        @Suppress("DEPRECATION")
        Notification.Builder(context)
    }
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    manager.notify(8, builder.setSmallIcon(android.R.drawable.ic_popup_sync)
        .setContentTitle("Винна карта — каталог оновлено")
        .setContentText("Завантажено вин: $count")
        .setContentIntent(pending).setAutoCancel(true).build())
}

@Composable
fun WineUpdateNotificationControl() {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    fun checkCatalog() {
        if (!canNotify(context)) {
            message = "Сповіщення вимкнено. Каталог і надалі доступний."
            return
        }
        busy = true
        message = "Перевіряємо каталог…"
        scope.launch {
            try {
                val wines = loadWines("Усі")
                if (canNotify(context)) {
                    showWineUpdate(context, wines.size)
                    message = "Перевірку завершено. Сповіщення надіслано."
                } else {
                    message = "Сповіщення вимкнено. Каталог і надалі доступний."
                }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                message = classifyWineError(error, hasNetworkConnection(context)).message
            } finally {
                busy = false
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) checkCatalog() else message = "Дозвіл відхилено. Лише сповіщення недоступні."
    }
    Text("Сповіщення про оновлення", color = WineInk)
    Text("Перевірити джерело вин та отримати сповіщення з кількістю завантажених позицій.", color = WineMuted)
    OutlinedButton(
        enabled = !busy,
        onClick = {
            if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else checkCatalog()
        }
    ) { Text("Перевірити каталог зі сповіщенням") }
    if (message.isNotEmpty()) Text(message, color = WineMuted)
}
