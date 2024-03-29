package com.SigmaDating.app.api

import com.SigmaDating.app.model.*
import com.google.gson.JsonObject
import com.SigmaDating.model.SchoolCommunityResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiHelper {

    suspend fun getEmployees(): Response<Loginmodel>
    suspend fun user_login(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun user_register(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun get_setting_update_details(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun user_forgot_password(jsonObject: JsonObject): Response<Forgotpassword>

    suspend fun user_login_phone(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun email_otp_send(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun email_otp_verification(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun user_phone_verifly(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun email_validation_check(jsonObject: JsonObject): Response<Loginmodel>


    suspend fun get_profile_swipe_details(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun report_user(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun block_user(jsonObject: JsonObject): Response<Loginmodel>

    suspend fun get_login_user_data(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun change_password(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun User_delete_account(jsonObject: JsonObject): Response<Loginmodel>

    suspend fun getSchoolFeternityList(): Response<SchoolCommunityResponse>

    suspend fun getlistads(jsonObject: JsonObject): Response<advertisingData>

    suspend fun getSubscriptionPlanslist(): Response<SubscriptionPlanData>
    suspend fun contact_info(): Response<contactinfoModel>

    suspend fun PostSubscriptionPlansdata(jsonObject: JsonObject): Response<SubscriptionPlanData>

    suspend fun upload_images(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun delete_images(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun changeDefaultPhoto(jsonObject: JsonObject): Response<Loginmodel>

    suspend fun Update_profile(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun get_user_bids(jsonObject: String): Response<home_model>
    suspend fun create_post(
        isPrivate: RequestBody,
        user_id: RequestBody,
        location: RequestBody,
        title: RequestBody,
        discription: RequestBody,
        tag_users: RequestBody,
        file: MultipartBody.Part
    ): Response<Loginmodel>

    suspend fun deletepost(jsonObject: JsonObject): Response<delelepost>
    suspend fun PostStatusChange(jsonObject: JsonObject): Response<delelepost>
    suspend fun showmyposts(jsonObject: JsonObject): Response<post>
    suspend fun showmylikeposts(jsonObject: JsonObject): Response<post>
    suspend fun getallcomment(jsonObject: JsonObject): Response<Comment_model>
    suspend fun get_user_match_bids(jsonObject: String): Response<Match_bids>
    suspend fun ctrateToken(jsonObject: JsonObject): Response<Token_data>
    suspend fun sendNotification(jsonObject: JsonObject): Response<Token_data>
    suspend fun sent_comment(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun save_like_post_data(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun get_notification(jsonObject: String): Response<Notification_model>
    suspend fun deletenotification(jsonObject: JsonObject): Response<Loginmodel>
    //suspend fun getUserDashboardProfile(jsonObject: JsonObject):Response<Loginmodel>
    suspend fun contact_form(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun post_users_updatecontacts(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun updateigauthtoken(jsonObject: JsonObject): Response<Loginmodel>
    suspend fun updatefbauthtoken(jsonObject: JsonObject): Response<Loginmodel>



}