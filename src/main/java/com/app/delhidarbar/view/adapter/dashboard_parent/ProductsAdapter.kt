package com.app.delhidarbar.view.adapter.dashboard_parent

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.CartProductController
import com.app.delhidarbar.helper.Util
import com.app.delhidarbar.helper.toast
import com.app.delhidarbar.model.dashboard.home.Product
import com.app.delhidarbar.product_dialog.*
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.RealmController
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import android.graphics.drawable.ColorDrawable
import kotlinx.android.synthetic.main.dialog_description.*

class ProductsAdapter(
        private val context: Activity,
        private val dietType: String = DietHelper.ANY,
        private var products: ArrayList<Product>,
        private var isFirstTime:Boolean) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    companion object {
        var list: MutableList<Product> = mutableListOf()
        var annualPrice: Double = 0.0
    }

    val cartProductController = CartProductController()
    var clickWithTxt: ClickVIewCartInterface? = null

    fun performItemClick(clickWithTxt: ClickVIewCartInterface) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_dashboard, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("listsize:::", products.size.toString())
        holder.bindItem(products[position])

        if(position == products.size){
            DashboardParentAdapter.isLoadingg = false
        }
    }

    override fun getItemCount(): Int = products.size

    private fun showDescriptionDialog (product: Product) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_description)
        dialog.setCancelable(true)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        Picasso.get().load(product.image).into(dialog.imageView)

        dialog.customise_txt_name.text = product.name
        dialog.customise_txt_description.text = product.description
        dialog.customise_txt_price.text = Util.toFormattedPrice(product.price)

        dialog.setOnKeyListener { dialogInterface, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialogInterface!!.dismiss()
            }
            true
        }
    }

    fun addData(listItems: List<Product>) {
        var size = this.products.size
        this.products.clear()
        this.products.addAll(listItems)
        var sizeNew = this.products.size
        notifyItemRangeChanged(size, sizeNew)
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_item_title: TextView = itemView.findViewById(R.id.tv_item_title)
        var tv_item_cost: TextView = itemView.findViewById(R.id.tv_item_cost)
        var txt_ingredient: TextView = itemView.findViewById(R.id.txt_spice_level)
        // var txt_spice: TextView = itemView.findViewById(R.id.txt_spice_level)
        var tv_item_desc: TextView = itemView.findViewById(R.id.tv_item_desc)
        var iv_mealImage: ImageView = itemView.findViewById(R.id.iv_mealImage)
        var tv_total_reviews: TextView = itemView.findViewById(R.id.tv_total_reviews)
        var rl_minus: RelativeLayout = itemView.findViewById(R.id.rl_minus)
        var rl_add: RelativeLayout = itemView.findViewById(R.id.rl_add)
        var tv_item_count: TextView = itemView.findViewById(R.id.tv_item_count)
        var lay_customize: LinearLayout = itemView.findViewById(R.id.lay_customize)
        var lay_addRemove: RelativeLayout = itemView.findViewById(R.id.lay_addRemove)
        var lay_desc: LinearLayout = itemView.findViewById(R.id.lay_desc)
        var rl_add1: RelativeLayout = itemView.findViewById(R.id.rl_add1)
        var rb_ratingBar: RatingBar = itemView.findViewById(R.id.rb_ratingBar)
        // var lay_item: LinearLayout = itemView.findViewById(R.id.lay_item)
        var pb_progressBar: ProgressBar = itemView.findViewById(R.id.pb_progressBar)

        var quantityChangeLayout: View = itemView.findViewById(R.id.quantity_change_layout)
        var increaseQuantityLayout: View = itemView.findViewById(R.id.increase_quantity_layout)
        var increaseQuantityImageView: ImageView = itemView.findViewById(R.id.increase_quantity_image_view)
        var decreaseQuantityLayout: View = itemView.findViewById(R.id.decrease_quantity_layout)
        var decreaseQuantityImageView: ImageView = itemView.findViewById(R.id.decrease_quantity_image_view)

        lateinit var product: Product

        init {
            increaseQuantityLayout.setOnClickListener {
                cartProductController.addItem(
                        product,
                        quantity = 1,
                        itemPrice = product.price
                )

                if (RealmController.getInstance().count > 1) {
                    context.toast("${RealmController.getInstance().count} " + context.resources.getString(R.string.items_added))
                } else {
                    context.toast("${RealmController.getInstance().count} " + context.resources.getString(R.string.item_added))
                }

                // underlying function isn't event using this data
                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, list.size, actualPriceOfItems = annualPrice, from = "")
                refreshQuantity()
            }

            decreaseQuantityLayout.setOnClickListener {
                cartProductController.removeItem(
                        product,
                        quantity = 1,
                        itemPrice = product.price
                )

                context.toast(R.string.item_removed)

                // underlying function isn't event using this data
                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, list.size, actualPriceOfItems = annualPrice, from = "")
                refreshQuantity()
            }

            lay_customize.setOnClickListener {
                ProductCustomizationDialog(context, 0, dietType, customizationListener = object : ProductCustomizationListener {
                    override fun onProductCustomizationRequestAvailable(request: ProductCustomizationRequest) {

                        val productPricePerUnit: Double = when {
                            request.selectedVariant == null -> product.price
                            request.selectedVariant.price.isBlank() -> product.price
                            else -> request.selectedVariant.price.toDouble()
                        }

                        cartProductController.addItem(
                                product,
                                request.selectedVariant,
                                request.selectedQuantity,
                                productPricePerUnit
                        )

                        if (RealmController.getInstance().count > 1) {
                            context.toast("${RealmController.getInstance().count} ${context.resources.getString(R.string.items_added)}")
                        } else {
                            context.toast("${RealmController.getInstance().count} ${context.resources.getString(R.string.item_added)}")
                        }

                        clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, list.size, actualPriceOfItems = annualPrice, from = "")
                        notifyItemChanged(adapterPosition)
                    }

                }).show(ProductDto.of(product))
            }

            iv_mealImage.setOnClickListener {
                showDescriptionDialog(product)
            }
            tv_item_title.setOnClickListener {
                showDescriptionDialog(product)
            }
            tv_item_desc.setOnClickListener {
                showDescriptionDialog(product)
            }
        }

        fun bindItem(product: Product) {
            this.product = product

            // show image
            pb_progressBar.visibility = View.VISIBLE
            if (product.image.isNotBlank()) {
                Picasso.get()
                        .load(product.image)
                        .placeholder(R.drawable.image)
                        .resize(250, 250)
                        .centerCrop()
                        .into(iv_mealImage, object : Callback {
                            override fun onSuccess() {
                                pb_progressBar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                pb_progressBar.visibility = View.GONE
                            }
                        })
                
            } else {
                iv_mealImage.setImageResource(R.drawable.image)
            }

            if (product.select_type == "Cart") {
                lay_customize.visibility = View.GONE
                lay_desc.visibility = View.GONE
                lay_addRemove.visibility = View.VISIBLE

                tv_item_cost.text = Util.toFormattedPrice(product.price)

            } else if (product.select_type == "Select") {
                lay_customize.visibility = View.VISIBLE
                lay_addRemove.visibility = View.GONE

                val lowestPrice = product.getLowestVariantPrice()
                val highestPrice = product.getHighestVariantPrice()

                if (lowestPrice == highestPrice) {
                    tv_item_cost.text = Util.toFormattedPrice(lowestPrice)
                } else {
                    tv_item_cost.text = "${Util.toFormattedPrice(lowestPrice)} - ${Util.toFormattedPrice(highestPrice)}"
                }
            }

            tv_item_desc.text = product.description
            tv_item_title.text = product.name

            if (product.reviews > 0) {
                tv_total_reviews.text = "(" + product.reviews + ")"
            } else {
                tv_total_reviews.text = ""
            }

            rb_ratingBar.rating = product.rating.toFloat()

            refreshQuantity()
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

        private fun refreshQuantity() {
            var isInCart = false
            if (RealmController.getInstance().count > 0) {
                for (existingRealmObject in RealmController.getInstance().all) {
                    if (existingRealmObject.id == product.id && existingRealmObject.quantity > 0) {
                        rl_minus.visibility = View.VISIBLE
                        rl_add1.setBackgroundResource(R.drawable.add_cart_fill)
                        increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
                        tv_item_count.visibility = View.VISIBLE
                        tv_item_count.text = "${existingRealmObject.quantity}"

                        showSelectedAddOns(existingRealmObject.ingredients, existingRealmObject.spices)
                        isInCart = true
                        break
                    }
                }
            }

            if (!isInCart) {
                rl_minus.visibility = View.GONE
                rl_add1.setBackgroundResource(R.drawable.add_cart_item)
                increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.add_to_cart_))
                tv_item_count.visibility = View.GONE
                lay_desc.visibility = View.GONE

            }
        }
    }

}

