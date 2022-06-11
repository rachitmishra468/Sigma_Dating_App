package com.example.sigmadatingapp.views.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.example.sigmadatingapp.module.Loginmodel
import com.example.sigmadatingapp.repository.MainRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel   @Inject constructor(
    private val mainRepository: MainRepository
):ViewModel(){

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
        jsonObject.addProperty("email", "phoneNumber")
        jsonObject.addProperty("password", "phoneNumber")
        jsonObject.addProperty("device_token", "phoneNumber")
        jsonObject.addProperty("device_type", "phoneNumber")
        jsonObject.addProperty("phone", "")
        mainRepository.user_login(jsonObject).let {
            if (it.isSuccessful){
                _res.postValue(Resource.success(it.body()))
            }else{
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}