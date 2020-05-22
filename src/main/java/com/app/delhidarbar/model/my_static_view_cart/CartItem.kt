package com.app.delhidarbar.model.my_static_view_cart


data class CartItem(
        val user_id: String,
        val total_amount: Double,
        val order_number: String,
        val id: String,
        var items: List<ItemsItem>,
        val status: String,
        val discount: Double,
        val discount_amount: Double

)