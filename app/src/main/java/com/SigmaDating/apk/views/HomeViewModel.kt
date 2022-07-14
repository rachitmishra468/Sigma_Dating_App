package com.SigmaDating.apk.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.SigmaDating.apk.model.Loginmodel
import com.SigmaDating.model.SchoolCommunityResponse
import com.SigmaDating.apk.repository.MainRepository
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val mainRepository: MainRepository, private val sharedPreferencesStorage: SharedPreferencesStorage) : ViewModel() {

    lateinit var get_user_data: MutableLiveData<Resource<Loginmodel>>
    lateinit var change_password: MutableLiveData<Resource<Loginmodel>>
    lateinit var upload_images: MutableLiveData<Resource<Loginmodel>>
    lateinit var delete_images: MutableLiveData<Resource<Loginmodel>>
    lateinit var school_dataResponse : MutableLiveData<Resource<SchoolCommunityResponse>>
    lateinit var update_profile: MutableLiveData<Resource<Loginmodel>>

    init {
        get_user_data= MutableLiveData<Resource<Loginmodel>>()
        change_password=MutableLiveData<Resource<Loginmodel>>()
        upload_images=MutableLiveData<Resource<Loginmodel>>()
        delete_images=MutableLiveData<Resource<Loginmodel>>()
        school_dataResponse= MutableLiveData<Resource<SchoolCommunityResponse>>()
        update_profile= MutableLiveData<Resource<Loginmodel>>()
    }

    fun get_Login_User_details(id:String) = viewModelScope.launch {
        get_user_data.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123",id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", "22 : -"+jsonObject.toString())
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
        Log.d("TAG@123", "-- "+jsonObject.toString())
        mainRepository.change_password(jsonObject).let {
            if (it.isSuccessful) {
                change_password.postValue(Resource.success(it.body()))
            } else {
                change_password.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }



    fun User_upload_images(id:String,img:String) = viewModelScope.launch {
        upload_images.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123",id)
        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("upload_image", img)

        Log.d("TAG@123", "-- "+jsonObject.toString())
        mainRepository.upload_images(jsonObject).let {
            if (it.isSuccessful) {
                upload_images.postValue(Resource.success(it.body()))
            } else {
                upload_images.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun User_delete_images(id:String,img:String) = viewModelScope.launch {
        delete_images.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123",id)
        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("photo", img)

        Log.d("TAG@123", "-- "+jsonObject.toString())
        mainRepository.delete_images(jsonObject).let {
            if (it.isSuccessful) {
                delete_images.postValue(Resource.success(it.body()))
            } else {
                delete_images.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun getSchoolingData()  = viewModelScope.launch {
        school_dataResponse.postValue(Resource.loading(null))
        mainRepository.ListSchoolFeternity().let {
            if (it.isSuccessful){
                school_dataResponse.postValue(Resource.success(it.body()))
            }else{
                school_dataResponse.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun update_profile(id:String, university:String,community :String,interests:String,about:String) = viewModelScope.launch {
        update_profile.postValue(Resource.loading(null))
        val jsonObject = JsonObject()

        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("university", university)
        jsonObject.addProperty("community", community)
        jsonObject.addProperty("interests", interests)
        jsonObject.addProperty("about", about)

        Log.d("TAG@123",jsonObject.toString())
        mainRepository.Update_profile(jsonObject).let {
            if (it.isSuccessful){
                update_profile.postValue(Resource.success(it.body()))
            }else{
                update_profile.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}