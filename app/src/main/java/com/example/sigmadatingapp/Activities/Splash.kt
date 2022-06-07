package com.example.sigmadatingapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.sigmadatingapp.R

class Splash : AppCompatActivity() {
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
               startActivity(Intent(this,Login_Activity::class.java))
                finish()

            },
            500
        )
    }


}