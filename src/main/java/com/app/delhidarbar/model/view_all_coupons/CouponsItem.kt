package com.app.delhidarbar.model.view_all_coupons

import com.google.gson.annotations.SerializedName

data class CouponsItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("percentage")
	val percentage: String? = null,

	@field:SerializedName("discount")
	val discount: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
)