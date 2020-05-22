package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class ProfileResponeModel(
        val addresses: List<Addresse>,
        val error: Boolean,
        val message: String?,
        val orders: List<Order>,
        val reviews: List<Review>,
        var user: User?,
        var total_addresses: Int?,
        var total_orders: Int?,
        var total_reviews: Int?

) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(Addresse.CREATOR),
            1 == source.readInt(),
            source.readString(),
            source.createTypedArrayList(Order.CREATOR),
            source.createTypedArrayList(Review.CREATOR),
            source.readParcelable<User>(User::class.java.classLoader),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(addresses)
        writeInt((if (error) 1 else 0))
        writeString(message)
        writeTypedList(orders)
        writeTypedList(reviews)
        writeParcelable(user, 0)
        writeValue(total_addresses)
        writeValue(total_orders)
        writeValue(total_reviews)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProfileResponeModel> = object : Parcelable.Creator<ProfileResponeModel> {
            override fun createFromParcel(source: Parcel): ProfileResponeModel = ProfileResponeModel(source)
            override fun newArray(size: Int): Array<ProfileResponeModel?> = arrayOfNulls(size)
        }
    }
}
