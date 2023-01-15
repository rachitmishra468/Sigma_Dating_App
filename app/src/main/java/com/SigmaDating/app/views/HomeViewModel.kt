package com.SigmaDating.app.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SigmaDating.app.model.*
import com.example.demoapp.other.Resource
import com.google.gson.JsonObject
import com.SigmaDating.model.SchoolCommunityResponse
import com.SigmaDating.app.repository.MainRepository
import com.SigmaDating.app.storage.SharedPreferencesStorage
import com.SigmaDating.app.utilities.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {
    lateinit var report_block_user: MutableLiveData<Resource<Loginmodel>>
    lateinit var setting_update_details: MutableLiveData<Resource<Loginmodel>>
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
    lateinit var change_status_post: MutableLiveData<Resource<delelepost>>

    lateinit var All_post: MutableLiveData<Resource<post>>
    lateinit var like_post: MutableLiveData<Resource<Loginmodel>>
    lateinit var notification_list: MutableLiveData<Resource<Notification_model>>
    lateinit var deletenotification: MutableLiveData<Resource<Loginmodel>>
    lateinit var sent_comment: MutableLiveData<Resource<Loginmodel>>
    lateinit var All_comment: MutableLiveData<Resource<Comment_model>>
    lateinit var all_match_bids: MutableLiveData<Resource<Match_bids>>
    lateinit var ctrateToken_data: MutableLiveData<Resource<Token_data>>
    lateinit var contact_responce: MutableLiveData<Resource<Loginmodel>>
    lateinit var subscriptionPlans: MutableLiveData<Resource<SubscriptionPlanData>>
    lateinit var subscription_post: MutableLiveData<Resource<SubscriptionPlanData>>
    val user_bids: MutableLiveData<Resource<home_model>>
    lateinit var app_ads: MutableLiveData<Resource<advertisingData>>

    init {
        report_block_user = MutableLiveData<Resource<Loginmodel>>()
        //  =MutableLiveData<Resource<Loginmodel>>()
        get_UserReportdata = MutableLiveData<Resource<Loginmodel>>()
        get_user_data = MutableLiveData<Resource<Loginmodel>>()
        get_secound_feb_data = MutableLiveData<Resource<Loginmodel>>()
        change_password = MutableLiveData<Resource<Loginmodel>>()
        user_bids = MutableLiveData<Resource<home_model>>()
        // profile_swipe=MutableLiveData<Resource<Loginmodel>>()
    }

    /*fun get_home_feb_data(id: String) {
       var job= get_Login_User_bids(id)
       var job_2= get_Login_User_details(id)

    }*/


    fun get_User_token(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "get user token call")
            ctrateToken_data.postValue(Resource.loading(null))
            mainRepository.ctrateToken(id).let {
                if (it.isSuccessful) {
                    Resource.success(it.body()).data.let {
                        Log.d("TAG@123", " token call :"+it)
                        Home.mCurrent_user_token = it?.token.toString()
                        Log.d("TAG@123", "Token : " + Home.mCurrent_user_token)

                    }
                    ctrateToken_data.postValue(Resource.success(it.body()))
                } else {
                    Log.d("TAG@123", "error in get token call ")
                    ctrateToken_data.postValue(Resource.error(it.errorBody().toString(), null))
                }


            }
        }
    }

    fun get_User_video_token(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "get user token call")
            ctrateToken_data.postValue(Resource.loading(null))
            mainRepository.ctrateToken(id).let {
                if (it.isSuccessful) {


                    Resource.success(it.body()).data.let {
                        Log.d("TAG@123", " token call :"+it)
                       // Home.mCurrent_user_token = it?.token.toString()
                        Home.mVideoGrant_user_token = it?.token.toString()
                        Log.d("TAG@123", "Token : " + Home.mCurrent_user_token)

                    }
                    ctrateToken_data.postValue(Resource.success(it.body()))
                } else {
                    Log.d("TAG@123", "error in get token call ")
                    ctrateToken_data.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }




    fun get_user_match_bids(id: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            all_match_bids.postValue(Resource.loading(null))
            mainRepository.get_user_match_bids(id).let {
                if (it.isSuccessful) {
                    all_match_bids.postValue(Resource.success(it.body()))
                } else {
                    all_match_bids.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun get_notification_list(jsonObject: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            notification_list.postValue(Resource.loading(null))
            mainRepository.get_notification(jsonObject).let {
                if (it.isSuccessful) {
                    notification_list.postValue(Resource.success(it.body()))
                } else {
                    notification_list.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun user_deletenotification(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            deletenotification.postValue(Resource.loading(null))
            mainRepository.deletenotification(jsonObject).let {
                if (it.isSuccessful) {
                    deletenotification.postValue(Resource.success(it.body()))
                } else {
                    deletenotification.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun save_like_post_data(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            like_post.postValue(Resource.loading(null))
            mainRepository.save_like_post_data(jsonObject).let {
                if (it.isSuccessful) {
                    like_post.postValue(Resource.success(it.body()))
                } else {
                    like_post.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun sent_comment(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            sent_comment.postValue(Resource.loading(null))
            mainRepository.sentcomment(jsonObject).let {
                if (it.isSuccessful) {
                    sent_comment.postValue(Resource.success(it.body()))
                } else {
                    sent_comment.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getAllComment(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            All_comment.postValue(Resource.loading(null))
            mainRepository.getallcomment(jsonObject).let {
                if (it.isSuccessful) {
                    All_comment.postValue(Resource.success(it.body()))
                } else {
                    All_comment.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun getAllPost(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            All_post.postValue(Resource.loading(null))
            mainRepository.showmyposts(jsonObject).let {
                if (it.isSuccessful) {
                    All_post.postValue(Resource.success(it.body()))
                } else {
                    All_post.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun create_post(file: File, jsonObject: HashMap<String, String>) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "images path : " + file.absolutePath)
            Log.d("TAG@123", "other data : " + jsonObject)
            Log.d(
                "TAG@123", "tag_users : " + jsonObject.get("tag_users")!!
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

            val id: RequestBody =
                jsonObject.get("user_id")!!.toRequestBody("text/plain".toMediaType())
            val title: RequestBody =
                jsonObject.get("title")!!.toRequestBody("text/plain".toMediaType())
            val tag_users: RequestBody =
                jsonObject.get("tag_users")!!.toRequestBody("text/plain".toMediaType())
            val description: RequestBody =
                jsonObject.get("description")!!.toRequestBody("text/plain".toMediaType())
            val location: RequestBody =
                jsonObject.get("location")!!.toRequestBody("text/plain".toMediaType())
            val isPrivate: RequestBody =
                jsonObject.get("isPrivate")!!.toRequestBody("text/plain".toMediaType())

            create_post.postValue(Resource.loading(null))
            mainRepository.create_post(
                isPrivate, id, location, title, description, tag_users, profileImageBody
            ).let {
                if (it.isSuccessful) {
                    create_post.postValue(Resource.success(it.body()))
                } else {
                    create_post.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun PostStatusChange(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            change_status_post.postValue(Resource.loading(null))
            mainRepository.PostStatusChange(jsonObject).let {
                if (it.isSuccessful) {
                    change_status_post.postValue(Resource.success(it.body()))
                } else {
                    change_status_post.postValue(Resource.error(it.errorBody().toString(), null))
                }

            }
        }
    }

    fun deletepost(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            delete_post.postValue(Resource.loading(null))
            mainRepository.deletepost(jsonObject).let {
                if (it.isSuccessful) {
                    delete_post.postValue(Resource.success(it.body()))
                } else {
                    delete_post.postValue(Resource.error(it.errorBody().toString(), null))
                }

            }
        }
    }





    fun report_user(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            report_block_user.postValue(Resource.loading(null))
            mainRepository.report_user(jsonObject).let {
                if (it.isSuccessful) {
                    report_block_user.postValue(Resource.success(it.body()))
                } else {
                    report_block_user.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }

    fun block_user(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            report_block_user.postValue(Resource.loading(null))
            mainRepository.block_user(jsonObject).let {
                if (it.isSuccessful) {
                    report_block_user.postValue(Resource.success(it.body()))
                } else {
                    report_block_user.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun profile_swipe_details(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            profile_swipe.postValue(Resource.loading(null))
            mainRepository.get_profile_swipe_details(jsonObject).let {
                if (it.isSuccessful) {
                    profile_swipe.postValue(Resource.success(it.body()))
                } else {
                    profile_swipe.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun get_setting_update_details(jsonObject: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            setting_update_details.postValue(Resource.loading(null))
            mainRepository.get_setting_update_details(jsonObject).let {
                if (it.isSuccessful) {
                    setting_update_details.postValue(Resource.success(it.body()))
                } else {
                    setting_update_details.postValue(
                        Resource.error(
                            it.errorBody().toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun get_ads_list(id: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            val jsonObject = JsonObject()
            Log.d("TAG@123", id)
            jsonObject.addProperty("position", id)
            app_ads.postValue(Resource.loading(null))
            Log.d("TAG@123", "get_app_ads_list")
            mainRepository.getlistads(jsonObject).let {
                if (it.isSuccessful) {
                    Log.d("TAG@123", "get_Login_User_bids  isSuccessful")
                    app_ads.postValue(Resource.success(it.body()))
                } else {
                    Log.d("TAG@123", "get_Login_User_bids  isSuccessful false")
                    app_ads.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }



    fun get_Login_User_bids(id: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }


    fun get_Edit_User_details(id: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }


    fun get_Login_User_details(id: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }


    fun get_secound_feb_User_details(id: String) = GlobalScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }

    fun get_UserReport_details(id: String) = GlobalScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }

    fun User_delete_account(id: String, password: String) =

        viewModelScope.launch {
            if (AppUtils.isNetworkAvailable()) {
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
        }


    fun User_change_password(id: String, password: String, password_confirm: String) =
        viewModelScope.launch {
            if (AppUtils.isNetworkAvailable()) {
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
        }


    fun get_edit_page_data(id: String) {
        getSchoolingData()
    }

    fun Update_edit_page_data(
        id: String,
        university: String,
        community: String,
        interests: String,
        about: String,
        is_private: String,
        interested_in:String,
        show_me:String,
        age_range:String,
        distance:String,
        gender:String
    ) {

        update_profile(id, university, community, interests, about, is_private,interested_in,show_me,age_range,distance,gender)


    }


    fun User_upload_images(id: String, img: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }


    fun User_delete_images(id: String, img: String) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
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
    }


    fun getSchoolingData() = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            school_dataResponse.postValue(Resource.loading(null))
            mainRepository.ListSchoolFeternity().let {
                if (it.isSuccessful) {
                    school_dataResponse.postValue(Resource.success(it.body()))
                } else {
                    school_dataResponse.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun update_profile(
        id: String,
        university: String,
        community: String,
        interests: String,
        about: String,
        is_private: String,
        interested_in:String,
        show_me:String,
        age_range:String,
        distance:String,
        gender:String
    ) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            update_profile.postValue(Resource.loading(null))
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", id)
            jsonObject.addProperty("university", university)
            jsonObject.addProperty("community", community)
            jsonObject.addProperty("interests", interests)
            jsonObject.addProperty("about", about)
            jsonObject.addProperty("is_private", is_private)
            jsonObject.addProperty("interested_in", interested_in)
            jsonObject.addProperty("show_me", show_me)
            jsonObject.addProperty("age_range", age_range)
            jsonObject.addProperty("distance", distance)
            jsonObject.addProperty("gender", gender)
            Log.d("TAG@123", "done Update" + jsonObject.toString())
            mainRepository.Update_profile(jsonObject).let {
                if (it.isSuccessful) {
                    update_profile.postValue(Resource.success(it.body()))

                } else {
                    update_profile.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun update_phone_location(
        id: String,
        key: String,
        vlaue: String,

        ) = viewModelScope.launch {

        if (AppUtils.isNetworkAvailable()) {
            update_profile.postValue(Resource.loading(null))
            val jsonObject = JsonObject()

            jsonObject.addProperty("user_id", id)
            jsonObject.addProperty(key, vlaue)


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


    fun sendChatNotification(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "Notification")
            mainRepository.sendNotification(id).let {
                if (it.isSuccessful) {
                    Log.d("TAG@123", "send Chat Notification sent")
                } else {
                    Log.d("TAG@123", "error in get send Chat Notification call ")

                }
            }
        }
    }


    fun getSubscriptionPlans() = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "Notification")
            mainRepository.getSubscriptionPlanslist().let {
                if (it.isSuccessful) {
                    subscriptionPlans.postValue(Resource.success(it.body()))
                } else {
                    subscriptionPlans.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }


    fun postSubscriptionData(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "postSubscriptionData"+id)
            mainRepository.postSubscriptionPlansdata(id).let {
                if (it.isSuccessful) {
                    subscription_post.postValue(Resource.success(it.body()))
                } else {
                    subscription_post.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }



    fun post_Contact_form(id: JsonObject) = viewModelScope.launch {
        if (AppUtils.isNetworkAvailable()) {
            Log.d("TAG@123", "Contact_form"+id)
            mainRepository.contact_form(id).let {
                if (it.isSuccessful) {
                    contact_responce.postValue(Resource.success(it.body()))
                } else {
                    contact_responce.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
        }
    }



}