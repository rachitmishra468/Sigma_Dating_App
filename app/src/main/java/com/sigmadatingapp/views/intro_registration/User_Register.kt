package com.sigmadatingapp.views.intro_registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Loginmodel
import com.sigmadatingapp.model.SchoolCommunityResponse
import com.sigmadatingapp.repository.MainRepository
import com.sigmadatingapp.storage.AppConstants
import com.sigmadatingapp.storage.SharedPreferencesStorage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class User_Register @Inject constructor(private val mainRepository: MainRepository, private val sharedPreferencesStorage: SharedPreferencesStorage) : ViewModel() {

    var registration: MutableLiveData<Resource<Loginmodel>>? = null
    var school_dataResponse : MutableLiveData<Resource<SchoolCommunityResponse>>?= null
    init {
        registration= MutableLiveData<Resource<Loginmodel>>()
        school_dataResponse= MutableLiveData<Resource<SchoolCommunityResponse>>()
       // Register()
    }

    fun Register(bitmap:String) = viewModelScope.launch {
        registration?.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))
        jsonObject.addProperty(
            "first_name",
            sharedPreferencesStorage.getString(AppConstants.fisrtname)
        )
        jsonObject.addProperty(
            "last_name",
            sharedPreferencesStorage.getString(AppConstants.Lastname)
        )
        jsonObject.addProperty(
            "password",
            sharedPreferencesStorage.getString(AppConstants.password)
        )
        jsonObject.addProperty(
            "password_confirm",
            sharedPreferencesStorage.getString(AppConstants.password)
        )

        jsonObject.addProperty(
            "phone",
            sharedPreferencesStorage.getString(AppConstants.phone)
            )


        jsonObject.addProperty(
            "location",
            sharedPreferencesStorage.getString(AppConstants.location)
        )
        jsonObject.addProperty("dob", sharedPreferencesStorage.getString(AppConstants.Dob))
        jsonObject.addProperty("gender", sharedPreferencesStorage.getString(AppConstants.gender))
        jsonObject.addProperty("university", sharedPreferencesStorage.getString(AppConstants.university))
        jsonObject.addProperty("community", sharedPreferencesStorage.getString(AppConstants.community))
        jsonObject.addProperty("interests", "")
        jsonObject.addProperty("interested_in", "")
        jsonObject.addProperty("facebookId", "")
        jsonObject.addProperty("appleId", "")
        jsonObject.addProperty("isSocialLogin", "")
        jsonObject.addProperty("upload_image", bitmap)
        jsonObject.addProperty("device_token", "")
        jsonObject.addProperty("device_type", "Android")
        Log.d("TAG@123", "414"+jsonObject.toString())
        mainRepository.user_register(jsonObject).let {
            if (it.isSuccessful) {
                registration?.postValue(Resource.success(it.body()))
            } else {
                registration?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }




    fun getSchoolingData()  = viewModelScope.launch {
        school_dataResponse?.postValue(Resource.loading(null))
        mainRepository.ListSchoolFeternity().let {
            if (it.isSuccessful){
                school_dataResponse?.postValue(Resource.success(it.body()))
            }else{
                school_dataResponse?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


}