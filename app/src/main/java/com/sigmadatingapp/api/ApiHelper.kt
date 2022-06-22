package com.example.demoapp.api
import com.sigmadatingapp.module.Loginmodel
import com.google.gson.JsonObject
import retrofit2.Response


interface ApiHelper {

    suspend fun getEmployees():Response<Loginmodel>

    suspend fun user_login(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_register(jsonObject: JsonObject):Response<Loginmodel>
}