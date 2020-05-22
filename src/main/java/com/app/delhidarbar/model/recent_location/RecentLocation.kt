package com.app.delhidarbar.model.recent_location

import com.google.gson.annotations.SerializedName

data class RecentLocation(

	@field:SerializedName("locations")
	val locations: List<LocationsItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)