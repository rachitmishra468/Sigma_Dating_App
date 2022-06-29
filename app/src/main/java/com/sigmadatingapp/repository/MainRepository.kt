package com.sigmadatingapp.repository

import com.example.demoapp.api.ApiHelper
import com.google.gson.JsonObject
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper:ApiHelper
){

    suspend fun getEmployee() = apiHelper.getEmployees()
    suspend fun user_login(jsonObject: JsonObject) = apiHelper.user_login(jsonObject)
    suspend fun user_register(jsonObject: JsonObject) = apiHelper.user_register(jsonObject)
    suspend fun user_login_phone(jsonObject: JsonObject) = apiHelper.user_login_phone(jsonObject)
    suspend fun user_phone_verifly(jsonObject: JsonObject) = apiHelper.user_phone_verifly(jsonObject)

}