package com.dexcare.sample.data.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New Firebase token generated: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("New message received:: data=${message.data}")
        message.data.let {
            val notificationMessage = NotificationMessage(
                title = it["title"].orEmpty(),
                message = it["message"].orEmpty(),
                url = it["visitURL"].orEmpty()
            )
            notificationManager.notify(notificationMessage)
        }
    }
}
