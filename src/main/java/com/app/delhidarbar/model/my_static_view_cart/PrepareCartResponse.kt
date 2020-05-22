package com.app.delhidarbar.model.my_static_view_cart


data class PrepareCartResponse(
        var restaurant: List<Restaurant>,
        val error: Boolean,
        val message: String,
        val user: User,
        val cart: List<CartItem>

)