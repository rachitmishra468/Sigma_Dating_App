package com.example.demoapp.api

import com.example.sigmadatingapp.api.ApiService
import com.example.sigmadatingapp.module.Loginmodel
import com.google.gson.JsonObject
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun getEmployees(): Response<Loginmodel> = apiService.getEmployees()
    override suspend fun user_login(jsonObject: JsonObject): Response<Loginmodel> =apiService.login(jsonObject)

}