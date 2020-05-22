package com.app.delhidarbar.model.dashboard.home

data class Variant (
        var id: Int,
        var name: String,
        var price: String,
        var size: String,
        var veg: String,
        var vegan: String
) {

    fun isVeg () : Boolean {
        return veg == "0"
    }

    fun isNonVeg () : Boolean {
        return veg == "1"
    }

    fun isVegan () : Boolean {
        return vegan == "vegan"
    }
}