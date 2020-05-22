package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class User(
        var email: String?,
        var fgid: String?,
        var id: Int,
        var image: String?,
        var background_image: String?,
        var location_address: String?,
        var location_latitude: String?,
        var location_longitude: String?,
        var login_type: String?,
        var name: String?,
        var phone: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
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
        writeString(email)
        writeString(fgid)
        writeInt(id)
        writeString(image)
        writeString(background_image)
        writeString(location_address)
        writeString(location_latitude)
        writeString(location_longitude)
        writeString(login_type)
        writeString(name)
        writeString(phone)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}