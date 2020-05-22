package com.app.delhidarbar.model.get_all_restaurant

import com.google.gson.annotations.SerializedName

data class RestaurantsItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)