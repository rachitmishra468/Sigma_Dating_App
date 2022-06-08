package com.example.sigmadatingapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Sigmadatingapp : Application()  {
    override fun onCreate() {
        super.onCreate()
    }
}