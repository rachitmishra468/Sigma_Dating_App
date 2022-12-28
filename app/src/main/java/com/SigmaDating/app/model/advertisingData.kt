package com.SigmaDating.app.model

data class advertisingData(val status: Boolean, val ads: List<advertising_model>,val message:String)

data class  advertising_model(
    var id:String,
    var filename:String,
    var type:String,
    var ad_link:String
)
