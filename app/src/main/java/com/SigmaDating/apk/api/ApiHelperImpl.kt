package com.example.demoapp.api

import com.SigmaDating.apk.api.ApiService
import com.google.gson.JsonObject
import com.SigmaDating.apk.api.ApiHelper
import com.SigmaDating.apk.model.*
import com.SigmaDating.model.SchoolCommunityResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun getEmployees(): Response<Loginmodel> = apiService.getEmployees()
    override suspend fun user_login(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.login(jsonObject)

    override suspend fun user_register(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.register(jsonObject)

    override suspend fun get_setting_update_details(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.get_setting_update_details(jsonObject)



    override suspend fun user_forgot_password(jsonObject: JsonObject): Response<Forgotpassword> =
        apiService.forgotpass(jsonObject)

    override suspend fun user_login_phone(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.user_login_phone(jsonObject)

    override suspend fun user_phone_verifly(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.user_phone_verifly(jsonObject)

    override suspend fun get_profile_swipe_details(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.get_profile_swipe_details(jsonObject)


    override suspend fun getSchoolFeternityList(): Response<SchoolCommunityResponse> =
        apiService.listSchoolFeternity()

    override suspend fun upload_images(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.upload_images(jsonObject)

    override suspend fun get_login_user_data(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.get_login_user_data(jsonObject)

    override suspend fun change_password(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.change_password(jsonObject)

    override suspend fun User_delete_account(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.User_delete_account(jsonObject)


    override suspend fun delete_images(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.delete_images(jsonObject)

    override suspend fun Update_profile(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.Update_profile(jsonObject)

    override suspend fun get_user_bids(id: String): Response<home_model> =
        apiService.get_user_bids(id)

    override suspend fun report_user(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.report_user(jsonObject)
    override suspend fun block_user(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.block_user(jsonObject)

    override suspend fun create_post(user_id:RequestBody,title:RequestBody,discription:RequestBody,tag_users:RequestBody, file: MultipartBody.Part): Response<Loginmodel> =
        apiService.create_post(user_id,title,discription,tag_users,file)

    override suspend fun deletepost(jsonObject: JsonObject): Response<Loginmodel> =
        apiService.deletepost(jsonObject)

    override suspend fun showmyposts(jsonObject: JsonObject): Response<post> =
        apiService.showmyposts(jsonObject)


    override suspend fun get_user_match_bids(id: String): Response<Match_bids> =
        apiService.get_user_match_bids(id)



    //  override suspend fun getUserDashboardProfile(id: JsonObject): Response<Loginmodel> =apiService.getUserDashboard(id)


}