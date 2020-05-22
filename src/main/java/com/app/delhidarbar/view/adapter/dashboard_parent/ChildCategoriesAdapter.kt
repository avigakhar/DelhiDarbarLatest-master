package com.app.delhidarbar.view.adapter.dashboard_parent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.dashboard.home.Category
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.view.fragments.HomeFragment

class ChildCategoriesAdapter(private var context: Context, private var restaurants: List<Category>) : RecyclerView.Adapter<ChildCategoriesAdapter.ViewHolder>() {
    var clickWithViewCart: ClickVIewCartInterface? = null

    fun performItemClick(clickVIewCartInterface: ClickVIewCartInterface) {
        this.clickWithViewCart = clickVIewCartInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_categories, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_categories.text = restaurants[position].name

        holder.itemView.setOnClickListener {
            holder.tv_categories.setTextColor(ContextCompat.getColor(context,android.R.color.black))
            clickWithViewCart!!.iWant2EnableMyViewCart(position, 0, 0.0, "")
            HomeFragment.lastCheckedPos = position
            notifyDataSetChanged()
        }

        if (HomeFragment.lastCheckedPos != position) {
           // holder.tv_categories.setTextColor(ContextCompat.getColor(context,android.R.color.black))
            holder.tv_categories.setBackgroundResource(R.drawable.button_round_shape_light_grey)
        } else{
            DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.SELECTED_VALUE,restaurants[position].name)
           // holder.tv_categories.setTextColor(ContextCompat.getColor(context,android.R.color.white))
            holder.tv_categories.setBackgroundResource(R.drawable.button_round_shape)
        }

    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv_categories: TextView = itemView.findViewById(R.id.tv_categories)

    }
}