package com.app.delhidarbar.product_dialog

import com.app.delhidarbar.model.dashboard.home.Variant

/**
 * Created by rupinderjeet47@gmail.com on 10/31/2019
 */
data class ProductCustomizationRequest (
        // val productDto: ProductDto,
        val selectedQuantity: Int,
        val selectedVariant: Variant?
)