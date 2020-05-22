package com.app.delhidarbar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.model.get_all_restaurant.RestaurantsItem
import com.app.delhidarbar.utils.ClickWithText

class AdapterBookATable(private val context: Context, var type: Int, var restaurantsItem: List<RestaurantsItem?>?, var parties: List<String?>?) : RecyclerView.Adapter<AdapterBookATable.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null
    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text_for_book_a_table, null)
        return ViewHolder(view, 0)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            //restaurant
           1 -> {
             holder.tv_contentBookTable.text="${this!!.restaurantsItem!![position]!!.name}"
               holder.itemView.setOnClickListener { clickWithTxt!!.itemClick(position,"1") }
            }

            //parties
           2-> {
               holder.tv_contentBookTable.text="${this!!.parties!![position]!!.toString()}"
               holder.itemView.setOnClickListener { clickWithTxt!!.itemClick(position,"2") }
            }

        }

    }

    override fun getItemCount(): Int {
        when (type) {
            1 -> {
                return restaurantsItem!!.size
            }
            2-> {
                return parties!!.size
            }

        }
        return 0
    }

    inner class ViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
        // item_orders
         var tv_contentBookTable= itemView.findViewById<TextView>(R.id.tv_contentBookTable) as TextView


    }
}
