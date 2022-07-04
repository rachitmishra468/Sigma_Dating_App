package com.sigmadatingapp.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Loginmodel
import com.sigmadatingapp.repository.MainRepository
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val mainRepository: MainRepository, private val sharedPreferencesStorage: SharedPreferencesStorage) : ViewModel() {

    lateinit var get_user_data: MutableLiveData<Resource<Loginmodel>>
    lateinit var change_password: MutableLiveData<Resource<Loginmodel>>

    init {
        get_user_data= MutableLiveData<Resource<Loginmodel>>()
        change_password=MutableLiveData<Resource<Loginmodel>>()
    }

    fun get_Login_User_details(id:String) = viewModelScope.launch {
        get_user_data.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123",id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", jsonObject.toString())
        mainRepository.get_login_user_data(jsonObject).let {
            if (it.isSuccessful) {
                get_user_data.postValue(Resource.success(it.body()))
            } else {
                get_user_data.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun User_change_password(id:String,password:String,password_confirm:String) = viewModelScope.launch {
        change_password.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123",id)
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("password", password)
        jsonObject.addProperty("password_confirm", password_confirm)
        Log.d("TAG@123", jsonObject.toString())
        mainRepository.change_password(jsonObject).let {
            if (it.isSuccessful) {
                change_password.postValue(Resource.success(it.body()))
            } else {
                change_password.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


}