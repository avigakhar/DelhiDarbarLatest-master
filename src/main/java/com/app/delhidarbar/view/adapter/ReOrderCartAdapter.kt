package com.app.delhidarbar.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.Util
import com.app.delhidarbar.model.profile_model.OrderItem
import com.app.delhidarbar.utils.ClickVIewCartInterface

class ReOrderCartAdapter(
        private val context: Activity, 
        private val orderItems: ArrayList<OrderItem>) : RecyclerView.Adapter<ReOrderCartAdapter.OrderItemViewHolder>() {

    // var mYquantity = 0
    var clickWithTxt: ClickVIewCartInterface? = null

    // var data: List<Variant> = ArrayList()
    // var list: MutableList<Product> = mutableListOf<Product>()

    fun performItemClick(clickWithTxt: ClickVIewCartInterface) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_dashboardcart, null)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bindItem(orderItems[position])
    }

    override fun getItemCount(): Int = orderItems.size

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_item_title: TextView = itemView.findViewById(R.id.tv_item_title)
        var totalPriceTextView: TextView = itemView.findViewById(R.id.tv_item_cost)
        var unitPriceTextView: TextView = itemView.findViewById(R.id.tv_item_cost1)
        var txt_ingredient: TextView = itemView.findViewById(R.id.txt_spice_level)
        var txt_spice: TextView = itemView.findViewById(R.id.txt_spice_level)
        var rl_minus: View = itemView.findViewById(R.id.rl_minus)
        var rl_add: View = itemView.findViewById(R.id.rl_add)
        var lay_customize: View = itemView.findViewById(R.id.lay_customize)
        var lay_addRemove: View = itemView.findViewById(R.id.lay_addRemove)
        var lay_desc: View = itemView.findViewById(R.id.lay_desc)
        var tv_item_count: TextView = itemView.findViewById(R.id.tv_item_count)

        var quantityChangeLayout: View = itemView.findViewById(R.id.quantity_change_layout)
        var increaseQuantityLayout: View = itemView.findViewById(R.id.increase_quantity_layout)
        var increaseQuantityImageView: ImageView = itemView.findViewById(R.id.increase_quantity_image_view)
        var decreaseQuantityLayout: View = itemView.findViewById(R.id.decrease_quantity_layout)
        var decreaseQuantityImageView: ImageView = itemView.findViewById(R.id.decrease_quantity_image_view)

        lateinit var orderItem: OrderItem

        init {
            increaseQuantityLayout.setOnClickListener {

                orderItem.quantity++

                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, orderItem.quantity, 0.0, "")
                notifyDataSetChanged()
            }

            decreaseQuantityLayout.setOnClickListener {

                if(orderItem.quantity > 0)
                    orderItem.quantity--

                clickWithTxt!!.iWant2EnableMyViewCart(adapterPosition, orderItem.quantity, 0.0, "")
                notifyDataSetChanged()
            }
        }

        fun bindItem (orderItem: OrderItem) {
            this.orderItem = orderItem

            tv_item_title.text = orderItem.item_name

            unitPriceTextView.text = Util.toFormattedPrice(orderItem.item_price)

            if (orderItem.quantity.toInt() >= 0) {
                tv_item_count.text = "${orderItem.quantity}"

                val totalPrice: Double = orderItem.item_price * orderItem.quantity
                totalPriceTextView.text = Util.toFormattedPrice(totalPrice)

                rl_minus.visibility = View.VISIBLE
                rl_add.setBackgroundResource(R.drawable.add_cart_fill)
                increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite))
                tv_item_count.visibility = View.VISIBLE
                tv_item_count.text = "${orderItem.quantity}"

            } /*else {
                rl_minus.visibility = View.VISIBLE
                //rl_add.setBackgroundResource(R.drawable.add_cart_item)
              //  increaseQuantityImageView.setColorFilter(ContextCompat.getColor(context, R.color.add_to_cart_))
               // tv_item_count.visibility = View.GONE
                tv_item_count.text = "${orderItem.quantity}"

            }*/
        }
    }
}
