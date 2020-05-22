package com.app.delhidarbar.product_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.Util
import com.app.delhidarbar.helper.setSelectionByValue
import com.app.delhidarbar.helper.toast
import com.app.delhidarbar.model.dashboard.home.Variant
import com.squareup.picasso.Picasso

class ProductCustomizationDialog(
        private val context: Context,
        private val cartClick: Int,
        private val dietType: String = DietHelper.ANY,
        private val isEditingMode: Boolean = false,
        private val customizationListener: ProductCustomizationListener
) {

    private var selectedQuantity = 0
    var cartClick1 = 0
    private var selectedVariant: Variant? = null

    fun show(productDto: ProductDto) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_customise)
        dialog.setCancelable(false)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        cartClick1 = cartClick

        val productViewHolder = ProductViewHolder(dialog)
        productViewHolder.bindItem(productDto)

        dialog.setOnKeyListener { dialogInterface, keyCode, _ ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialogInterface.dismiss()
            }
            true
        }
    }

    inner class ProductViewHolder(dialog: Dialog) {

        private val nameTextView: TextView = dialog.findViewById(R.id.product_name_text_view)
        private val productImageView: ImageView = dialog.findViewById(R.id.product_image_view)

        private val ingredientsLayout: View = dialog.findViewById(R.id.product_ingredients_layout)
        private val ingredientsSpinner: Spinner = dialog.findViewById(R.id.product_ingredients_spinner)

        private val spicesLayout: View = dialog.findViewById(R.id.product_spices_layout)
        private val spicesSpinner: Spinner = dialog.findViewById(R.id.product_spices_spinner)

        private val decreaseQuantityLayout: View = dialog.findViewById(R.id.decrease_quantity_layout)
        // private val decreaseQuantityImageView: ImageView = dialog.findViewById(R.id.decrease_quantity_image_view)

        private val increaseQuantityLayout: View = dialog.findViewById(R.id.increase_quantity_layout)
        // private val increaseQuantityImageView: ImageView = dialog.findViewById(R.id.increase_quantity_image_view)

        // private val quantityLayout: View = dialog.findViewById(R.id.product_quantity_layout)
        private val quantityTextView: TextView = dialog.findViewById(R.id.product_quantity_text_view)

        private val priceLayout: View = dialog.findViewById(R.id.product_price_layout)
        private val priceTextView: TextView = dialog.findViewById(R.id.product_price_text_view)

        private val submitButton: Button = dialog.findViewById(R.id.submit_button)
        private val cancelButton: Button = dialog.findViewById(R.id.cancel_button)

        private lateinit var productDto: ProductDto

        private val variantIngredientTitles: MutableList<String> = mutableListOf()
        private val variantSpiceTitles: MutableList<String> = mutableListOf()

        init {
            ingredientsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    submitButton.isEnabled = position != 0
                    if (submitButton.isEnabled && variantSpiceTitles.size > 1) {
                        submitButton.isEnabled = spicesSpinner.selectedItemPosition != 0
                    }

                    showUpdatedPrice()
                }
            }

            spicesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    submitButton.isEnabled = position != 0
                    if (submitButton.isEnabled && variantIngredientTitles.size > 1) {
                        submitButton.isEnabled = ingredientsSpinner.selectedItemPosition != 0
                    }

                    showUpdatedPrice()
                }
            }

            increaseQuantityLayout.setOnClickListener {

                if (selectedQuantity >= 200) {
                    context.toast(R.string.product_quantity_limit_reached)
                    return@setOnClickListener
                }

                selectedQuantity++
                quantityTextView.text = selectedQuantity.toString()

                val updatedPrice: Double = if (selectedVariant == null) {
                    productDto.getPrice()
                } else if (selectedVariant!!.price.isNullOrBlank()) {
                    productDto.getPrice()
                } else {
                    selectedVariant!!.price.toDouble()
                }

                priceTextView.text = Util.toFormattedPrice(updatedPrice * selectedQuantity)
                if (variantIngredientTitles.size <= 1 && variantSpiceTitles.size <= 1) {
                    priceLayout.visibility = View.VISIBLE
                }
            }

            decreaseQuantityLayout.setOnClickListener {

                if (selectedQuantity <= 0) {
                    return@setOnClickListener
                }

                selectedQuantity--
                quantityTextView.text = selectedQuantity.toString()

                val updatedPrice: Double = if (selectedVariant == null) {
                    productDto.getPrice()
                } else if (selectedVariant!!.price.isNullOrBlank()) {
                    productDto.getPrice()
                } else {
                    selectedVariant!!.price.toDouble()
                }

                priceTextView.text = Util.toFormattedPrice(updatedPrice * selectedQuantity)
                if (variantIngredientTitles.size <= 1 && variantSpiceTitles.size <= 1) {
                    priceLayout.visibility = View.VISIBLE
                }
            }

            submitButton.setOnClickListener {

                if (!isEditingMode && selectedQuantity < 1) {
                    Toast.makeText(
                            context,
                            "Please choose at least one item in quantity",
                            Toast.LENGTH_SHORT
                    ).show()
                } else {

                    customizationListener.onProductCustomizationRequestAvailable(
                            ProductCustomizationRequest(
                                    // productDto,
                                    selectedQuantity,
                                    selectedVariant
                            )
                    )

                    dialog.dismiss()
                }
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        fun bindItem(productDto: ProductDto) {

            this.productDto = productDto

            nameTextView.text = productDto.getName()
            productImageView.visibility = View.GONE

            // Check if we have ingredients
            if (productDto.getVariants().isEmpty()) {

                Picasso.get()
                        .load(productDto.getImage())
                        .placeholder(R.drawable.image)
                        .into(productImageView)

                priceTextView.text = Util.toFormattedPrice(productDto.getPrice())

                productImageView.visibility = View.VISIBLE
                ingredientsLayout.visibility = View.GONE
                spicesLayout.visibility = View.GONE
                submitButton.isEnabled = true
                increaseQuantityLayout.isEnabled = true
                decreaseQuantityLayout.isEnabled=true
            }

            // We have ingredients
            else {
                ingredientsLayout.visibility = View.VISIBLE
                spicesLayout.visibility = View.VISIBLE

                // VARIANT INGREDIENTS
                variantIngredientTitles.add(context.resources.getString(when {
                    CategoryHelper.isBiryaniCategory(productDto.getCategoryId()) -> R.string.Sauce_type
                    CategoryHelper.isDrinksCategory(productDto.getCategoryId()) -> R.string.choose_types
                    CategoryHelper.isWinesCategory(productDto.getCategoryId()) -> R.string.choose_types
                    CategoryHelper.isKidsCategory(productDto.getCategoryId()) -> R.string.choose_type
                    CategoryHelper.isSideDishesCategory(productDto.getCategoryId()) -> R.string.Choose_spice
                    CategoryHelper.isHouseSpecialsCategory(productDto.getCategoryId()) -> R.string.Choose_spice
                    else -> R.string.Select_ingredients
                }))

                // VARIANT SPICES
                variantSpiceTitles.add(context.resources.getString(when {
                    CategoryHelper.isDrinksCategory(productDto.getCategoryId()) -> R.string.Choose_size
                  //  CategoryHelper.isKidsCategory(productDto.getCategoryId()) -> R.string.choose_type
                    CategoryHelper.isWinesCategory(productDto.getCategoryId()) -> R.string.Choose_size
                    else -> R.string.Choose_spice
                }))

                createVariantIngredientsAndSpicesTitles()

                if(CategoryHelper.isSideDishesCategory(productDto.getCategoryId())){
                    for (variant in productDto.getVariants()) {
                        addIngredientsOrSpicesForVariant(variant)

                    }
                }

                val ingredientsSpinnerAdapter = ArrayAdapter(context, R.layout.spinner_layout, variantIngredientTitles)
                ingredientsSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                ingredientsSpinner.adapter = ingredientsSpinnerAdapter

                val spicesSpinnerAdapter = ArrayAdapter(context, R.layout.spinner_layout, variantSpiceTitles)
                spicesSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spicesSpinner.adapter = spicesSpinnerAdapter
            }

            if (isEditingMode) {
                showPresetData()
            }
        }

        private fun createVariantIngredientsAndSpicesTitles() {

            for (variant in productDto.getVariants()) {

                if (DietHelper.isAny(dietType)) {
                    addIngredientsOrSpicesForVariant(variant)
                }
                else if (DietHelper.isVegan(dietType)
                        && variant.isVegan()) {
                    addIngredientsOrSpicesForVariant(variant)
                }
                else if (DietHelper.isVeg(dietType)
                        && variant.isVeg()) {
                    addIngredientsOrSpicesForVariant(variant)
                }
                else if (DietHelper.isVegOrVegan(dietType)
                        && (variant.isVegan() || variant.isVeg())) {

                    addIngredientsOrSpicesForVariant(variant)
                }
                else if (DietHelper.isNonVeg(dietType) && variant.isNonVeg()) {
                    addIngredientsOrSpicesForVariant(variant)
                }
            }

            if (variantSpiceTitles.size == 1) {
                spicesLayout.visibility = View.GONE
            }
        }

        private fun addIngredientsOrSpicesForVariant(variant: Variant) {

            if (!variantIngredientTitles.contains(variant.name)) {
                variantIngredientTitles.add(variant.name)
            }

            if (!variant.size.isNullOrBlank()) {
                if (!variantSpiceTitles.contains(variant.size)) {
                    variantSpiceTitles.add(variant.size)
                }
            }
        }

        private fun showUpdatedPrice() {
            selectedVariant = null
            priceLayout.visibility = View.GONE
            submitButton.isEnabled = false
            increaseQuantityLayout.isEnabled = false
            decreaseQuantityLayout.isEnabled=false
            if (ingredientsSpinner.selectedItemPosition > 0) {

                if (variantSpiceTitles.size > 1) {
                    if (spicesSpinner.selectedItemPosition != 0) {

                        selectedVariant = findVariantForIngredientAndSpice(
                                ingredientsSpinner.selectedItem as String,
                                spicesSpinner.selectedItem as String
                        )
                    }
                }

                // No Spices
                else {
                    selectedVariant = findVariantForIngredient(
                            ingredientsSpinner.selectedItem as String
                    )
                }
            }

            if (selectedVariant != null) {

                if (selectedQuantity == 0 && cartClick1 ==0) {
                    selectedQuantity++
                    quantityTextView.text = selectedQuantity.toString()
                }

                if(cartClick1==1){
                    selectedQuantity = productDto.getQuantity()
                    quantityTextView.text = selectedQuantity.toString()
                   // cartClick1=0
                }

                val variantPrice = if (selectedVariant!!.price.isNullOrBlank()) {
                    productDto.getPrice()
                } else {
                    selectedVariant!!.price.toDouble()
                }

                val selectedVariantPrice = selectedQuantity * variantPrice
                priceTextView.text = Util.toFormattedPrice(selectedVariantPrice)
                priceLayout.visibility = View.VISIBLE

                submitButton.isEnabled = true
                increaseQuantityLayout.isEnabled = true
                decreaseQuantityLayout.isEnabled=true
            }
        }

        private fun findVariantForIngredient(ingredientName: String): Variant? {

            for (variant in productDto.getVariants()) {
                if (variant.name == ingredientName) {
                    return variant
                }
            }

            return null
        }

        private fun findVariantForIngredientAndSpice(ingredientName: String, spiceName: String): Variant? {

            for (variant in productDto.getVariants()) {
                if (variant.name == ingredientName) {
                    if (variant.size == spiceName) {
                        return variant
                    }
                }
            }

            return null
        }

        private fun showPresetData () {
            selectedQuantity = productDto.getQuantity()
            quantityTextView.text = "${selectedQuantity}"

            val totalPrice: Double = productDto.getPrice() * selectedQuantity
            priceTextView.text = Util.toFormattedPrice(totalPrice)
            priceLayout.visibility = View.VISIBLE

            ingredientsSpinner.setSelectionByValue(productDto.getSelectedIngredient())
            spicesSpinner.setSelectionByValue(productDto.getSelectedIngredientSize())
        }
    }
}