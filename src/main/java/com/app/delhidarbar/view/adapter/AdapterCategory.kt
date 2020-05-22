package com.app.delhidarbar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.model.dashboard.home.Category
import com.app.delhidarbar.utils.ClickWithText


class AdapterCategory(private val context: Context, private val restaurants: MutableList<Category>) : RecyclerView.Adapter<AdapterCategory.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null

    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text_for_book_a_table, null)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_contentBookTable.text="${this!!.restaurants!![position]!!.name}"
        holder.itemView.setOnClickListener {
            clickWithTxt!!.itemClick(position,"")
        }
    }

    override fun getItemCount(): Int {
        return restaurants!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv_contentBookTable= itemView.findViewById<TextView>(R.id.tv_contentBookTable) as TextView


    }
}
