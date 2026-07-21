package com.example.data

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "সহজ হিসাব"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        if (body.isNotEmpty()) {
            NotificationHelper.showNotification(
                context = applicationContext,
                title = title,
                message = body,
                notificationId = System.currentTimeMillis().toInt()
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Store or update in Firestore if user is authenticated
        // This is the real FCM token propagation logic
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val user = auth.currentUser
            if (user != null) {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users").document(user.uid)
                    .update("fcmToken", token)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
