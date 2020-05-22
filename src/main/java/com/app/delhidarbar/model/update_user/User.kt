package com.app.delhidarbar.model.update_user

import com.google.gson.annotations.SerializedName

data class User(

        @field:SerializedName("location_address")
        val locationAddress: String? = null,

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("fgid")
        val fgid: String? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("login_type")
        val loginType: String? = null,

        @field:SerializedName("location_latitude")
        val locationLatitude: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("location_longitude")
        val locationLongitude: String? = null,

        @field:SerializedName("email")
        val email: String? = null
)