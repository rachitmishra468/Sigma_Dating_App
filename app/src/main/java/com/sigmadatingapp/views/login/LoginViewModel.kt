package com.sigmadatingapp.views.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.sigmadatingapp.model.Loginmodel
import com.sigmadatingapp.repository.MainRepository
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Forgotpassword
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel   @Inject constructor(private val mainRepository: MainRepository,  private val sharedPreferencesStorage: SharedPreferencesStorage):ViewModel(){

    private val _res = MutableLiveData<Resource<Loginmodel>>()
    private val _forgotResponse = MutableLiveData<Resource<Forgotpassword>>()

    var res : LiveData<Resource<Loginmodel>>? = null
        get() = _res

    var responsForgot : LiveData<Resource<Forgotpassword>>? = null
        get() = _forgotResponse


    init {
        User_1_login()
        User_forgot()
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
    private fun User_forgot()  = viewModelScope.launch {
        _forgotResponse.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))

        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_forgotpass(jsonObject).let {
            if (it.isSuccessful){
                _forgotResponse.postValue(Resource.success(it.body()))
            }else{
                _forgotResponse.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }






}