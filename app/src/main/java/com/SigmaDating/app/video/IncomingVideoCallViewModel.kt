package com.SigmaDating.app.video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SigmaDating.app.repository.MainRepository
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingVideoCallViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {

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

}