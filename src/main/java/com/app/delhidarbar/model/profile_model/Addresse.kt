package com.app.delhidarbar.model.profile_model

import android.os.Parcel
import android.os.Parcelable

data class Addresse(
        val address: String?,
        val created_at: String?,
        val id: String?,
        val name: String?,
        val user_id: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(address)
        writeString(created_at)
        writeString(id)
        writeString(name)
        writeString(user_id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Addresse> = object : Parcelable.Creator<Addresse> {
            override fun createFromParcel(source: Parcel): Addresse = Addresse(source)
            override fun newArray(size: Int): Array<Addresse?> = arrayOfNulls(size)
        }
    }
}