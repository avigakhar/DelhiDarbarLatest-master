package com.app.delhidarbar.helper

object Util {

    fun toFormattedPrice (any : Any) : String {
        return String.format("%.2f", any) + CURRENCY
    }
}