package com.sigmadatingapp.api


import com.sigmadatingapp.module.Loginmodel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService{

    @GET("employees")
    suspend fun getEmployees():Response<Loginmodel>


    @POST("users/login")
    suspend fun login(@Body jsonObject: JsonObject): Response<Loginmodel>


}