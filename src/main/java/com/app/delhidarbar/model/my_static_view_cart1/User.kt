package com.app.delhidarbar.model.my_static_view_cart1

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("location_address") var locationAddress: String? = null,
        @SerializedName("image") val image: String? = null,
        @SerializedName("fgid") val fgid: String? = null,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("login_type") val loginType: String? = null,
        @SerializedName("location_latitude") val locationLatitude: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("location_longitude") val locationLongitude: String? = null,
        @SerializedName("email") val email: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(locationAddress)
        writeString(image)
        writeString(fgid)
        writeString(phone)
        writeString(loginType)
        writeString(locationLatitude)
        writeString(name)
        writeValue(id)
        writeString(locationLongitude)
        writeString(email)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}
