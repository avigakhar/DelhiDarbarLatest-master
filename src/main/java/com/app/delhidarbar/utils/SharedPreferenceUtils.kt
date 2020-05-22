package com.app.delhidarbar.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtils(context: Context) {
    init {
        sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)
    }

    companion object {
        var sharedPreferences: SharedPreferences? = null

    }
    fun putString(key: String, value: String) {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun getString(key: String): String? {
        return if (sharedPreferences != null) {
            sharedPreferences!!.getString(key, "")
        } else ""
    }

    fun clearAllValues() {
        val prefsEditor = sharedPreferences!!.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }
}
//
//import android.content.Context
//import android.content.SharedPreferences
//import com.braintreepayments.api.internal.BraintreeSharedPreferences.getSharedPreferences
//
//
//
//class SharedPreferenceUtils(internal var mContext: Context) {
//    private val PREF_NAME = "PreferencesDB"
//    internal var preferences: SharedPreferences
//
//    init {
//        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//    }
//    fun putBoolean(key: String, value: Boolean): String {
//        val editor = preferences!!.edit();
//        editor.putBoolean(key, value);
//        editor.apply();
//        return key
//    }
//
//    fun getBoolean(key: String): Boolean {
//        return preferences?.getBoolean(key, false)
//    }
//    fun putString(key: String, value: String): String {
//        val editor = preferences.edit()
//        editor.putString(key, value)
//        editor.apply()
//        return key
//    }
//    fun  clearAllData(){
//        val editor = preferences.edit()
//        editor.clear()
//        editor.commit()
//
//
//    }
//
//    fun getString(key: String): String? {
//        return preferences.getString(key, "")
//
//
//
//
//
//
//
//    }
//}
