package com.app.delhidarbar.model.place_order_response

data class PlaceOrderResponse(
	val restaurant: Restaurant? = null,
	val error: Boolean? = null,
	val client_token: String? = null,
	val message: String? = null,
	val user: User? = null,
	val cart: List<CartItem?>? = null
)
