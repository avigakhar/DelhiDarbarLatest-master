package com.app.delhidarbar.model.register

import com.google.gson.annotations.SerializedName


data class ParentRegisterResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)