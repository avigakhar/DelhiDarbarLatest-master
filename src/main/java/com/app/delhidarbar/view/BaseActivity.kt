package com.app.delhidarbar.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.delhidarbar.R
import com.app.delhidarbar.view.fragments.LocationFragment
import com.app.delhidarbar.view.fragments.LoginFragment


class BaseActivity : AppCompatActivity() {
    internal var fragmentContianor: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

     // DelhiDarbar.instance?.hideKeyboard(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        }

        setFragmentManager()
    }

    private fun setFragmentManager() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager!!.beginTransaction()
        val login=LoginFragment()
        transaction.addToBackStack("LoginFragment")

        transaction.replace(R.id.fragment_contianor, login).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        /* locationManager.requestLocationUpdates(provider, 400, 1, this);*/
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permisson denied", Toast.LENGTH_SHORT).show()

                }
                return
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()

        } else {
            AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string
                            .really_exit))
                    .setMessage(resources.getString(R.string.do_u_want_to_sure))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 -> finish() }.create().show()
        }

    }

    companion object {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99

        private var fragmentManager: FragmentManager? = null
    }


}

