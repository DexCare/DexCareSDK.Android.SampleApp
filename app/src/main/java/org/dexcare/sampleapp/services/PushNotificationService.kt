package org.dexcare.sampleapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.dexcare.sampleapp.R
import org.dexcare.sampleapp.ui.common.SchedulingInfo
import org.koin.android.ext.android.get

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        get<SchedulingInfo>().fcmId = token
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification(p0)
    }

    private companion object {
        const val CHANNEL_ID = 55555
        const val NOTIFICATION_ID = 8080
    }

    private fun showNotification(notification: RemoteMessage) {
        val channelID = packageName + CHANNEL_ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "virtual_visit_notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val title = notification.data["title"]
        val message = notification.data["message"]

        val builder = NotificationCompat.Builder(this, channelID)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_check_circle_green_24dp)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }
}
