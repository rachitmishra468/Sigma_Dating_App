package com.example.sigmadatingapp.storage

interface Storage {

    fun <T> setValue(key: String, value:T)
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getInt(key: String): Int
}