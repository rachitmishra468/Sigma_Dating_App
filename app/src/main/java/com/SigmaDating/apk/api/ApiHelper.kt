package com.SigmaDating.apk.api

import com.SigmaDating.apk.model.Loginmodel
import com.google.gson.JsonObject
import com.SigmaDating.apk.model.Forgotpassword
import com.SigmaDating.model.SchoolCommunityResponse
import retrofit2.Response


interface ApiHelper {

    suspend fun getEmployees():Response<Loginmodel>
    suspend fun user_login(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_register(jsonObject: JsonObject):Response<Loginmodel>

    suspend fun user_forgot_password(jsonObject: JsonObject):Response<Forgotpassword>

    suspend fun user_login_phone(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_phone_verifly(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun get_login_user_data(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun change_password(jsonObject: JsonObject):Response<Loginmodel>

    suspend fun getSchoolFeternityList():Response<SchoolCommunityResponse>

    suspend fun upload_images(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun delete_images(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun Update_profile(jsonObject: JsonObject):Response<Loginmodel>






}