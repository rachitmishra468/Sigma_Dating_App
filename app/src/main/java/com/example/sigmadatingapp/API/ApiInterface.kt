package com.example.sigmadatingapp.API

import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface ApiInterface {

  /*  @POST("api/login")
    fun login(@Body jsonObject: JsonObject): Single<LoginResponse>

    @POST("api/otp-verify")
    fun otpVerify(@Body jsonObject: JsonObject): Single<LoginResponse>

    @GET("api/get-profile")
    fun getProfile(@Header("Authorization") token: String): Single<LoginResponse>

    @GET("api/get-profile/{input}")
    fun getProfile(@Header("Authorization") token: String,
                   @Path("input") id : Int ): Single<LoginResponse>

    @GET("api/get-users")
    fun getUser(@Header("Authorization") token: String): Single<UserResponse>

    @GET("api/get-meetings")
    fun getMeetings(@Header("Authorization") token: String): Single<MeetingResponse>

    @GET("api/pending-meetings")
    fun getPendingMeetings(@Header("Authorization") token: String): Single<MeetingResponse>

    @GET("api/sent-meetings")
    fun getSentMeetings(@Header("Authorization") token: String): Single<MeetingResponse>

    @GET("api/get-gallery")
    fun getGallery(@Header("Authorization") token: String): Single<GalleryResponse>

    @GET("api/get-gallery/{input}")
    fun getGallery(@Header("Authorization") token: String,
                   @Path("input") id : Int ): Single<GalleryResponse>

    @POST("api/update-profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Single<LoginResponse>

    @POST("api/get-users-search")
    fun getUserSearch(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Single<UserResponse>

    @POST("api/store-meeting")
    fun bookMeeting(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Single<UserResponse>

    @POST("api/action-meeting")
    fun actionMeeting(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Single<MeetingResponse>

    @POST("api/video-profile-update")
    @Multipart
    fun updateVideoProfile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Single<LoginResponse>

    @POST("api/update-gallery")
    @Multipart
    fun updateGallery(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Single<GalleryResponse>

    @POST("api/countries")
    fun getCountry(): Single<CountryResponse>

    @POST("api/states")
    fun getState(@Body jsonObject: JsonObject): Single<CountryResponse>

    @POST("api/cities")
    fun getCity(@Body jsonObject: JsonObject): Single<CountryResponse>

    @POST("api/initialise")
    fun getPaytmChecksum(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ): Single<PaytmTokenResponse>*/
}