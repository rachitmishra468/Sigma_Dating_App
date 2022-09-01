package com.SigmaDating.app.model.communityModel

import com.google.gson.annotations.SerializedName

data class UniversityList (
	@SerializedName("id")

	val id : Int,
	@SerializedName("name")

	val name : String,
	@SerializedName("greekLetter")

	val greekLetter : String,

	@SerializedName("orgType")

	val orgType : String
)