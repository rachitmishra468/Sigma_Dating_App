package com.example.demoapp.api
import com.sigmadatingapp.module.Loginmodel
import com.google.gson.JsonObject
import retrofit2.Response


interface ApiHelper {

    suspend fun getEmployees():Response<Loginmodel>
    suspend fun user_login(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_register(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_login_phone(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_phone_verifly(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun get_login_user_data(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun change_password(jsonObject: JsonObject):Response<Loginmodel>

}