package com.app.delhidarbar.model.login

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("phone")
	val phone: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)