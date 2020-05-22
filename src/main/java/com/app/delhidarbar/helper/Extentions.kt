package com.app.delhidarbar.helper

import android.app.Activity
import android.content.Context
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast (@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}

fun Context.toast (message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.getContext () : Context {
    return this
}

fun Spinner.setSelectionByValue (value: Any) {
    for (i in 0 until count) {
        if (getItemAtPosition(i) == value) {
            setSelection(i)
            break
        }
    }
}