package com.app.delhidarbar.model.view_all_coupons

import com.google.gson.annotations.SerializedName

data class ViewAllCouponResponse(

	@field:SerializedName("coupons")
	val coupons: List<CouponsItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)