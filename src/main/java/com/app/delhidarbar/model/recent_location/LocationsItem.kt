package com.app.delhidarbar.model.recent_location

import com.google.gson.annotations.SerializedName

data class LocationsItem(

	@field:SerializedName("location_address")
	val locationAddress: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("location_latitude")
	val locationLatitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("location_longitude")
	val locationLongitude: String? = null
)
