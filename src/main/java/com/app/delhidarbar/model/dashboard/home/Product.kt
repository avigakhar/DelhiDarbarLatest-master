package com.app.delhidarbar.model.dashboard.home

data class Product(
        val category_id: Int,
        val description: String,
        val id: Int,
        val image: String,
        val name: String,
        val price: Double,
        val rating: Int,
        val reviews: Int,
        var my_final_price: Double,
        val type: String,
        val all_type: String,
        val select_type: String,
        val gredients: MutableList<Variant>?
) {
    private var lowestPrice: Double = 0.0
    private var highestPrice: Double = 0.0


    fun isVeg () : Boolean {
        return type == "0"
    }

    fun isNonVeg () : Boolean {
        return type == "1"
    }

    fun isVegan () : Boolean {
        return all_type == "vegan"
    }

    fun getLowestVariantPrice () : Double {

        if (lowestPrice != 0.0) {
            return lowestPrice
        }

        if (gredients != null) {
            if (gredients.isEmpty()) {
                return price
            }

            lowestPrice = price
            for (variant in gredients) {
                if (variant.price.isNullOrBlank()) {
                    continue
                }

                if (variant.price.toDouble() < lowestPrice) {
                    lowestPrice = variant.price.toDouble()
                }
            }
        }

        return lowestPrice
    }

    fun getHighestVariantPrice () : Double {

        if (highestPrice != 0.0) {
            return highestPrice
        }

        if (gredients != null) {
            if (gredients.isEmpty()) {
                return price
            }

            highestPrice = price
            for (variant in gredients) {
                if (variant.price.isNullOrBlank()) {
                    continue
                }

                if (variant.price.toDouble() > highestPrice) {
                    highestPrice = variant.price.toDouble()
                }
            }
        }

        return highestPrice
    }
}