package com.app.delhidarbar.product_dialog

object CategoryHelper {

    fun hasIngredientsVariantInCategory (categoryId: Int) : Boolean {
        return categoryId == 5 || categoryId == 9 || categoryId == 13
    }

    fun isCurryOrBiryani (categoryId: Int) : Boolean {
        return categoryId == 5 || categoryId == 9
    }

    /*
     * All Categories
     */

    fun isAppetizersCategory (categoryId: Int) : Boolean {
        return categoryId == 3
    }

    fun isStartersCategory (categoryId: Int) : Boolean {
        return categoryId == 4
    }

    fun isCurryCategory (categoryId: Int) : Boolean {
        return categoryId == 5
    }

    fun isHouseSpecialsCategory (categoryId: Int) : Boolean {
        return categoryId == 6
    }

    fun isSideDishesCategory (categoryId: Int) : Boolean {
        return categoryId == 7
    }

    fun isSizzlersCategory (categoryId: Int) : Boolean {
        return categoryId == 8
    }

    fun isBiryaniCategory (categoryId: Int) : Boolean {
        return categoryId == 9
    }

    fun isRiceCategory (categoryId: Int) : Boolean {
        return categoryId == 10
    }

    fun isNaanRotiCategory (categoryId: Int) : Boolean {
        return categoryId == 11
    }

    fun isKidsCategory (categoryId: Int) : Boolean {
        return categoryId == 12
    }

    fun isDrinksCategory (categoryId: Int) : Boolean {
        return categoryId == 13
    }

    fun isWinesCategory (categoryId: Int) : Boolean {
        return categoryId == 14
    }
}