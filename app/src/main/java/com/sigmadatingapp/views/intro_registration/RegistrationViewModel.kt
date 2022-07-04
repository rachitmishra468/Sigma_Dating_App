package com.sigmadatingapp.views.intro_registration


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.sigmadatingapp.model.SchoolCommunityResponse
import com.sigmadatingapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public  class RegistrationViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    private val school_dataResponse = MutableLiveData<Resource<SchoolCommunityResponse>>()

    init {
        getSchoolingData()
    }

    var responseserver: MutableLiveData<Resource<SchoolCommunityResponse>>? = null
        get() = school_dataResponse


    fun getSchoolingData()  = viewModelScope.launch {
        school_dataResponse.postValue(Resource.loading(null))

        val jsonObject = JsonObject()
      //  jsonObject.addProperty("email", sharedPreferencesStorage.getString(AppConstants.email))

       // Log.d("TAG@123", jsonObject.toString())
        repository.ListSchoolFeternity().let {
            if (it.isSuccessful){
                school_dataResponse.postValue(Resource.success(it.body()))
            }else{
                school_dataResponse.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}