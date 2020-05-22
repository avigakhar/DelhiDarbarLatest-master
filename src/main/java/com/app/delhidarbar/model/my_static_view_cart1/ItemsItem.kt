package com.app.delhidarbar.model.my_static_view_cart1

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ItemsItem(
        @SerializedName("amount") val amount: Double? = null,
        @SerializedName("quantity") var quantity: String? = null,
        @SerializedName("item_desc") val itemDesc: String? = null,
        @SerializedName("item_id") val itemId: String? = null,
        @SerializedName("item_price") val itemPrice: Double? = null,
        @SerializedName("item_image") val itemImage: String? = null,
        @SerializedName("item_name") val itemName: String? = null,
        @SerializedName("select_type") val select_type: String? = null,
        @SerializedName("id") val id: String? = null,
        @SerializedName("order_id") val orderId: String? = null,
        @SerializedName("category_id") val category_id: String? = null,
        @SerializedName("reviews") val reviews: Int,
        @SerializedName("type") val type: String? = null,
        @SerializedName("all_type") val all_type: String? = null,
        @SerializedName("rating") val rating: Int

) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt()

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(amount)
        writeString(quantity)
        writeString(itemDesc)
        writeString(itemId)
        itemPrice?.let { writeDouble(it) }
        writeString(itemImage)
        writeString(itemName)
        writeString(id)
        writeString(orderId)
        writeString(select_type)
        writeString(category_id)
        reviews?.let { writeInt(it) }
        writeString(type)
        writeString(all_type)
        rating?.let { writeInt(it) }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ItemsItem> = object : Parcelable.Creator<ItemsItem> {
            override fun createFromParcel(source: Parcel): ItemsItem = ItemsItem(source)
            override fun newArray(size: Int): Array<ItemsItem?> = arrayOfNulls(size)
        }
    }
}
