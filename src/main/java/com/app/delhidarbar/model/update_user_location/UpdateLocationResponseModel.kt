package com.app.delhidarbar.model.update_user_location

data class UpdateLocationResponseModel(
    val error: Boolean,
    val message: String,
    val user: User
)