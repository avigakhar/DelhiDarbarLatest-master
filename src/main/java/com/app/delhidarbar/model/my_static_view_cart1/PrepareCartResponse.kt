package com.app.delhidarbar.model.my_static_view_cart1

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PrepareCartResponse(
		@SerializedName("restaurant")val restaurant: Restaurant? = null,
		@SerializedName("error")val error: Boolean? = null,
		@SerializedName("amount")val amount: String? = null,
		@SerializedName("message")val message: String? = null,
		@SerializedName("user")val user: User? = null,
		@SerializedName("cart")val cart: List<CartItem?>? = null,
		@SerializedName("client_token")val client_token: String? = null

) : @com.google.gson.annotations.SerializedName("")Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readParcelable(Restaurant::class.java.classLoader),
			parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
			parcel.readString(),
			parcel.readString(),
			parcel.readParcelable(User::class.java.classLoader),
			parcel.createTypedArrayList(CartItem.CREATOR),
			parcel.readString()){
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeParcelable(restaurant, flags)
		parcel.writeValue(error)
		parcel.writeString(amount)
		parcel.writeString(message)
		parcel.writeParcelable(user, flags)
		parcel.writeTypedList(cart)
		parcel.writeString(client_token)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<PrepareCartResponse> {
		override fun createFromParcel(parcel: Parcel): PrepareCartResponse {
			return PrepareCartResponse(parcel)
		}

		override fun newArray(size: Int): Array<PrepareCartResponse?> {
			return arrayOfNulls(size)
		}
	}
}
