package com.SigmaDating.apk.model

data class comment_list(
    var id:String,
    var post_id:String,
    var location:String,
    var comment_text:String,
    var user_id:String,
    var date_added:String,
    var media:String,
    var first_name:String,
    var last_name:String,
    var upload_image:String
)
