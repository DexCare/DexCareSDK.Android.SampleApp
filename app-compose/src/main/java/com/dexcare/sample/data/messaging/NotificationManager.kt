package com.dexcare.sample.data.messaging

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.dexcare.acme.android.R
import com.dexcare.sample.presentation.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

class NotificationManager(@ApplicationContext private val context: Context) {

    fun notify(notificationMessage: NotificationMessage) {
        if (!hasNotificationPermission()) {
            Timber.w("Notification Permission is not available. Notification message will be discarded.")
            return
        }
        val resultIntent = MainActivity.createNotificationIntent(context, notificationMessage)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntent(resultIntent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        createNotificationChannel(
            CHANNEL_ID_VIRTUAL_CARE,
            "Virtual Visits",
            "Get important reminders about your virtual visit"
        )
        createSimpleNotification(
            title = notificationMessage.title,
            content = notificationMessage.message,
            notificationId = notificationMessage.title.hashCode() + notificationMessage.message.hashCode(),
            channelId = CHANNEL_ID_VIRTUAL_CARE,
            pendingIntent = pendingIntent
        )
    }

    fun hasNotificationPermission(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        Timber.i("hasNotificationPermission $result")
        return result
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(
        activity: FragmentActivity,
        onPermissionResult: (Boolean) -> Unit
    ) {
        val singlePermissionLauncher: ActivityResultLauncher<String> =
            activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                onPermissionResult(isGranted)
            }
        singlePermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String,
        importance: Int = android.app.NotificationManager.IMPORTANCE_DEFAULT
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )
        channel.description = channelDescription
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createSimpleNotification(
        title: String,
        content: String,
        notificationId: Int,
        channelId: String,
        pendingIntent: PendingIntent?,
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (@Suppress("SwallowedException") ex: SecurityException) {
                Timber.e("Failed to show notification. Permission not available")
            }

        }
    }

    companion object {
        private const val CHANNEL_ID_VIRTUAL_CARE = "1001"
    }
}
