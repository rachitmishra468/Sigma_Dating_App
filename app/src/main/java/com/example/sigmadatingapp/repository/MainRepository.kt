package com.example.sigmadatingapp.repository

import com.example.sigmadatingapp.api.ApiInterface
import com.google.gson.JsonObject
import javax.inject.Inject

class MainRepository


    @Inject
    constructor(private val apiHelper: ApiInterface){


    fun login(jsonObject: JsonObject) = apiHelper.login(jsonObject)
   /* fun otpVerify(jsonObject: JsonObject) = apiHelper.otpVerify(jsonObject)
    fun getProfile(token : String) = apiHelper.getProfile(token)
    fun getProfile(token : String, id : Int) = apiHelper.getProfile(token, id)
    fun getUser(token : String) = apiHelper.getUser(token)
    fun getMeetings(token : String) = apiHelper.getMeetings(token)
    fun getPendingMeetings(token : String) = apiHelper.getPendingMeetings(token)
    fun getSentMeetings(token : String) = apiHelper.getSentMeetings(token)
    fun getGallery(token : String) = apiHelper.getGallery(token)
    fun getGallery(token : String, id : Int) = apiHelper.getGallery(token, id)
    fun bookMeeting(token : String, jsonObject: JsonObject) =
        apiHelper.bookMeeting(token, jsonObject)
    fun actionMeeting(token : String, jsonObject: JsonObject) =
        apiHelper.actionMeeting(token, jsonObject)
    fun getUserSearch(token : String, jsonObject: JsonObject) =
        apiHelper.getUserSearch(token, jsonObject)
    fun updateProfile(token : String, jsonObject: JsonObject) =
        apiHelper.updateProfile(token, jsonObject)
    fun updateVideoProfile(token : String, file: MultipartBody.Part) =
        apiHelper.updateVideoProfile(token, file)
    fun updategallery(token : String, file: MultipartBody.Part) =
        apiHelper.updateGallery(token, file)
    fun getCountry() = apiHelper.getCountry()
    fun getState(jsonObject: JsonObject) = apiHelper.getState(jsonObject)
    fun getCity(jsonObject: JsonObject) = apiHelper.getCity(jsonObject)
    fun getPaytmChecksum(token : String, jsonObject: JsonObject) =
        apiHelper.getPaytmChecksum(token, jsonObject)*/
}