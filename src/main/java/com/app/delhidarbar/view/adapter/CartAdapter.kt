package com.app.delhidarbar.view.adapter

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.CartProductController
import com.app.delhidarbar.helper.Util
import com.app.delhidarbar.product_dialog.ProductCustomizationDialog
import com.app.delhidarbar.product_dialog.ProductCustomizationListener
import com.app.delhidarbar.product_dialog.ProductCustomizationRequest
import com.app.delhidarbar.product_dialog.ProductDto
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.RealmProduct
import com.app.delhidarbar.view.CartActivity

class CartAdapter (
        private val context: Activity,
        private val realmProducts: MutableList<RealmProduct>
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    val cartProductController = CartProductController()
    var clickWithTxt: ClickVIewCartInterface? = null

    fun performItemClick(clickWithTxt: ClickVIewCartInterface) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_dashboardcart, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(realmProducts[position])
    }

    override fun getItemCount(): Int = realmProducts.size

    /*
     * VIEW HOLDER
     */

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv_item_title: TextView = itemView.findViewById(R.id.tv_item_title)
        var totalPriceTextView: TextView = itemView.findViewById(R.id.tv_item_cost)
        var unitPriceTextView: TextView = itemView.findViewById(R.id.tv_item_cost1)
        var txt_ingredient: TextView = itemView.findViewById(R.id.txt_spice_level)
        // var txt_spice: TextView = itemView.findViewById(R.id.txt_spice_level)
        var rl_minus: RelativeLayout = itemView.findViewById(R.id.rl_minus)
        var rl_add: RelativeLayout = itemView.findViewById(R.id.rl_add)
        var tv_item_count: TextView = itemView.findViewById(R.id.tv_item_count)
        var lay_customize: LinearLayout = itemView.findViewById(R.id.lay_customize)
        var lay_addRemove: RelativeLayout = itemView.findViewById(R.id.lay_addRemove)
        var lay_desc: LinearLayout = itemView.findViewById(R.id.lay_desc)

        var quantityChangeLayout: View = itemView.findViewById(R.id.quantity_change_layout)
        var increaseQuantityLayout: View = itemView.findViewById(R.id.increase_quantity_layout)
        var increaseQuantityImageView: ImageView = itemView.findViewById(R.id.increase_quantity_image_view)
        var decreaseQuantityLayout: View = itemView.findViewById(R.id.decrease_quantity_layout)
        var decreaseQuantityImageView: ImageView = itemView.findViewById(R.id.decrease_quantity_image_view)

        lateinit var realmProduct: RealmProduct

        init {
            increaseQuantityLayout.setOnClickListener {
                cartProductController.addItem(
                        realmProduct,
                        quantity = 1,
                        itemPrice = realmProduct.price
                )

                // underlying function isn't event using this data
                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, realmProduct.quantity, 0.0, "")
                notifyDataSetChanged()
            }

            decreaseQuantityLayout.setOnClickListener {
                cartProductController.removeItem(
                        realmProduct,
                        quantity = 1,
                        itemPrice = realmProduct.price
                )
                // underlying function isn't event using this data
                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, itemCount, 0.0, "")
                notifyDataSetChanged()
            }

            lay_customize.setOnClickListener {

                ProductCustomizationDialog(context, 1, isEditingMode = true, customizationListener = object : ProductCustomizationListener {

                    override fun onProductCustomizationRequestAvailable(request: ProductCustomizationRequest) {

                        val productPricePerUnit: Double = when {
                            request.selectedVariant == null -> realmProduct.price
                            request.selectedVariant.price.isBlank() -> realmProduct.price
                            else -> request.selectedVariant.price.toDouble()
                        }

                        if (request.selectedQuantity > realmProduct.quantity) {
                            cartProductController.addItem(
                                    realmProduct,
                                    request.selectedVariant,
                                    request.selectedQuantity - realmProduct.quantity,
                                    productPricePerUnit
                            )
                        }
                        else {
                            cartProductController.removeItem(
                                    realmProduct,
                                    request.selectedVariant,
                                    realmProduct.quantity - request.selectedQuantity,
                                    productPricePerUnit
                            )
                        }

//                        notifyItemChanged(adapterPosition)

                        if (context is CartActivity) {
                            context.updateCartDataFromRealms()
                        }
                    }
                }).show(ProductDto.of(realmProduct))
            }
        }

        fun bindItem(realmProduct : RealmProduct) {

            this.realmProduct = realmProduct

            tv_item_title.text = realmProduct.name
            tv_item_count.text = "${realmProduct.quantity}"

            val totalPrice: Double = realmProduct.price * realmProduct.quantity
            totalPriceTextView.text = Util.toFormattedPrice(totalPrice)

            unitPriceTextView.text = Util.toFormattedPrice(realmProduct.price)

            if (realmProduct.select_type == "Cart") {
                lay_customize.visibility = View.GONE
                lay_desc.visibility = View.GONE
                lay_addRemove.visibility = View.VISIBLE
            } else if (realmProduct.select_type == "Select") {
                lay_customize.gravity = Gravity.END
                lay_addRemove.visibility = View.GONE
                lay_customize.visibility = View.VISIBLE
                lay_desc.visibility = View.VISIBLE

                showSelectedAddOns(realmProduct.ingredients, realmProduct.spices)
            }

            if (realmProduct.quantity > 0) {
                rl_minus.visibility = View.VISIBLE
                rl_add.setBackgroundResource(R.drawable.add_cart_fill)
                increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
                tv_item_count.visibility = View.VISIBLE
                tv_item_count.text = ("" + realmProduct.quantity)

            } else {
                rl_minus.visibility = View.GONE
                rl_add.setBackgroundResource(R.drawable.add_cart_item)
                increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.add_to_cart_))
                tv_item_count.visibility = View.GONE
            }
        }

        private fun showSelectedAddOns (selectedIngredients : String, selectedSizes: String) {

            if (selectedIngredients.isBlank() && selectedSizes.isBlank()) {
                lay_desc.visibility = View.GONE
                txt_ingredient.text = ""
            } else if (!selectedIngredients.isBlank() && !selectedSizes.isBlank()) {
                lay_desc.visibility = View.VISIBLE
                txt_ingredient.text = "${selectedIngredients} & ${selectedSizes}"
            } else if (!selectedIngredients.isBlank()) {
                lay_desc.visibility = View.VISIBLE
                txt_ingredient.text = selectedIngredients
            } else if (!selectedSizes.isBlank()) {
                lay_desc.visibility = View.VISIBLE
                txt_ingredient.text = selectedSizes
            } else {
                lay_desc.visibility = View.GONE
            }
        }
    }
}
