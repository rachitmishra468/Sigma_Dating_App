package com.SigmaDating.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.SigmaDating.R
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.video.IncomingVideoCall
import com.SigmaDating.app.video.VideoActivity
import com.SigmaDating.app.views.Home
import com.SigmaDating.app.views.Splash
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


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
    var sender_id=""
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.size>0) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            user_ID = remoteMessage.data["user_id"].toString()
            type = remoteMessage.data["type"].toString()
            match_ID = remoteMessage.data["match_id"].toString()
            user_name = remoteMessage.data["name"].toString()
            user_images = remoteMessage.data["image"].toString()
            sender_id= remoteMessage.data["sender_id"].toString()
            if(type.equals("close_video")){
                AppUtils.stopPhoneCallRing()
                Log.d(TAG, "call_cut Notification recived user id : $user_ID  sender id : $sender_id : ${remoteMessage.from}")
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent("BROADCAST_FOR_CLOSE_VIDEO"))
            }else{
                showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["body"].toString())
            if (type.equals("video")) {
                Home.sender_id=sender_id
                Home.match_id = match_ID
                broadcastVideoNotification("", "")
            }

        }}
    }


    companion object {
        private const val TAG = "TAG@123"
    }


    private fun showNotification(title: String, body: String) {
        createNotificationChannel()
        Log.d(TAG, "Message Notification showNotification")
        var intent: Intent? = null
        if (type.equals("video")) {
             intent = Intent(this, VideoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            AppUtils.playPhoneCallRing(this)
        } else {
            intent = Intent(this, Splash::class.java)
        }
        intent.putExtra("TYPE", 1)
        intent.putExtra("SENDERID", sender_id)
        intent.putExtra(VIDEO_USERID, user_ID)
        intent.putExtra(VIDEO_NOTIFICATION_MatchID, match_ID)
        intent.putExtra(USER_NAME, user_name)
        intent.putExtra(USER_IMAGE, user_images)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        val builder = NotificationCompat.Builder(this, "all_notifications")
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.app_logo, getString(R.string.app_name),
                pendingIntent)
        val manager = NotificationManagerCompat.from(this)
        manager.apply {
            cancelAll()
            notify(generateRandom(), builder.build())
        }
    }

    private fun broadcastVideoNotification(title: String, roomName: String) {
        try {
            val intent = Intent(this, VideoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(VIDEO_USERID, user_ID)
            intent.putExtra(VIDEO_NOTIFICATION_MatchID, match_ID)
            intent.putExtra(USER_NAME, user_name)
            intent.putExtra(USER_IMAGE, user_images)
            intent.putExtra("TYPE", 1)
            intent.putExtra("SENDERID", sender_id)
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    fun generateRandom(): Int {
        return 0
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

}