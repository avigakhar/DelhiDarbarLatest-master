package com.app.delhidarbar.model.my_static_view_cart

import com.app.delhidarbar.model.dashboard.home.Variant

data class ItemsItem(
        var quantity: String,
        val amount: Double,
        val item_desc: String,
        val item_id: String,
        var item_price: Double,
        val item_image: String,
        val item_name: String,
        val select_type: String,
        val id: String,
        val orderId: String,
        val reviews: Int,
        val category_id: String,
        val rating: Int,
        var gredients: MutableList<Variant>,
        val type: String,
        val spices: String,
        val ingredients: String
)