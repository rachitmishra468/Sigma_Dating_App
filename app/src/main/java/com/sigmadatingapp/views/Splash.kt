package com.sigmadatingapp.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.sigmadatingapp.R
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage

import com.sigmadatingapp.views.login.Login_Activity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import com.sigmadatingapp.views.intro_registration.OnBoardingActivity
import java.lang.Exception
import java.security.MessageDigest


@AndroidEntryPoint
class Splash : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
       // GetKeyHase()

    }

    override fun onResume() {
        super.onResume()
        check_login_flag()
    }

    fun check_login_flag() {
        Handler().postDelayed(
            {


                if (sharedPreferencesStorage!!.getBoolean(AppConstants.IS_AUTHENTICATED)) {
                    startActivity(Intent(this, Login_Activity::class.java))
                    finish()
                } else {
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
                "com.sigmadatingapp",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashkey_value: String = String(Base64.encode(md.digest(), 0))
                Log.d("TAG@123  hash key", hashkey_value)
                //check you logcat hash key value
            }
        } catch (e: Exception) {
            Log.d("TAG@123 exception", e.toString())
        }
    }
}