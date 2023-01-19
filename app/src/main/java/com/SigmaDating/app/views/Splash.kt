package com.SigmaDating.app.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.SigmaDating.R
import com.SigmaDating.app.storage.AppConstants
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.views.login.Login_Activity
import com.example.demoapp.other.Constants
import com.google.android.gms.common.wrappers.InstantApps
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import javax.inject.Inject


@AndroidEntryPoint
class Splash : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage
    var match_ID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
// Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "my_item_id")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        if (InstantApps.isInstantApp(this)) {
            firebaseAnalytics.setUserProperty("ANALYTICS_USER_PROP", "STATUS_INSTANT");
        } else {
            firebaseAnalytics.setUserProperty("ANALYTICS_USER_PROP", "STATUS_INSTALLED");
        }

        match_ID = intent.getStringExtra("MATCHID").toString()
        Log.d("TAG@123", "identity : " + match_ID)
        GetKeyHase()

    }

    override fun onResume() {
        super.onResume()
        check_login_flag()
    }
//adb shell setprop debug.firebase.analytics.app com.SigmaDating.app.views.Splash

    fun check_login_flag() {
        Handler().postDelayed(
            {
                if (sharedPreferencesStorage.getBoolean(AppConstants.IS_AUTHENTICATED)) {


                  /*  if (!sharedPreferencesStorage.getBoolean(AppConstants.Disclaimer)) {
                        Disclaimer()
                    } else {*/
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "my_item_id")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                            param(
                                "USER_ID",
                                sharedPreferencesStorage.getString(AppConstants.USER_ID)
                            )
                            param(
                                "Screen Name",
                                "Splash Screen"
                            )
                            param(
                                "USER_TYPE",
                                "OLD"
                            )
                        }
                       // throw RuntimeException("Test Crash")
                        startActivity(Intent(this, Home::class.java))
                        finish()


                } else {
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                        param(
                            "USER_ID",
                            "New User"
                        )
                        param(
                            "Screen Name",
                            "Splash Screen"
                        )

                        param(
                            "USER_TYPE",
                            "NEW"
                        )
                    }
                    startActivity(Intent(this, Login_Activity::class.java))
                    finish()
                }

            },
            1000
        )
    }


    @SuppressLint("PackageManagerGetSignatures")
    private fun GetKeyHase() {
        try {
            val info = packageManager.getPackageInfo(
                "com.SigmaDating.app",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashkey_value: String = String(Base64.encode(md.digest(), 0))
                Log.d("TAG@123", "hash key " + hashkey_value)
                //check you logcat hash key value
            }
        } catch (e: Exception) {
            Log.d("TAG@123 exception", e.toString())
        }
    }

    private fun Disclaimer() {
        val dialog = Dialog(this, R.style.AppBaseTheme)
        dialog.setContentView(R.layout.full_screen_dialog)
        val logout = dialog.findViewById<Button>(R.id.logout)
        val cancle = dialog.findViewById<Button>(R.id.cancel)
        val webView = dialog.findViewById<WebView>(R.id.webView_diclamer)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Constants.BASE_URL+ Constants.disclaimer)
        dialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_UP) {
                finish()
                dialog.dismiss()
            }
            true
        }
        logout.setOnClickListener {
            sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                true
            )
            startActivity(Intent(this, Home::class.java))
            finish()
            dialog.dismiss()
        }
        cancle.setOnClickListener {
            sharedPreferencesStorage.setValue(
                AppConstants.Disclaimer,
                false
            )
            onBackPressed()
        }

        dialog.show()
    }
}