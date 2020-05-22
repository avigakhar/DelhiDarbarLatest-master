package com.app.delhidarbar.model.dashboard.home

data class Parent_Home(
        var categories: MutableList<Category>,
        val error: Boolean,
        var items: List<Product>,
        val message: String,
        val offers: List<Offer>
)