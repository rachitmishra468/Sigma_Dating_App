package com.SigmaDating.apk.api

import com.SigmaDating.apk.model.Loginmodel
import com.google.gson.JsonObject
import com.SigmaDating.apk.model.Forgotpassword
import com.SigmaDating.apk.model.home_model
import com.SigmaDating.apk.model.post
import com.SigmaDating.model.SchoolCommunityResponse
import okhttp3.MultipartBody
import retrofit2.Response


interface ApiHelper {

    suspend fun getEmployees():Response<Loginmodel>
    suspend fun user_login(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_register(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun get_setting_update_details(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_forgot_password(jsonObject: JsonObject):Response<Forgotpassword>

    suspend fun user_login_phone(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun user_phone_verifly(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun get_profile_swipe_details(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun report_user(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun block_user(jsonObject: JsonObject):Response<Loginmodel>

    suspend fun get_login_user_data(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun change_password(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun User_delete_account(jsonObject: JsonObject):Response<Loginmodel>

    suspend fun getSchoolFeternityList():Response<SchoolCommunityResponse>

    suspend fun upload_images(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun delete_images(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun Update_profile(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun get_user_bids(jsonObject: String):Response<home_model>
    suspend fun create_post(user_id:String,title:String,discription:String,file: MultipartBody.Part):Response<Loginmodel>
    suspend fun deletepost(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun showmyposts(jsonObject: JsonObject):Response<post>

//suspend fun getUserDashboardProfile(jsonObject: JsonObject):Response<Loginmodel>




}