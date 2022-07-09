package com.example.demoapp.api

import com.sigmadatingapp.api.ApiService
import com.sigmadatingapp.model.Loginmodel
import com.google.gson.JsonObject
import com.sigmadatingapp.api.ApiHelper
import com.sigmadatingapp.model.Forgotpassword
import com.sigmadatingapp.model.SchoolCommunityResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun getEmployees(): Response<Loginmodel> = apiService.getEmployees()
    override suspend fun user_login(jsonObject: JsonObject): Response<Loginmodel> =apiService.login(jsonObject)
    override suspend fun user_register(jsonObject: JsonObject): Response<Loginmodel> =apiService.register(jsonObject)
    override suspend fun user_forgot_password(jsonObject: JsonObject): Response<Forgotpassword> =apiService.forgotpass(jsonObject)
    override suspend fun user_login_phone(jsonObject: JsonObject): Response<Loginmodel> =apiService.user_login_phone(jsonObject)
    override suspend fun user_phone_verifly(jsonObject: JsonObject): Response<Loginmodel> =apiService.user_phone_verifly(jsonObject)
    override suspend fun getSchoolFeternityList(): Response<SchoolCommunityResponse>   =apiService.listSchoolFeternity()
    override suspend fun upload_images(jsonObject: JsonObject): Response<Loginmodel> =apiService.upload_images(jsonObject)
    override suspend fun get_login_user_data(jsonObject: JsonObject): Response<Loginmodel> =apiService.get_login_user_data(jsonObject)
    override suspend fun change_password(jsonObject: JsonObject): Response<Loginmodel> =apiService.change_password(jsonObject)
    override suspend fun delete_images(jsonObject: JsonObject): Response<Loginmodel> =apiService.delete_images(jsonObject)
    override suspend fun Update_profile(jsonObject: JsonObject): Response<Loginmodel> =apiService.Update_profile(jsonObject)



}