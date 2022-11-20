package com.SigmaDating.app.fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.SigmaDating.R
import com.SigmaDating.app.video.IncomingVideoCall
import com.SigmaDating.app.video.VideoActivity
import com.SigmaDating.app.views.Home
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val ACTION_VIDEO_NOTIFICATION = "VIDEO_NOTIFICATION"
    val VIDEO_NOTIFICATION_ROOM_NAME = "VIDEO_NOTIFICATION_ROOM_NAME"
    val VIDEO_NOTIFICATION_TITLE = "VIDEO_NOTIFICATION_TITLE"

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            showNotification(it.title!!, it.body!!, "")
            broadcastVideoNotification("", "")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    companion object {
        private const val TAG = "TAG@123"
    }


    private fun showNotification(title: String, body: String, roomName: String) {
        Log.d(TAG, "Message Notification showNotification")
        val intent = Intent(this, VideoActivity::class.java)
        intent.action = ACTION_VIDEO_NOTIFICATION
        intent.putExtra(VIDEO_NOTIFICATION_TITLE, title)
        intent.putExtra(VIDEO_NOTIFICATION_ROOM_NAME, roomName)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        var pendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: Notification.Builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_video_call_white_24dp)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun broadcastVideoNotification(title: String, roomName: String) {
     try{ /*  val intent = Intent(ACTION_VIDEO_NOTIFICATION)
        intent.putExtra(VIDEO_NOTIFICATION_TITLE, title)
        intent.putExtra(VIDEO_NOTIFICATION_ROOM_NAME, roomName)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)*/
        val intent = Intent(this, IncomingVideoCall::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Home.mVideoGrant_user_token="vnjzjfhvjfxkh"
        startActivity(intent)}catch (e:Exception){

        }
    }




}