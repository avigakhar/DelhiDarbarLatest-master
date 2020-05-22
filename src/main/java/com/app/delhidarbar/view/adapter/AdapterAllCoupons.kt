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
import com.app.delhidarbar.model.view_all_coupons.CouponsItem
import com.app.delhidarbar.utils.ClickWithText
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class AdapterAllCoupons(private val context: Context, private val restaurants: List<CouponsItem?>?) : RecyclerView.Adapter<AdapterAllCoupons.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null

    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, null)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.pb_progressBar.visibility = View.VISIBLE
        holder.tv_couponName.text = restaurants?.get(position)!!.title
        holder.tv_offPercentage.text = "${restaurants[position]!!.percentage.toString()} % ${context.resources.getString(R.string.OFF)}"
        holder.tv_couponCode.text = "${context.resources.getString(R.string.Code)} ${restaurants[position]!!.code} "
        holder.tv_disCountUpto.text = "${restaurants[position]!!.discount}"
        holder.itemView.setOnClickListener {
            clickWithTxt!!.itemClick(position,"")
        }
        Picasso.get().load(restaurants[position]!!.image).into(holder.iv_mealImage, object : Callback {
            override fun onSuccess() {
                holder.pb_progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.pb_progressBar.visibility = View.GONE
            }
        })
    }

    override fun getItemCount(): Int {
        return restaurants!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var pb_progressBar: ProgressBar = itemView.findViewById(R.id.pb_progressBar)
        var vw: View = itemView.findViewById(R.id.vw)
        var iv_mealImage: ImageView = itemView.findViewById(R.id.iv_mealImage)
        var tv_couponName: TextView = itemView.findViewById(R.id.tv_couponName)
        var tv_offPercentage: TextView = itemView.findViewById(R.id.tv_offPercentage)
        var tv_couponCode: TextView = itemView.findViewById(R.id.tv_couponCode)
        var tv_disCountUpto: TextView = itemView.findViewById(R.id.tv_disCountUpto)

    }
}
