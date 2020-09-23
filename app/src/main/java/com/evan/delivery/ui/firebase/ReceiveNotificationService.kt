package com.evan.delivery.ui.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.evan.delivery.R
import com.evan.delivery.ui.spalash.SpalashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReceiveNotificationService : FirebaseMessagingService() {
    val TAG = "ReceiveNotificationService"

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "OnReceive: ${remoteMessage.from}")
        Log.d(TAG, "OnReceive: ${remoteMessage.data.get("title")}")
        Log.d(TAG, "OnReceive: ${remoteMessage.data.get("body")}")

        showNotificationAgain(remoteMessage.data.get("title"), remoteMessage.data.get("body"))

    }


    @SuppressLint("WrongConstant")
    private fun showNotificationAgain(title: String?, body: String?) {
        val intent = Intent(this, SpalashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "DOKAN"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "DOKAN NOTIFICATION",
                NotificationManager.IMPORTANCE_MAX
            )
            notificationChannel.description = "Evan Channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor=(Color.RED)
            notificationChannel.vibrationPattern= longArrayOf(0,1000,500,500)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

        }

        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder= NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
        notificationBuilder
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.alphabet)
            .setSound(uri)
            .setAutoCancel(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(resources,
                    R.drawable.letters))
            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setStyle( NotificationCompat.BigTextStyle().bigText(body))
            .setContentText(body)

        notificationManager.notify(0, notificationBuilder.build())

    }

}