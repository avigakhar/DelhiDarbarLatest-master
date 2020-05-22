package com.app.delhidarbar.helper

import android.util.Log
import com.app.delhidarbar.model.dashboard.home.Product
import com.app.delhidarbar.model.dashboard.home.Variant
import com.app.delhidarbar.utils.RealmController
import com.app.delhidarbar.utils.RealmProduct
import com.google.gson.Gson
import io.realm.Realm

class CartProductController {

    var realm: Realm = RealmController.getInstance().realm

    fun addItem(product: Product,
                selectedVariant: Variant? = null,
                quantity: Int,
                itemPrice: Double
    ) {

        var alreadyQuantity = 0
        var alreadyAddedRealmId = 0

        var blAlready = false

        val shouldCopyOrUpdate: Boolean
        val newRealmProduct = RealmProduct()
        val existingRealmObjects = RealmController.getInstance().all

        if (existingRealmObjects.size != 0) {
            for (existingRealmObject in existingRealmObjects) {

                if (product.id == existingRealmObject.id) {

                    if (product.gredients == null || product.gredients.isEmpty()) {
                        alreadyAddedRealmId = existingRealmObject.realmid
                        alreadyQuantity = (existingRealmObject.quantity)
                        blAlready = true
                    } else {
                        if (selectedVariant != null
                                && selectedVariant.name == existingRealmObject.ingredients
                                && selectedVariant.size == existingRealmObject.spices) {

                            alreadyAddedRealmId = existingRealmObject.realmid
                            alreadyQuantity = existingRealmObject.quantity
                            blAlready = true
                        } else {
                            alreadyAddedRealmId = 0
                            alreadyQuantity = 0
                            blAlready = false
                        }
                    }
                    break
                }
            }

            newRealmProduct.realmid = when {
                blAlready -> alreadyAddedRealmId
                else -> getNewRealmId()
            }

            shouldCopyOrUpdate = blAlready

        } else {
            newRealmProduct.realmid = getNewRealmId()
            shouldCopyOrUpdate = false
        }

        newRealmProduct.id = product.id
        newRealmProduct.category_id = product.category_id
        newRealmProduct.reviews = product.reviews
        newRealmProduct.rating = product.rating
        newRealmProduct.quantity = alreadyQuantity + quantity
        newRealmProduct.description = product.description
        newRealmProduct.image = product.image
        newRealmProduct.name = product.name
        newRealmProduct.type = product.type
        newRealmProduct.all_type = product.all_type
        newRealmProduct.select_type = product.select_type
        newRealmProduct.price = itemPrice
        newRealmProduct.myFinalPrice = newRealmProduct.quantity * itemPrice
        newRealmProduct.spices = selectedVariant?.size ?: ""
        newRealmProduct.ingredients = selectedVariant?.name ?: ""
        newRealmProduct.addon_id = "${selectedVariant?.id}"
        newRealmProduct.variantsJson = Gson().toJson(product.gredients)

        realm.beginTransaction()
        if (shouldCopyOrUpdate) {
            realm.copyToRealmOrUpdate(newRealmProduct)
        } else {
            realm.copyToRealm(newRealmProduct)
        }

        realm.commitTransaction()
    }

    fun removeItem(product: Product,
                   selectedVariant: Variant? = null,
                   quantity: Int,
                   itemPrice: Double
    ) {

        var alreadyQuantity = 0
        var alreadyAddedRealmId = 0

        var blAlready = false
        val newRealmProduct = RealmProduct()
        val existingRealmObjects = RealmController.getInstance().all

        if (existingRealmObjects.size == 0) {
            return
        }

        for (existingRealmObject in existingRealmObjects) {
            if (product.id == existingRealmObject.id) {

                if (product.gredients == null || product.gredients.isEmpty()) {
                    alreadyAddedRealmId = existingRealmObject.realmid
                    alreadyQuantity = existingRealmObject.quantity
                    blAlready = true
                } else {
                    if (selectedVariant != null
                            && selectedVariant.name == existingRealmObject.ingredients
                            && selectedVariant.size == existingRealmObject.spices) {

                        alreadyAddedRealmId = existingRealmObject.realmid
                        alreadyQuantity = existingRealmObject.quantity
                        blAlready = true
                    }
                }

                break
            }
        }

        if (blAlready) {

            if (alreadyQuantity >= quantity) {
                newRealmProduct.realmid = alreadyAddedRealmId
                newRealmProduct.id = product.id
                newRealmProduct.category_id = product.category_id
                newRealmProduct.reviews = product.reviews
                newRealmProduct.rating = product.rating
                newRealmProduct.quantity = alreadyQuantity - quantity
                newRealmProduct.description = product.description
                newRealmProduct.image = product.image
                newRealmProduct.name = product.name
                newRealmProduct.type = product.type
                newRealmProduct.all_type = product.all_type
                newRealmProduct.select_type = product.select_type
                newRealmProduct.price = itemPrice
                newRealmProduct.myFinalPrice =  newRealmProduct.quantity * itemPrice
                newRealmProduct.spices = selectedVariant?.size ?: ""
                newRealmProduct.addon_id = "${selectedVariant?.id}"
                newRealmProduct.ingredients = selectedVariant?.name ?: ""
                newRealmProduct.variantsJson = Gson().toJson(product.gredients)

                realm.beginTransaction()
                realm.copyToRealmOrUpdate(newRealmProduct)
                realm.commitTransaction()
                // TODO realm.close()
            } else {
                deleteById(alreadyAddedRealmId)
            }
        }
    }


