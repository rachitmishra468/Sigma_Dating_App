package com.sigmadatingapp.api

import android.util.Base64
import okhttp3.*

class AuthenticationInterceptor : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        var requestAvailable: Request? = null
        try {
            val credentials = "admin" + ":" + "1234"
            val auth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            requestAvailable = response?.request?.newBuilder()
                ?.addHeader("Authorization", auth)
                ?.addHeader("X_API_KEY", "anonymous")
                ?.build()
            return requestAvailable
        } catch (ex: Exception) { }
        return requestAvailable
    }

}