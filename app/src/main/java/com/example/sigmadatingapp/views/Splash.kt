package com.example.sigmadatingapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.sigmadatingapp.R
import com.example.sigmadatingapp.storage.AppConstants
import com.example.sigmadatingapp.storage.SharedPreferencesStorage

import com.example.sigmadatingapp.views.login.Login_Activity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class Splash : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferencesStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


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


}