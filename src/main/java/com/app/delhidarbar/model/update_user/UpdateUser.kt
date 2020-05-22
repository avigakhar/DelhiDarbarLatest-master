package com.app.delhidarbar.model.update_user

import com.google.gson.annotations.SerializedName

data class UpdateUser(

        @field:SerializedName("error")
        val error: Boolean? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("user")
        val user: User? = null
)