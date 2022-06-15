package com.sigmadatingapp.storage

import android.content.SharedPreferences
import javax.inject.Inject

// @Inject tells Dagger how to provide instances of this type
class SharedPreferencesStorage @Inject constructor(val sharedPreferences : SharedPreferences) : Storage {

//    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun <T> setValue(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when(value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)!!
    }

    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)!!
    }

    fun clear(){
        sharedPreferences.edit().clear().apply()
    }
}