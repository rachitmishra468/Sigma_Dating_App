package com.SigmaDating.app.api


import com.SigmaDating.app.model.*
import com.google.gson.JsonObject
import com.SigmaDating.model.SchoolCommunityResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

import retrofit2.http.POST

import retrofit2.http.Multipart




interface ApiService {

    @GET("employees")
    suspend fun getEmployees(): Response<Loginmodel>

    @POST("users/login")
    suspend fun login(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/register")
    suspend fun register(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/forgotpassword")
    suspend fun forgotpass(@Body jsonObject: JsonObject): Response<Forgotpassword>

    @POST("twilio/sendotp")
    suspend fun user_login_phone(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("twilio/verifyotp")
    suspend fun user_phone_verifly(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/emailused")
    suspend fun email_validation_check(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/view")
    suspend fun get_login_user_data(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/changepass")
    suspend fun change_password(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/deleteaccount")
    suspend fun User_delete_account(@Body jsonObject: JsonObject): Response<Loginmodel>

    @GET("common/listschoolscommunity")
    suspend fun listSchoolFeternity(): Response<SchoolCommunityResponse>

    @POST("users/subscribe_membership")
    suspend fun PostSubscriptionPlansdata(@Body jsonObject: JsonObject): Response<SubscriptionPlanData>

    @GET("users/plans")
    suspend fun getSubscriptionPlanslist(): Response<SubscriptionPlanData>

    @POST("common/ads")
    suspend fun listads(@Body jsonObject: JsonObject): Response<advertisingData>

    @GET("common/{id}")
    suspend fun get_user_bids(@Path("id") id: String): Response<home_model>

    @POST("users/uploadphoto")
    suspend fun upload_images(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/deletephoto")
    suspend fun delete_images(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("dating/reportuser")
    suspend fun report_user(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("dating/blockuser")
    suspend fun block_user(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/editsettings")
    suspend fun get_setting_update_details(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/editprofile")
    suspend fun Update_profile(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("dating/doswipe")
    suspend fun get_profile_swipe_details(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("post/deletepost")
    suspend fun deletepost(@Body jsonObject: JsonObject): Response<delelepost>

    @POST("post/changestatus")
    suspend fun PostStatusChange(@Body jsonObject: JsonObject): Response<delelepost>

    @POST("post/showmyposts")
    suspend fun showmyposts(@Body jsonObject: JsonObject): Response<post>

    @Multipart
    @POST("post/save")
    suspend fun create_post(
        @Part("isPrivate") isPrivate: RequestBody,
        @Part("user_id") user_id: RequestBody,
                             @Part("location") location: RequestBody,
                          @Part("title") title: RequestBody, @Part("description") description: RequestBody,
                             @Part("tag_users") tag_users: RequestBody,
                          @Part file: MultipartBody.Part?
    ) : Response<Loginmodel>



    @GET("dating/showmymatches/{user_id}")
    suspend fun get_user_match_bids(@Path("user_id") id: String): Response<Match_bids>



    @POST("twilio/accesstoken")
    suspend fun ctrateToken(@Body jsonObject: JsonObject): Response<Token_data>



    @POST("twilio/notifyuser")
    suspend fun sendNotification(@Body jsonObject: JsonObject): Response<Token_data>

    @POST("post/getpostcomments")
    suspend fun getallcomment(@Body jsonObject: JsonObject): Response<Comment_model>

    @POST("post/savecomment")
    suspend fun sent_comment(@Body jsonObject: JsonObject): Response<Loginmodel>


    @POST("post/likepost")
    suspend fun save_like_post_data(@Body jsonObject: JsonObject): Response<Loginmodel>


    @GET("notification/index/{user_id}")
    suspend fun get_notification(@Path("user_id") id: String): Response<Notification_model>

    @POST("notification/deletenotification")
    suspend fun deletenotification(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/sendotpemail")
    suspend fun email_otp_send(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/verifyotpemail")
    suspend fun email_otp_verification(@Body jsonObject: JsonObject): Response<Loginmodel>


}