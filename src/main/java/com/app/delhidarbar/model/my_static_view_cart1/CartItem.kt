package com.app.delhidarbar.model.my_static_view_cart1

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CartItem(
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("total_amount") val totalAmount: Double? = null,
        @SerializedName("order_number") val orderNumber: String? = null,
        @SerializedName("id") val id: String? = null,
        @SerializedName("items") val items: List<ItemsItem?>? = null,
        @SerializedName("status") val status: String? = null,
        @SerializedName("discount") val discount: Double? = null,
        @SerializedName("discount_amount") val discount_amount: Double? = null

) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.createTypedArrayList(ItemsItem.CREATOR),
            source.readString(),
            source.readDouble(),
            source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userId)
        writeValue(totalAmount)
        writeString(orderNumber)
        writeString(id)
        writeTypedList(items)
        writeString(status)
        writeValue(discount)
        writeValue(discount_amount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CartItem> = object : Parcelable.Creator<CartItem> {
            override fun createFromParcel(source: Parcel): CartItem = CartItem(source)
            override fun newArray(size: Int): Array<CartItem?> = arrayOfNulls(size)
        }
    }
}
