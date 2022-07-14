package com.SigmaDating.apk

import android.content.Context

object AppReseources {

    private var sAppContext : Context?=null

    fun setAppContext(context: Context){
        if(sAppContext==null){
            sAppContext=context
        }

    }


    fun getAppContext():Context?= sAppContext
}