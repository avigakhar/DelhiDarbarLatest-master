package com.app.delhidarbar.model.my_static_view_cart1

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Restaurant(
		@SerializedName("address")val address: String? = null,
		@SerializedName("user_id")val userId: String? = null,
		@SerializedName("name")val name: String? = null,
		@SerializedName("id")val id: String? = null
) : Parcelable {
	constructor(source: Parcel) : this(
			source.readString(),
			source.readString(),
			source.readString(),
			source.readString()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(address)
		writeString(userId)
		writeString(name)
		writeString(id)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Restaurant> = object : Parcelable.Creator<Restaurant> {
			override fun createFromParcel(source: Parcel): Restaurant = Restaurant(source)
			override fun newArray(size: Int): Array<Restaurant?> = arrayOfNulls(size)
		}
	}
}
