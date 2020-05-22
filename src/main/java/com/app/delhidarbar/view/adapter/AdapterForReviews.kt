package com.app.delhidarbar.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.model.profile_model.Addresse
import com.app.delhidarbar.model.profile_model.Order
import com.app.delhidarbar.model.profile_model.Review
import com.app.delhidarbar.utils.ClickWithText
import java.text.ParseException
import java.text.SimpleDateFormat

class AdapterForReviews(private val context: Context, var type: Int, var reviews: List<Review>, var addresse: List<Addresse>, var order: List<Order>) : RecyclerView.Adapter<AdapterForReviews.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null

    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(type, null)
        return ViewHolder(view, type)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            R.layout.item_orders -> {
                if (order.size > 0) {
                    holder.tv_restaurent_name.text = "${order[position].rest_name}"/*+order[position]*/
                    var price:String = String.format("%.2f", order[position].total_amount.toDouble()).toString()
                    holder.tv_price.text = "" + price + " ${context.resources.getString(R.string.Price_symbol)}"

                    if(order[position].has_review.equals("1"))
                        holder.img_review.visibility = View.INVISIBLE

                    var aftrConcatinated = ""
                    if(order[position].items!=null){
                        for (i in 0 until order[position].items!!.size) {
                            if (i == 0) {
                                if(order[position].items!![i].item_name!=null)
                                    aftrConcatinated += order[position].items!![i].item_name
                            } else {
                                if(order[position].items!![i].item_name!=null)
                                    aftrConcatinated += ", ${order[position].items!![i].item_name}"
                            }
                        }
                    }

                    holder.tv_cart_item.text = aftrConcatinated
                    val string = order[position].created_at
                    val fromUser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val myFormat = SimpleDateFormat("dd MMM yyyy,  HH:mm:ss aaa")

                    try {
                        val reformattedStr = myFormat.format(fromUser.parse(string))
                        holder.tv_date.text = "" + reformattedStr
                        Log.e("TAG", "58>>><<<$reformattedStr")
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    holder.img_reorder.setOnClickListener {
                        clickWithTxt!!.itemClick(position, "reorder")
                    }

                    holder.img_review.setOnClickListener {
                        clickWithTxt!!.itemClick(position, "review")
                    }
                }
            }
            //reviews
            R.layout.child_item_review -> {
                if(reviews.size>0) {
                    holder.tv_review_restaurent_name.text = "" + reviews[position].rest_name
                    holder.tv_review_ratingBar.rating = reviews[position].rating!!.toFloat()
                    holder.tv_review__description.text = "" + reviews[position].description
                }
            }
            //addresses
            R.layout.item_addresses -> {
                if (addresse.size > 0) {
                    var myString = addresse[position].name
                    if (myString != null && !myString.equals(""))
                        holder.tv_home.text = myString?.substring(0, 1)?.toUpperCase() + myString?.substring(1)
                    else
                        holder.tv_home.text = addresse[position].name


                    holder.tv_address.text = "${addresse[position].address}"
                    holder.tv_delete_address.setOnClickListener {
                        clickWithTxt!!.itemClick(position, "delete")
                    }
                    holder.img_edit_review.setOnClickListener {
                        clickWithTxt!!.itemClick(position, "edit")
                    }
                }

            }

        }

    }

    override fun getItemCount(): Int {
        when (type) {
            //order
            R.layout.item_orders -> {
                return order.size
            }
            //reviews
            R.layout.child_item_review -> {
                return reviews.size
            }
            //addresses
            R.layout.item_addresses -> {
                return addresse.size
            }
        }
        return 0
    }

    inner class ViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
        // item_orders
        lateinit var tv_restaurent_name: TextView
        lateinit var tv_price: TextView
        lateinit var tv_date: TextView
        lateinit var tv_cart_item: TextView
        lateinit var img_reorder: ImageView
        lateinit var img_review: ImageView

        //child_item_review

        lateinit var tv_review_restaurent_name: TextView
        lateinit var tv_review_ratingBar: RatingBar
        lateinit var tv_review__description: TextView

        //item_addresses

        lateinit var tv_home: TextView
        lateinit var tv_address: TextView
        lateinit var tv_delete_address: ImageView
        lateinit var img_edit_review: ImageView


        init {
            when (type) {
                //order
                R.layout.item_orders -> {
                    tv_restaurent_name = itemView.findViewById<TextView>(R.id.tv_restaurent_name) as TextView
                    tv_price = itemView.findViewById<TextView>(R.id.tv_price) as TextView
                    tv_date = itemView.findViewById<TextView>(R.id.tv_date) as TextView
                    tv_cart_item = itemView.findViewById<TextView>(R.id.tv_cart_item) as TextView
                    img_reorder = itemView.findViewById<ImageView>(R.id.img_reorder) as ImageView
                    img_review = itemView.findViewById<ImageView>(R.id.img_review) as ImageView

                }
                //reviews
                R.layout.child_item_review -> {

                    tv_review_restaurent_name = itemView.findViewById<TextView>(R.id.tv_review_restaurent_name) as TextView
                    tv_review_ratingBar = itemView.findViewById<RatingBar>(R.id.tv_review_ratingBar) as RatingBar
                    tv_review__description = itemView.findViewById<TextView>(R.id.tv_review__description) as TextView


                } //addresses

                R.layout.item_addresses -> {

                    tv_home = itemView.findViewById<TextView>(R.id.tv_home) as TextView
                    tv_address = itemView.findViewById<TextView>(R.id.tv_address) as TextView
                    tv_delete_address = itemView.findViewById<ImageView>(R.id.tv_delete_address) as ImageView
                    img_edit_review = itemView.findViewById<ImageView>(R.id.img_edit_review) as ImageView


                }
            }

        }

    }
}
