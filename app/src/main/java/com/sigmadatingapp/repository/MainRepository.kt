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
    suspend fun user_forgotpass(jsonObject: JsonObject) = apiHelper.user_forgot_password(jsonObject)
    suspend fun user_login_phone(jsonObject: JsonObject) = apiHelper.user_login_phone(jsonObject)
    suspend fun user_phone_verifly(jsonObject: JsonObject) = apiHelper.user_phone_verifly(jsonObject)
    suspend fun  ListSchoolFeternity()=apiHelper.getSchoolFeternityList()
    suspend fun get_login_user_data(jsonObject: JsonObject) = apiHelper.get_login_user_data(jsonObject)
    suspend fun change_password(jsonObject: JsonObject)=apiHelper.change_password(jsonObject)


}