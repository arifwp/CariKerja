package com.amikom.carikerja.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.amikom.carikerja.R
import com.amikom.carikerja.ui.MainActivity
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {


    private val TAG = "MessagingService"
    var NOTIFICATION_CHANNEL_ID = "com.amikom.carikerja.ui.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG,"message : ${remoteMessage}")

        if (remoteMessage.data.size > 0) {
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            Log.d(TAG,"title : ${title}")
            Log.d(TAG,"body : ${body}")
            showNotification(applicationContext, title, body)
        } else {
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            showNotification(applicationContext, title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)


        Log.e(TAG,"New Token $token")
    }


    fun showNotification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val ii: Intent
        ii = Intent(context, MainActivity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi =
            PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(getNotificationIcon())
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                title,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pi)
                .setOngoing(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.circle_logo else R.drawable.circle_logo
    }

}