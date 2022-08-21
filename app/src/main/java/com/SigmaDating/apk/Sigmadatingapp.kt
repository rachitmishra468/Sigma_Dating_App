package com.SigmaDating.apk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Sigmadatingapp : Application()  {

    private val instance: Sigmadatingapp? = null
    //private val basicClient: ChatClientManager? = null

    fun get(): Sigmadatingapp? {
        return instance
    }

  /*  fun getChatClientManager(): ChatClientManager? {
        return basicClient
    }*/
    override fun onCreate() {
        super.onCreate()
        AppReseources.setAppContext(applicationContext)
    }
}