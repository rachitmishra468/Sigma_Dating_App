package com.SigmaDating.app

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.multidex.MultiDex
import com.SigmaDating.app.model.Pages
import com.SigmaDating.app.network.ConnectivityMonitorImpl
import com.google.android.gms.common.wrappers.InstantApps
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Sigmadatingapp : Application()  {

    private val instance: Sigmadatingapp? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun get(): Sigmadatingapp? {
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        AppReseources.setAppContext(applicationContext)
        firebaseAnalytics = Firebase.analytics
        MultiDex.install(this);
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG@123", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            fcm_token=token
            Log.d("TAG@123", token)

        })

        if (InstantApps.isInstantApp(this)) {
            firebaseAnalytics.setUserProperty("ANALYTICS_USER_PROP", "STATUS_INSTANT");
        } else {
            firebaseAnalytics.setUserProperty("ANALYTICS_USER_PROP", "STATUS_INSTALLED");
        }

        get_device_id()

    }
    companion object {
        var fcm_token: String=""
        var device_id:String=""
    }

    fun get_device_id(){
        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        Log.d("TAG@123", "devices id : "+device_id)
    }

}