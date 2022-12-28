package com.SigmaDating.app.model

data class SubscriptionPlanData(val status: Boolean, val plans: List<Plans>,val message:String)


data class  Plans(
    var id:String,
    var plan_name:String,
    var plan_description:String,
    var price:String,
    var color:String
)