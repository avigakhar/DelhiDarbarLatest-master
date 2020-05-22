package com.app.delhidarbar.model.update_user_location

data class User(
    val email: String,
    val fgid: String,
    val id: Int,
    val image: String,
    val location_address: String,
    val location_latitude: String,
    val location_longitude: String,
    val login_type: String,
    val name: String,
    val phone: String
)