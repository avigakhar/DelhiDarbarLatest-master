package com.app.delhidarbar.model.place_order_response

data class CartItem(
	val userId: String? = null,
	val totalAmount: Int? = null,
	val orderNumber: String? = null,
	val id: String? = null,
	val items: List<ItemsItem?>? = null,
	val status: String? = null
)
