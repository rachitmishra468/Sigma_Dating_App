package com.SigmaDating.apk.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SigmaDating.apk.model.*
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.SigmaDating.model.SchoolCommunityResponse
import com.SigmaDating.apk.repository.MainRepository
import com.SigmaDating.apk.storage.AppConstants
import com.SigmaDating.apk.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

import org.jetbrains.anko.custom.async
import java.io.File
import javax.inject.Inject





@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {


    lateinit var report_block_user: MutableLiveData<Resource<Loginmodel>>
   lateinit var  setting_update_details: MutableLiveData<Resource<Loginmodel>>
    lateinit var profile_swipe: MutableLiveData<Resource<Loginmodel>>
    val get_user_data: MutableLiveData<Resource<Loginmodel>>
    lateinit var get_user_edit_user: MutableLiveData<Resource<Loginmodel>>
    val get_secound_feb_data: MutableLiveData<Resource<Loginmodel>>
    val get_UserReportdata: MutableLiveData<Resource<Loginmodel>>
    val change_password: MutableLiveData<Resource<Loginmodel>>
    lateinit var upload_images: MutableLiveData<Resource<Loginmodel>>
    lateinit var delete_images: MutableLiveData<Resource<Loginmodel>>
    lateinit var school_dataResponse: MutableLiveData<Resource<SchoolCommunityResponse>>
    lateinit var update_profile: MutableLiveData<Resource<Loginmodel>>
    lateinit var create_post: MutableLiveData<Resource<Loginmodel>>
    lateinit var delete_post: MutableLiveData<Resource<delelepost>>
    lateinit var All_post: MutableLiveData<Resource<post>>
    lateinit var all_match_bids: MutableLiveData<Resource<Match_bids>>
    val user_bids: MutableLiveData<Resource<home_model>>

    init {
        report_block_user=MutableLiveData<Resource<Loginmodel>>()
       // profile_swipe=MutableLiveData<Resource<Loginmodel>>()
        get_UserReportdata=MutableLiveData<Resource<Loginmodel>>()
        get_user_data = MutableLiveData<Resource<Loginmodel>>()
        get_secound_feb_data= MutableLiveData<Resource<Loginmodel>>()
        change_password = MutableLiveData<Resource<Loginmodel>>()
         user_bids = MutableLiveData<Resource<home_model>>()
    }

    fun get_home_feb_data(id: String){
        val one = async {  get_Login_User_details(id) }
        val two = async { get_Login_User_bids(id) }
    }





    fun get_user_match_bids(id:String) = viewModelScope.launch {
        all_match_bids.postValue(Resource.loading(null))
        mainRepository.get_user_match_bids(id).let {
            if (it.isSuccessful) {
                all_match_bids.postValue(Resource.success(it.body()))
            } else {
                all_match_bids.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun getAllPost(jsonObject:JsonObject) = viewModelScope.launch {
        All_post.postValue(Resource.loading(null))
        mainRepository.showmyposts(jsonObject).let {
            if (it.isSuccessful) {
                All_post.postValue(Resource.success(it.body()))
            } else {
                All_post.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun create_post(file:File,jsonObject:HashMap<String,String>) = viewModelScope.launch {
        Log.d("TAG@123","images path : "+file.absolutePath)
        Log.d("TAG@123","other data : "+jsonObject)
        Log.d(
            "TAG@123","tag_users : "+ jsonObject.get("tag_users")!!
        )

        val profileImage: RequestBody = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            file
        )

        val profileImageBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "media",
                file.getName(), profileImage
            )

        val id: RequestBody = jsonObject.get("user_id")!!.toRequestBody("text/plain".toMediaType())
        val title: RequestBody = jsonObject.get("title")!!.toRequestBody("text/plain".toMediaType())
        val tag_users: RequestBody = jsonObject.get("tag_users")!!.toRequestBody("text/plain".toMediaType())
        val description: RequestBody = jsonObject.get("description")!!.toRequestBody("text/plain".toMediaType())
        create_post.postValue(Resource.loading(null))
        mainRepository.create_post(id,title,description,tag_users
            ,profileImageBody).let {
            if (it.isSuccessful) {
                create_post.postValue(Resource.success(it.body()))
            } else {
                create_post.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun deletepost(jsonObject:JsonObject) = viewModelScope.launch {
        delete_post.postValue(Resource.loading(null))
        mainRepository.deletepost(jsonObject).let {
            if (it.isSuccessful) {
                delete_post.postValue(Resource.success(it.body()))
            } else {
                delete_post.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun report_user(jsonObject:JsonObject) = viewModelScope.launch {
        report_block_user.postValue(Resource.loading(null))
        mainRepository.report_user(jsonObject).let {
            if (it.isSuccessful) {
                report_block_user.postValue(Resource.success(it.body()))
            } else {
                report_block_user.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun block_user(jsonObject:JsonObject) = viewModelScope.launch {
        report_block_user.postValue(Resource.loading(null))
        mainRepository.block_user(jsonObject).let {
            if (it.isSuccessful) {
                report_block_user.postValue(Resource.success(it.body()))
            } else {
                report_block_user.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun profile_swipe_details(jsonObject:JsonObject) = viewModelScope.launch {
        profile_swipe.postValue(Resource.loading(null))
        mainRepository.get_profile_swipe_details(jsonObject).let {
            if (it.isSuccessful) {
                profile_swipe.postValue(Resource.success(it.body()))
            } else {
                profile_swipe.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }





    fun get_setting_update_details(jsonObject:JsonObject) = viewModelScope.launch {
        setting_update_details.postValue(Resource.loading(null))
        mainRepository.get_setting_update_details(jsonObject).let {
            if (it.isSuccessful) {
                setting_update_details.postValue(Resource.success(it.body()))
            } else {
                setting_update_details.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }




    fun get_Login_User_bids(id: String)= viewModelScope.launch {
        user_bids.postValue(Resource.loading(null))
        Log.d("TAG@123", "get_Login_User_bids")
        mainRepository.get_user_bids(id).let {
            if (it.isSuccessful) {
                Log.d("TAG@123", "get_Login_User_bids  isSuccessful")
                user_bids.postValue(Resource.success(it.body()))
            } else {
                Log.d("TAG@123", "get_Login_User_bids  isSuccessful false")
                user_bids.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun get_Edit_User_details(id: String) = viewModelScope.launch {
        get_user_edit_user.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", "22 : -" + jsonObject.toString())
        mainRepository.get_login_user_data(jsonObject).let {
            if (it.isSuccessful) {
                get_user_edit_user.postValue(Resource.success(it.body()))
            } else {
                get_user_edit_user.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun get_Login_User_details(id: String) = viewModelScope.launch {
        get_user_data.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", "22 : -" + jsonObject.toString())
        mainRepository.get_login_user_data(jsonObject).let {
            if (it.isSuccessful) {
                get_user_data.postValue(Resource.success(it.body()))
            } else {
                get_user_data.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }



    fun get_secound_feb_User_details(id: String) = GlobalScope.launch {
        get_secound_feb_data.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", "22 : -" + jsonObject.toString())
        mainRepository.get_login_user_data(jsonObject).let {
            if (it.isSuccessful) {
                get_secound_feb_data.postValue(Resource.success(it.body()))
            } else {
                get_secound_feb_data.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun get_UserReport_details(id: String) = GlobalScope.launch {
        get_UserReportdata.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        Log.d("TAG@123", "22 : -" + jsonObject.toString())
        mainRepository.get_login_user_data(jsonObject).let {
            if (it.isSuccessful) {
                get_UserReportdata.postValue(Resource.success(it.body()))
            } else {
                get_UserReportdata.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun User_delete_account(id: String, password: String) =
        viewModelScope.launch {
            change_password.postValue(Resource.loading(null))
            val jsonObject = JsonObject()
            Log.d("TAG@123", id)
            jsonObject.addProperty("id", id)
            jsonObject.addProperty("password", password)
            Log.d("TAG@123", "-- " + jsonObject.toString())
            mainRepository.User_delete_account(jsonObject).let {
                if (it.isSuccessful) {
                    change_password.postValue(Resource.success(it.body()))
                } else {
                    change_password.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }



    fun User_change_password(id: String, password: String, password_confirm: String) =
        viewModelScope.launch {
            change_password.postValue(Resource.loading(null))
            val jsonObject = JsonObject()
            Log.d("TAG@123", id)
            jsonObject.addProperty("id", id)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("password_confirm", password_confirm)
            Log.d("TAG@123", "-- " + jsonObject.toString())
            mainRepository.change_password(jsonObject).let {
                if (it.isSuccessful) {
                    change_password.postValue(Resource.success(it.body()))
                } else {
                    change_password.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }


    fun get_edit_page_data(id: String) {
        val one = async { getSchoolingData() }
    }

    fun Update_edit_page_data(
        id: String,
        university: String,
        community: String,
        interests: String,
        about: String
    ) {
        val one = async {
            update_profile(id, university, community, interests, about) }
       // val two = async { get_Login_User_details(id) }


    }



    fun User_upload_images(id: String, img: String) = viewModelScope.launch {
        upload_images.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("upload_image", img)

        Log.d("TAG@123", "-- " + jsonObject.toString())
        mainRepository.upload_images(jsonObject).let {
            if (it.isSuccessful) {
                upload_images.postValue(Resource.success(it.body()))
            } else {
                upload_images.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun User_delete_images(id: String, img: String) = viewModelScope.launch {
        delete_images.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        Log.d("TAG@123", id)
        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("photo", img)

        Log.d("TAG@123", "-- " + jsonObject.toString())
        mainRepository.delete_images(jsonObject).let {
            if (it.isSuccessful) {
                delete_images.postValue(Resource.success(it.body()))
            } else {
                delete_images.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun getSchoolingData() = viewModelScope.launch {
        school_dataResponse.postValue(Resource.loading(null))
        mainRepository.ListSchoolFeternity().let {
            if (it.isSuccessful) {
                school_dataResponse.postValue(Resource.success(it.body()))
            } else {
                school_dataResponse.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


    fun update_profile(
        id: String,
        university: String,
        community: String,
        interests: String,
        about: String
    ) = viewModelScope.launch {
        update_profile.postValue(Resource.loading(null))
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty("university", university)
        jsonObject.addProperty("community", community)
        jsonObject.addProperty("interests", interests)
        jsonObject.addProperty("about", about)

        Log.d("TAG@123", "done Update" + jsonObject.toString())
        mainRepository.Update_profile(jsonObject).let {
            if (it.isSuccessful) {
                update_profile.postValue(Resource.success(it.body()))

            } else {
                update_profile.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }



    fun update_phone_location(
        id: String,
        key: String,
        vlaue: String,

    ) = viewModelScope.launch {
        update_profile.postValue(Resource.loading(null))
        val jsonObject = JsonObject()

        jsonObject.addProperty("user_id", id)
        jsonObject.addProperty(key,vlaue)


        Log.d("TAG@123", "done Update" + jsonObject.toString())
        mainRepository.Update_profile(jsonObject).let {
            if (it.isSuccessful) {
                change_password.postValue(Resource.success(it.body()))
            } else {
                change_password.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }


}