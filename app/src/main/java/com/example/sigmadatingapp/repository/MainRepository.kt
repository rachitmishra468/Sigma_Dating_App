package com.example.sigmadatingapp.repository

import com.example.demoapp.api.ApiHelper
import com.example.sigmadatingapp.api.ApiService
import com.google.gson.JsonObject
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper:ApiHelper
){

    suspend fun getEmployee() = apiHelper.getEmployees()
    suspend fun user_login(jsonObject: JsonObject) = apiHelper.user_login(jsonObject)

}