    fun addItem(realmProduct: RealmProduct,
                selectedVariant: Variant? = null,
                quantity: Int,
                itemPrice: Double
    ) {

        val newRealmProduct = RealmProduct()
        newRealmProduct.realmid = realmProduct.realmid
        newRealmProduct.id = realmProduct.id
        newRealmProduct.category_id = realmProduct.category_id
        newRealmProduct.reviews = realmProduct.reviews
        newRealmProduct.rating = realmProduct.rating
        newRealmProduct.quantity = realmProduct.quantity + quantity
        newRealmProduct.description = realmProduct.description
        newRealmProduct.image = realmProduct.image
        newRealmProduct.name = realmProduct.name
        newRealmProduct.type = realmProduct.type
        newRealmProduct.all_type = realmProduct.all_type
        newRealmProduct.select_type = realmProduct.select_type
        newRealmProduct.price = itemPrice
        newRealmProduct.myFinalPrice =  newRealmProduct.quantity * itemPrice
        newRealmProduct.spices = selectedVariant?.size ?: ""
        newRealmProduct.addon_id = "${selectedVariant?.id}"
        newRealmProduct.ingredients = selectedVariant?.name ?: ""
        newRealmProduct.variantsJson = realmProduct.variantsJson

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(newRealmProduct)
        realm.commitTransaction()
    }

    fun removeItem(realmProduct: RealmProduct,
                   selectedVariant: Variant? = null,
                   quantity: Int,
                   itemPrice: Double
    ) {

        val newRealmProduct = RealmProduct()

        if (realmProduct.quantity >= quantity) {
            newRealmProduct.realmid = realmProduct.realmid
            newRealmProduct.id = realmProduct.id
            newRealmProduct.category_id = realmProduct.category_id
            newRealmProduct.reviews = realmProduct.reviews
            newRealmProduct.rating = realmProduct.rating
            newRealmProduct.quantity = realmProduct.quantity - quantity
            newRealmProduct.description = realmProduct.description
            newRealmProduct.image = realmProduct.image
            newRealmProduct.name = realmProduct.name
            newRealmProduct.type = realmProduct.type
            newRealmProduct.all_type = realmProduct.all_type
            newRealmProduct.select_type = realmProduct.select_type
            newRealmProduct.price = itemPrice
            newRealmProduct.myFinalPrice =  newRealmProduct.quantity * itemPrice
            newRealmProduct.spices = selectedVariant?.size ?: ""
            newRealmProduct.addon_id = "${selectedVariant?.id}"
            newRealmProduct.ingredients = selectedVariant?.name ?: ""
            newRealmProduct.variantsJson = realmProduct.variantsJson

            realm.beginTransaction()
            realm.copyToRealmOrUpdate(newRealmProduct)
            realm.commitTransaction()
        } else {
            deleteById(realmProduct.realmid)
        }
    }

    private fun getNewRealmId(): Int {

        var maximumRealmId = 0

        // find max in existing realm ids
        for (existingRealmObject in RealmController.getInstance().all) {
            if (existingRealmObject.realmid > maximumRealmId) {
                maximumRealmId = existingRealmObject.realmid
            }
        }

        return ++maximumRealmId
    }

    private fun getProductQuantityInCart(id: Int): Int {

        // find quantity of existing realm item
        for (existingRealmObject in RealmController.getInstance().all) {
            if (existingRealmObject.id == id) {
                return existingRealmObject.quantity
            }
        }

        return 0
    }

    private fun deleteById(id: Int) {
        realm.beginTransaction()
        val results = realm.where(RealmProduct::class.java).equalTo("realmid", id).findAll()
        results.deleteAllFromRealm()
        realm.commitTransaction()
        //TODO realm.close()
    }

    /**
     * print existing products/realms in cart
     */
    private fun printCart() {
        for (existingRealmObject in RealmController.getInstance().all) {
            Log.d("CartProductController",
                    "realmId: ${existingRealmObject.realmid}, " +
                            "item-id: ${existingRealmObject.id}, " +
                            "item-quantity: ${existingRealmObject.quantity}"
            )
        }
        Log.d("CartProductController", "--------------------------------------")
    }
}