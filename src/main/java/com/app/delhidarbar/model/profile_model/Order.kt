package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class Order(
        val created_at: String?,
        val id: String?,
        val items: ArrayList<OrderItem>?,
        val order_number: String?,
        val status: String?,
        val total_amount: Double,
        val user_id: String?,
        val rest_name: String?,
        val has_review: String?

) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.createTypedArrayList(OrderItem.CREATOR) as ArrayList<OrderItem>?,
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString()
    )
   // source.createTypedArrayList(Item.CREATOR) as List<Item>?,

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(created_at)
        writeString(id)
        writeTypedList(items)
        writeString(order_number)
        writeString(status)
        writeDouble(total_amount)
        writeString(user_id)
        writeString(rest_name)
        writeString(has_review)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Order> = object : Parcelable.Creator<Order> {
            override fun createFromParcel(source: Parcel): Order = Order(source)
            override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
        }
    }
}