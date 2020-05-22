package com.app.delhidarbar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.get_all_restaurant.RestaurantsItem
import com.app.delhidarbar.model.restaurent.Restaurant
import com.app.delhidarbar.utils.ClickWithText

class AdapterSelectRest1(private val context: Context, private val restaurants: List<RestaurantsItem>?) : RecyclerView.Adapter<AdapterSelectRest1.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null
    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_select_restaurent, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.restaurent_name.text = restaurants?.get(position)?.name
        holder.restaurent_address.text = restaurants?.get(position)?.address
        //holder.progress_bar.visibility = View.VISIBLE

      //  DelhiDarbar.instance!!.commonMethods.setImageWithLoader(restaurants?.get(position)?.image, holder.restaurentImage, holder.progress_bar)

        holder.itemView.setOnClickListener {
            clickWithTxt?.itemClick(position, "")
        }
    }

    override fun getItemCount(): Int {
        return restaurants!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var restaurentImage: ImageView
//        var progress_bar: ProgressBar
        var restaurent_name: TextView
        var restaurent_address: TextView

        init {
           // progress_bar = itemView.findViewById(R.id.progress_bar)
            restaurent_name = itemView.findViewById(R.id.tv_name)
            restaurent_address = itemView.findViewById(R.id.tv_address)
           // restaurentImage = itemView.findViewById(R.id.diagonal)
        }
    }
}
