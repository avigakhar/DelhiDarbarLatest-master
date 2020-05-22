package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class Review(
        val created_at: String?,
        val description: String?,
        val id: String?,
        val order_id: String?,
        val price: String?,
        val rating: String?,
        val rest_address: String?,
        val rest_name: String?,
        val user_id: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(created_at)
        writeString(description)
        writeString(id)
        writeString(order_id)
        writeString(price)
        writeString(rating)
        writeString(rest_address)
        writeString(rest_name)
        writeString(user_id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Review> = object : Parcelable.Creator<Review> {
            override fun createFromParcel(source: Parcel): Review = Review(source)
            override fun newArray(size: Int): Array<Review?> = arrayOfNulls(size)
        }
    }
}