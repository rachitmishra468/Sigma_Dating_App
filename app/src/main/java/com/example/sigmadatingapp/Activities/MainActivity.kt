package com.example.sigmadatingapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sigmadatingapp.Activities.ui.main.MainFragment
import com.example.sigmadatingapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}