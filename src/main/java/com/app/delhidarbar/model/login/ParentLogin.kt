package com.app.delhidarbar.model.login

import com.google.gson.annotations.SerializedName

data class ParentLogin(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)