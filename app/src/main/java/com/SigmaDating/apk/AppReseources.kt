package com.SigmaDating.apk

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object AppReseources {

    private var sAppContext : Context?=null
    private var gso: GoogleSignInOptions?=null

    fun setAppContext(context: Context){
        if(sAppContext==null){
            sAppContext=context
        }

    }
    fun setGoogleSignInOptions(){
        if(gso==null){
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        }
    }

    fun getGoogleSignInOptions():GoogleSignInOptions?= gso

    fun getAppContext():Context?= sAppContext
}