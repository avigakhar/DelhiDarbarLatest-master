package com.app.delhidarbar.helper

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.StringRes
import com.app.delhidarbar.R


object Dialogs {

    fun showNetworkErrorDialog (context: Context?) : AlertDialog? {
        return showInformationDialog(context, R.string.alert_internetConnection)
    }

    fun showInformationDialog (context: Context?,  @StringRes messageRes : Int) : AlertDialog? {

        if (context == null) {
            return null
        }

        return showInformationDialog(context, context.getString(messageRes))
    }

    fun showInformationDialog (context: Context?, message : String?) : AlertDialog? {

        if (context == null) {
            return null
        }

        if (message == null) {
            return null
        }

        val alertDialog = AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setIcon(R.mipmap.delhi_darbar)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .create()
        alertDialog.show()
        return alertDialog
    }
}