package com.SigmaDating.apk.repository

import com.SigmaDating.apk.api.ApiHelper
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper
){

    suspend fun getEmployee() = apiHelper.getEmployees()
    suspend fun user_login(jsonObject: JsonObject) = apiHelper.user_login(jsonObject)
    suspend fun user_register(jsonObject: JsonObject) = apiHelper.user_register(jsonObject)
    suspend fun user_forgotpass(jsonObject: JsonObject) = apiHelper.user_forgot_password(jsonObject)
    suspend fun user_login_phone(jsonObject: JsonObject) = apiHelper.user_login_phone(jsonObject)
    suspend fun user_phone_verifly(jsonObject: JsonObject) = apiHelper.user_phone_verifly(jsonObject)
    suspend fun get_profile_swipe_details(jsonObject: JsonObject) = apiHelper.get_profile_swipe_details(jsonObject)
    suspend fun report_user(jsonObject: JsonObject) = apiHelper.report_user(jsonObject)
    suspend fun block_user(jsonObject: JsonObject) = apiHelper.block_user(jsonObject)

    suspend fun  ListSchoolFeternity()=apiHelper.getSchoolFeternityList()

    suspend fun get_setting_update_details(jsonObject: JsonObject) = apiHelper.get_setting_update_details(jsonObject)
    suspend fun get_login_user_data(jsonObject: JsonObject) = apiHelper.get_login_user_data(jsonObject)
    suspend fun change_password(jsonObject: JsonObject)=apiHelper.change_password(jsonObject)
    suspend fun User_delete_account(jsonObject: JsonObject)=apiHelper.User_delete_account(jsonObject)

    suspend fun upload_images(jsonObject: JsonObject)=apiHelper.upload_images(jsonObject)
    suspend fun delete_images(jsonObject: JsonObject)=apiHelper.delete_images(jsonObject)
    suspend fun Update_profile(jsonObject: JsonObject)=apiHelper.Update_profile(jsonObject)
    suspend fun get_user_bids(id: String)= apiHelper.get_user_bids(id)
    //suspend fun create_post(jsonObject: JsonObject)=apiHelper.create_post(jsonObject)
    suspend fun create_post(user_id:RequestBody,title:RequestBody,discription:RequestBody,tag_users:RequestBody,file: MultipartBody.Part)=apiHelper.create_post(user_id,title,discription,tag_users,file)
    suspend fun deletepost(jsonObject: JsonObject)=apiHelper.deletepost(jsonObject)

    suspend fun showmyposts(jsonObject: JsonObject)=apiHelper.showmyposts(jsonObject)

    suspend fun get_user_match_bids(id: String)= apiHelper.get_user_match_bids(id)

    //suspend fun getUserDashboardData(jsonObject: JsonObject)=apiHelper.getUserDashboardProfile(jsonObject)

}