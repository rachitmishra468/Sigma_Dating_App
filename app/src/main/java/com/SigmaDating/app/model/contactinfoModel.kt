package com.SigmaDating.app.model

data class contactinfoModel(val status: Boolean, val data: Data)


data class Data(
    val share_app_text: String,
    val safety_message_text: String
)