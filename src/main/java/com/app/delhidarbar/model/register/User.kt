package com.app.delhidarbar.model.register

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)