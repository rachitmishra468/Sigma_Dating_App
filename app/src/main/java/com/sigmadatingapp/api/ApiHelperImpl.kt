package com.example.demoapp.api

import School_CommunityResponse
import com.sigmadatingapp.api.ApiService
import com.sigmadatingapp.model.Loginmodel
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Forgotpassword
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
    override suspend fun getSchoolFeternityList(): Response<School_CommunityResponse>   =apiService.listSchoolFeternity()

    override suspend fun get_login_user_data(jsonObject: JsonObject): Response<Loginmodel> =apiService.get_login_user_data(jsonObject)
    override suspend fun change_password(jsonObject: JsonObject): Response<Loginmodel> =apiService.change_password(jsonObject)

}