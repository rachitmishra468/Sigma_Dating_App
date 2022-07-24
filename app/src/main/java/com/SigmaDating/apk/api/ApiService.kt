package com.SigmaDating.apk.api


import com.SigmaDating.apk.model.Loginmodel
import com.google.gson.JsonObject
import com.SigmaDating.apk.model.Forgotpassword
import com.SigmaDating.apk.model.home_model
import com.SigmaDating.model.SchoolCommunityResponse
import retrofit2.Response
import retrofit2.http.*

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

    @POST("users/view")
    suspend fun get_login_user_data(@Body jsonObject: JsonObject): Response<Loginmodel>

    @POST("users/changepass")
    suspend fun change_password(@Body jsonObject: JsonObject): Response<Loginmodel>

    @GET("common/listschoolscommunity")
    suspend fun listSchoolFeternity():Response<SchoolCommunityResponse>

    @GET("common/{id}")
    suspend fun get_user_bids(@Path("id")id: String):Response<home_model>


    @POST("users/uploadphoto")
    suspend fun upload_images(@Body jsonObject: JsonObject): Response<Loginmodel>



    @POST("users/deletephoto")
    suspend fun delete_images(@Body jsonObject: JsonObject): Response<Loginmodel>



    @POST("users/editprofile")
    suspend fun Update_profile(@Body jsonObject: JsonObject): Response<Loginmodel>

   /* @POST("users/view")
    suspend fun getUserDashboard(@Body jsonObject: JsonObject): Response<UserDashboardModel>*/
}