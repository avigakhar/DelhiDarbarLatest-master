package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class OrderItem(
        val amount: Double,
        val id: String?,
        val item_desc: String?,
        val item_id: String?,
        val item_image: String?,
        val item_name: String?,
        val item_price: Double,
        val order_id: String?,
        var quantity: Int = 0,
        var in_item_id: String?

) : Parcelable {
    constructor(source: Parcel) : this(
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(amount)
        writeString(id)
        writeString(item_desc)
        writeString(item_id)
        writeString(item_image)
        writeString(item_name)
        writeDouble(item_price)
        writeString(order_id)
        writeInt(quantity)
        writeString(in_item_id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderItem> = object : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(source: Parcel): OrderItem = OrderItem(source)
            override fun newArray(size: Int): Array<OrderItem?> = arrayOfNulls(size)
        }
    }
}