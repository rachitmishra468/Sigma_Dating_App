package com.SigmaDating.apk.model

data class home_model(
    val status: Boolean,
    val  message:String,
    var max_swipes_per_day:String,
    var notifications_count:String,
    var bids: ArrayList<Bids>,
    var pages:ArrayList<Pages>
)