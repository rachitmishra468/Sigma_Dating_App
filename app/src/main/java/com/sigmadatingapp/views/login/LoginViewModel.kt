package com.sigmadatingapp.views.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.sigmadatingapp.module.Loginmodel
import com.sigmadatingapp.repository.MainRepository
import com.google.gson.JsonObject
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel   @Inject constructor(private val mainRepository: MainRepository,  private val sharedPreferencesStorage: SharedPreferencesStorage):ViewModel(){

    private val _res = MutableLiveData<Resource<Loginmodel>>()

    var res : LiveData<Resource<Loginmodel>>? = null
        get() = _res

    init {
        User_1_login()
    }


    fun User_login(){

    }


    private fun User_1_login()  = viewModelScope.launch {
        _res.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))
        jsonObject.addProperty("password", sharedPreferencesStorage.getString(AppConstants.password))
        jsonObject.addProperty("device_token", "phoneNumber")
        jsonObject.addProperty("device_type", "phoneNumber")
        jsonObject.addProperty("phone", "")
        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_login(jsonObject).let {
            if (it.isSuccessful){
                _res.postValue(Resource.success(it.body()))
            }else{
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}