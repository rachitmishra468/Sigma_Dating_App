package com.sigmadatingapp.api


import School_CommunityResponse
import com.sigmadatingapp.model.Loginmodel
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Forgotpassword
import retrofit2.Response
import retrofit2.http.*

interface ApiService{

    @GET("employees")
    suspend fun getEmployees():Response<Loginmodel>

    @POST("users/login")
    suspend fun login(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/register")
    suspend fun register(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/forgotpassword")
    suspend fun forgotpass(@Body jsonObject: JsonObject): Response<Forgotpassword>


    @POST("twilio/sendotp")
    suspend fun user_login_phone(@Body jsonObject: JsonObject): Response<Loginmodel>


    @POST("twilio/verifyotp")
    suspend fun user_phone_verifly(@Body jsonObject: JsonObject): Response<Loginmodel>

    @GET("common/get_common_listschoolscommunity")
    suspend fun listSchoolFeternity():Response<School_CommunityResponse>
}