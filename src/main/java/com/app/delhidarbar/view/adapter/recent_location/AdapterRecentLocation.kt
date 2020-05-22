package com.app.delhidarbar.view.adapter.recent_location

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.model.recent_location.LocationsItem
import com.app.delhidarbar.utils.ClickWithText

class AdapterRecentLocation(private val context: Context, var locations: List<LocationsItem?>?) : RecyclerView.Adapter<AdapterRecentLocation.ViewHolder>() {
    var clickWithTxt: ClickWithText? = null
    var row_index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recents_locations, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_name.text = locations?.get(position)?.locationAddress
        holder.itemView.setOnClickListener {
            clickWithTxt?.itemClick(position,"")
        }

        holder.lay_address.setOnClickListener { view ->
           // row_index = position
            clickWithTxt?.itemClick(position,"")

            if(row_index==position){
                row_index=-1;
                notifyDataSetChanged();
                return@setOnClickListener
            }
            row_index = position;
            notifyDataSetChanged();
        }

        if (row_index == position) {
           // holder.rd_name.setText("Default Address");
            holder.rd_name.setChecked(true);
            //holder.rd_name.setTextColor(context.getResources().getColor(R.color.dark_orange));
            holder.lay_address.setBackgroundColor(context.getResources().getColor(R.color.light_add_to_cart_));

            //holder.txt_name.text = locations?.get(position)?.locationAddress

        } else {
            holder.rd_name.setChecked(false);
          //  holder.txt_name.text = locations?.get(position)?.locationAddress

            holder.lay_address.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    override fun getItemCount(): Int {
        return locations!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_name: TextView
        val txt_address:TextView
        val txt_mobile:TextView
        val rd_name: RadioButton
        val lay_address:LinearLayout
        val lay_name:LinearLayout

        init {
            rd_name = itemView.findViewById(R.id.rd_name);

            txt_name = itemView.findViewById(R.id.tv_adpter_recent_locations);
            txt_address = itemView.findViewById(R.id.txt_address);
            txt_mobile = itemView.findViewById(R.id.txt_mobile);

            lay_address = itemView.findViewById(R.id.address_layout);
            lay_name = itemView.findViewById(R.id.lay_name);
        }

    }


    fun performItemClick(clickWithTxt: ClickWithText) {
        this.clickWithTxt = clickWithTxt
    }
}
