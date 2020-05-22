package com.app.delhidarbar.view.adapter.dashboard_parent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.model.dashboard.home.Offer
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.rounded_iv.RoundedImageView
import com.squareup.picasso.Picasso

class ChildOfferAdapter(private val context: Context, private val restaurants: List<Offer>) : RecyclerView.Adapter<ChildOfferAdapter.ViewHolder>() {
    var clickWithTxt: ClickVIewCartInterface? = null
    fun performItemClick(clickWithTxt: ClickVIewCartInterface) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offer_dashboard, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.cv_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gulabi_color))

            if(position == 0){
                holder.cv_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_orange))
            }

        } else {
            if (position % 3 == 0) {
                holder.cv_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_orange))
            } else {
                holder.cv_card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bengan_color))
            }
        }
        holder.itemView.setOnClickListener {
            clickWithTxt!!.iWant2EnableMyViewCart(position,0,0.0,"")
        }
        holder.tv_offer.setText(restaurants[position].percentage + "% OFF")
        holder.tv_disccount.setText(restaurants[position].discount +" "+ context.resources.getString(R.string.Price_symbol))
        holder.tv_first_order.setText(restaurants[position].title)
        holder.tv_offer_code.setText("Code: " +restaurants[position].code)
        Picasso.get().load(restaurants[position].image).into(holder.photo_imageView)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv_card: CardView
        var photo_imageView: RoundedImageView
        var tv_offer_code: TextView
        var tv_disccount: TextView
        var tv_first_order: TextView
        var tv_offer: TextView

        init {
            cv_card = itemView.findViewById(R.id.cv_card)
            photo_imageView = itemView.findViewById(R.id.photo_imageView)
            tv_offer_code = itemView.findViewById(R.id.tv_offer_code)
            tv_disccount = itemView.findViewById(R.id.tv_disccount)
            tv_first_order = itemView.findViewById(R.id.tv_first_order)
            tv_offer = itemView.findViewById(R.id.tv_offer)
        }
    }
}
