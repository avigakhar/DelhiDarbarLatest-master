package com.app.delhidarbar.product_dialog

import com.app.delhidarbar.model.dashboard.home.Product
import com.app.delhidarbar.model.dashboard.home.Variant
import com.app.delhidarbar.utils.RealmProduct
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by rupinderjeet47@gmail.com on 11/1/2019
 */
class ProductDto private constructor() {

    var hasPojo = false
    lateinit var pojoProduct: Product
    lateinit var realmProduct: RealmProduct

    private lateinit var variants: List<Variant>

    companion object {
        fun of(product: Product) : ProductDto {
            val productDto = ProductDto()
            productDto.hasPojo = true
            productDto.pojoProduct = product
            return productDto
        }

        fun of(realmProduct: RealmProduct) : ProductDto {
            val productDto = ProductDto()
            productDto.hasPojo = false
            productDto.realmProduct = realmProduct
            return productDto
        }
    }

    // fun getPrimaryId () : Int = if (hasPojo) -1 else realmProduct.realmid
    fun getId () : Int = if (hasPojo) pojoProduct.id else realmProduct.id
    fun getName () : String = if (hasPojo) pojoProduct.name else realmProduct.name
    fun getCategoryId () : Int = if (hasPojo) pojoProduct.category_id else realmProduct.category_id
    fun getImage () : String = if (hasPojo) pojoProduct.image else realmProduct.image
    fun getDescription () : String = if (hasPojo) pojoProduct.description else realmProduct.description
    fun getPrice () : Double = if (hasPojo) pojoProduct.price else realmProduct.price
    fun getRating () : Int = if (hasPojo) pojoProduct.rating else realmProduct.rating
    fun getReviewCount () : Int = if (hasPojo) pojoProduct.reviews else realmProduct.reviews
    fun getQuantity () : Int = if (hasPojo) -1 else realmProduct.quantity
    fun getSelectionType () : String = if (hasPojo) pojoProduct.select_type else realmProduct.select_type
    fun getVariants () : List<Variant> {

        return when {
            hasPojo -> {

                val availableVariants = pojoProduct.gredients
                if (availableVariants == null || availableVariants.isEmpty()) {
                    return listOf()
                }

                availableVariants
            }
            ::variants.isInitialized -> variants
            else -> {
                variants = Gson().fromJson(
                        realmProduct.variantsJson,
                        TypeToken.getParameterized(List::class.java, Variant::class.java).type
                )
                variants
            }
        }
    }

    fun getSelectedIngredient () : String = if (hasPojo) "" else realmProduct.ingredients
    fun getSelectedIngredientSize () : String = if (hasPojo) "" else realmProduct.spices
    fun getVegType () : String = if (hasPojo) pojoProduct.type else realmProduct.type
    fun getVeganType () : String = if (hasPojo) pojoProduct.all_type else realmProduct.all_type
    fun getTotalPrice () : Double = if (hasPojo) pojoProduct.my_final_price else realmProduct.myFinalPrice
}