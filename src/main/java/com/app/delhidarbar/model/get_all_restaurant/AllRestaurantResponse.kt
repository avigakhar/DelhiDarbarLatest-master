package com.app.delhidarbar.model.get_all_restaurant

import com.google.gson.annotations.SerializedName

data class AllRestaurantResponse(

	@field:SerializedName("parties")
	val parties: List<String?>? = null,

	@field:SerializedName("restaurants")
	val restaurants: List<RestaurantsItem>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)