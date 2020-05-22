package com.app.delhidarbar.product_dialog

/**
 * Created by rupinderjeet47@gmail.com on 10/30/2019
 */
object DietHelper {

    const val VEG = "0"
    const val VEGAN = "1"
    const val VEG_OR_VEGAN = "2"
    const val NON_VEG = "3"
    const val ANY = "5"

    /*
     * All Diets
     */

    fun isVeg (diet: String) : Boolean {
        return diet == VEG
    }

    fun isVegan (diet: String) : Boolean {
        return diet == VEGAN
    }

    fun isVegOrVegan (diet: String) : Boolean {
        return diet == VEG_OR_VEGAN
    }

    fun isNonVeg (diet: String) : Boolean {
        return diet == NON_VEG
    }

    fun isAny (diet: String) : Boolean {
        return diet == ANY
    }
}