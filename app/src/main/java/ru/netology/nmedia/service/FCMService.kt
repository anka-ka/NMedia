package ru.netology.nmedia.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import ru.netology.nmedia.auth.AppAuth

import kotlin.random.Random
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val contentJson = message.data["content"]
        val contentObject = contentJson?.let {
            gson.fromJson(it, NotificationContent::class.java)
        }
        val recipientId = contentObject?.recipientId
        val currentRecipientId = AppAuth.getInstance().data.value?.id?: 0L

        when {
            recipientId == null -> {
                Log.i("FCMService", "Mass notification received.")
                showNotification(message)
            }
            recipientId == currentRecipientId -> {
                Log.i("FCMService", "Notification for current recipient received.")
                showNotification(message)
            }
            recipientId == 0L -> {
                Log.i("FCMService", "Anonymous authentication detected. Resending push token.")
                AppAuth.getInstance().sendPushToken()
            }
            recipientId != 0L && recipientId != currentRecipientId -> {
                Log.i("FCMService", "Different authentication detected. Resending push token.")
                AppAuth.getInstance().sendPushToken()
            }
        }
    }
    private fun showNotification(message: RemoteMessage) {
        message.data[action]?.let {
            try {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(
                            message.data[content],
                            Like::class.java
                        )
                    )
                    Action.POST -> handleNewPost(
                        gson.fromJson(
                            message.data[content],
                            Post::class.java
                        )
                    )
                }
            } catch (e: RuntimeException) {
                println("ERROR")
            }
        }
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val intent = Intent(this, AppActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            -1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun handleNewPost(content: Post) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_new_post,
                    content.postAuthor,
                )
            )
            .setContentText(
                content.content
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
}

enum class Action {
    LIKE,
    POST
}

data class Like(
    val postId: Long,
    val userName: String,
    val postAuthor: String
)

data class Post(
    val postId: Long,
    val postAuthor: String,
    val content: String,
)
data class NotificationContent(
    val recipientId: Long?,
    val content: String,
)