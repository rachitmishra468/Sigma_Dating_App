package com.SigmaDating.apk.model

data class User_bids_list(
    val id:String,
    val email: String,
    val last_name: String,
    val active: String,
    val user_type:String,
    val dob: String,
    val first_name: String,
    val gender: String,
    val about:String,
    val upload_image:String,
    val location:String,
    val phone:String,
    val university:String,
    val community:String,
    val interests:String,
    val age_range:String,
    val distance:String,
    val latitude:String,
    val longitude:String,
    val interested_in:String,
    val greekletter:String,
    val age:String,
    val match_id:String,
    var tag_add:Boolean=false


)
