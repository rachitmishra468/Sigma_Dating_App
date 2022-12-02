package com.SigmaDating.app.video

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SigmaDating.app.model.Token_data
import com.SigmaDating.app.repository.MainRepository
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.SigmaDating.app.views.Home
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingVideoCallViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {

    lateinit var ctrateToken_data: MutableLiveData<Resource<Token_data>>

    fun sendNotification(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "get user token call")
            mainRepository.sendNotification(id).let {
                if (it.isSuccessful) {
                    Log.d("TAG@123", "Notification sent")
                } else {
                    Log.d("TAG@123", "error in get token call ")

                }


            }
        }
    }

    fun get_User_video_token(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "get user token call")
            ctrateToken_data.postValue(Resource.loading(null))
            mainRepository.ctrateToken(id).let {
                if (it.isSuccessful) {


                    Resource.success(it.body()).data.let {
                        Home.mVideoGrant_user_token = it?.token.toString()
                        Log.d("TAG@123", "Token : " + Home.mCurrent_user_token)

                    }
                    ctrateToken_data.postValue(Resource.success(it.body()))
                } else {
                    Log.d("TAG@123", "error in get token call ")
                    ctrateToken_data.postValue(Resource.error(it.errorBody().toString(), null))
                }


            }
        }
    }


    fun sendChatNotification(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "Notification")
            mainRepository.sendNotification(id).let {
                if (it.isSuccessful) {
                    Log.d("TAG@123", "Video cut Notification sent")
                } else {
                    Log.d("TAG@123", "error in get send Chat Notification call ")

                }
            }
        }
    }



}