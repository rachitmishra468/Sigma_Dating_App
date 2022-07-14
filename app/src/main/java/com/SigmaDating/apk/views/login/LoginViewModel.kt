package com.SigmaDating.apk.views.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.apk.repository.MainRepository
import com.google.gson.JsonObject
import com.SigmaDating.apk.model.Forgotpassword
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.storage.AppConstants.PHONE_LOGIN
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {



    var _res: MutableLiveData<Resource<Loginmodel>>? = null

    var sent_otp: MutableLiveData<Resource<Loginmodel>>? = null

    var verifly_otp: MutableLiveData<Resource<Loginmodel>>? = null

    var responsForgot : MutableLiveData<Resource<Forgotpassword>>? = null

    var registration: MutableLiveData<Resource<Loginmodel>>? = null


    init {
        _res = MutableLiveData<Resource<Loginmodel>>()
        sent_otp = MutableLiveData<Resource<Loginmodel>>()
        verifly_otp = MutableLiveData<Resource<Loginmodel>>()
        responsForgot = MutableLiveData<Resource<Forgotpassword>>()
        registration= MutableLiveData<Resource<Loginmodel>>()
    }





    fun User_1_login() = viewModelScope.launch {
        _res?.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        if (PHONE_LOGIN) {
            jsonObject.addProperty(
                "phone",
                sharedPreferencesStorage.getString(AppConstants.USER_COUNTRY_CODE) + "" + sharedPreferencesStorage.getString(
                    AppConstants.phone
                )
            )
        } else {
            jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))
            jsonObject.addProperty(
                "password",
                sharedPreferencesStorage.getString(AppConstants.password)
            )
            jsonObject.addProperty("device_token", "phoneNumber")
            jsonObject.addProperty("device_type", "phoneNumber")
            jsonObject.addProperty("phone", "")
        }

        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_login(jsonObject).let {
            if (it.isSuccessful) {
                _res?.postValue(Resource.success(it.body()))
            } else {
                _res?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun login_phone() = viewModelScope.launch {

        sent_otp?.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "phone",
            sharedPreferencesStorage.getString(AppConstants.USER_COUNTRY_CODE) + "" + sharedPreferencesStorage.getString(
                AppConstants.phone
            )
        )

        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_login_phone(jsonObject).let {
            if (it.isSuccessful) {
                sent_otp?.postValue(Resource.success(it.body()))
            } else {
                sent_otp?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }


    }
     fun User_forgot()  = viewModelScope.launch {
         responsForgot?.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))

        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_forgotpass(jsonObject).let {
            if (it.isSuccessful){
                responsForgot?.postValue(Resource.success(it.body()))
            }else{
                responsForgot?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }







    fun phone_verifly_OTP(OTP: String) = viewModelScope.launch {

        verifly_otp?.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "phone",
            sharedPreferencesStorage.getString(AppConstants.USER_COUNTRY_CODE) + "" + sharedPreferencesStorage.getString(
                AppConstants.phone
            )
        )
        jsonObject.addProperty("otp", OTP)
        Log.d("TAG@123", jsonObject.toString())
        mainRepository.user_phone_verifly(jsonObject).let {
            if (it.isSuccessful) {
                verifly_otp?.postValue(Resource.success(it.body()))
            } else {
                verifly_otp?.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }


    }




    fun Register() = viewModelScope.launch {
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
        jsonObject.addProperty("appleId", sharedPreferencesStorage.getString(AppConstants.appleId))
        jsonObject.addProperty("isSocialLogin",  sharedPreferencesStorage.getBoolean(AppConstants.isSocialLogin))
        jsonObject.addProperty("upload_image", "")
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



}