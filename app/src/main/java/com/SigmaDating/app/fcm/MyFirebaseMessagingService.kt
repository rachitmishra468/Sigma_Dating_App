package com.SigmaDating.app.fcm

import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.SigmaDating.R
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.video.IncomingVideoCall
import com.SigmaDating.app.views.Splash
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val VIDEO_USERID = "USERID"
    val VIDEO_NOTIFICATION_MatchID = "MATCHID"
    val USER_NAME = "NAME"
    val USER_IMAGE = "IMAGE"

    var user_ID = ""
    var type = ""
    var match_ID = ""
    var user_name=""
    var user_images=""
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            user_ID = remoteMessage.data["user_id"].toString()
            type = remoteMessage.data["type"].toString()
            match_ID = remoteMessage.data["match_id"].toString()
            user_name = remoteMessage.data["name"].toString()
            user_images = remoteMessage.data["image"].toString()
            showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["body"].toString(), "")
            if (type.equals("video")) {
                broadcastVideoNotification("", "")
            }

        }
    }


    companion object {
        private const val TAG = "TAG@123"
    }


    private fun showNotification(title: String, body: String, roomName: String) {
        Log.d(TAG, "Message Notification showNotification")
        var intent: Intent? = null
        if (type.equals("video")) {
            intent = Intent(this, IncomingVideoCall::class.java)
            AppUtils.playPhoneCallRing(this)
        } else {
            intent = Intent(this, Splash::class.java)
        }
        intent.putExtra(VIDEO_USERID, user_ID)
        intent.putExtra(VIDEO_NOTIFICATION_MatchID, match_ID)
        intent.putExtra(USER_NAME, user_name)
        intent.putExtra(USER_IMAGE, user_images)

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

        var builder = NotificationCompat.Builder(this, "MyNotification")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.app_logo)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        var manager = NotificationManagerCompat.from(this)
        manager.apply {
            notify(123, builder.build())
        }


    }

    private fun broadcastVideoNotification(title: String, roomName: String) {
        try {
            val intent = Intent(this, IncomingVideoCall::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(VIDEO_USERID, user_ID)
            intent.putExtra(VIDEO_NOTIFICATION_MatchID, match_ID)
            intent.putExtra(USER_NAME, user_name)
            intent.putExtra(USER_IMAGE, user_images)
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

}