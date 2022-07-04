package com.sigmadatingapp.views.intro_registration

import School_CommunityResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.sigmadatingapp.model.Forgotpassword
import com.sigmadatingapp.repository.MainRepository
import com.sigmadatingapp.storage.AppConstants
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    private val school_dataResponse = MutableLiveData<Resource<School_CommunityResponse>>()

    init {
        getSchoolingData()
    }

    var responseserver: MutableLiveData<Resource<School_CommunityResponse>>? = null
        get() = school_dataResponse


    fun getSchoolingData()  = viewModelScope.launch {
        responseserver!!.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
      //  jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))

       // Log.d("TAG@123", jsonObject.toString())
        repository.ListSchoolFeternity().let {
            if (it.isSuccessful){
                responseserver!!.postValue(Resource.success(it.body()))
            }else{
                responseserver!!.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}