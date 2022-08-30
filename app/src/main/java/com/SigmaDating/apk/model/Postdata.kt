package com.SigmaDating.apk.model

data class Postdata(
    var id:String,
    var description:String,
    var title:String,
    var location:String,
    var media:String,
    var isPrivate:String,
    var user_id:String,
    var post_id:String,
    var date_added:String,
    var first_name:String,
    var last_name:String,
    var like :Boolean,
    var upload_image:String
)
