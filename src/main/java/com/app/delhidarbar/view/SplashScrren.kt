package com.app.delhidarbar.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.app.delhidarbar.R
import android.widget.Toast
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.RealmController
import com.app.delhidarbar.utils.location.LocationUpdatesService
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import org.json.JSONObject
import org.jsoup.Jsoup

class SplashScrren : AppCompatActivity() {
    private var counter = 0
    val REQUEST_LOCATION = 1999
    private val secondsDelayed = 1
    lateinit var currentVersion:String
    lateinit var latestVersion:String
    private var isFromSetting = false
    private var imageView: ImageView? = null
    private var mHandler: IncomingMessageHandler? = null
    internal lateinit var progress_bar: ProgressBar
    private lateinit var googleApiClient: GoogleApiClient
    internal var update = false
    internal lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_scrren)

        DelhiDarbar.instance?.hideStatusBar(this)

        progress_bar = findViewById(R.id.progressBar)
        mHandler = IncomingMessageHandler()
        val rel = RealmController.getInstance()
        val myRealms = rel.clearAll()

        imageView = findViewById(R.id.imSplash)
        val animation = AnimationUtils.loadAnimation(this, R.anim.my_splash)
        imageView!!.startAnimation(animation)

        try {
            getCurrentVersion()
        } catch (e: Exception) {
            e.printStackTrace()
            nextScreen()
        }
    }

    private fun nextScreen() {
        val manager = this@SplashScrren.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@SplashScrren)) {
            requestPermission79()
        }

        if (!hasGPSDevice(this@SplashScrren)) {
            Toast.makeText(this@SplashScrren, "Gps not Supported", Toast.LENGTH_SHORT).show()
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@SplashScrren)) {
            Log.e("65", "Gps already enabled")
            enableLoc()
        } else {
            Log.e("69", "Gps already enabled")
            requestPermission79()
        }

    }

    private fun getCurrentVersion() {
        val pm = this.packageManager
        var pInfo: PackageInfo? = null

        try {
            pInfo = pm.getPackageInfo(this.packageName, 0)

        } catch (e1: PackageManager.NameNotFoundException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }

        currentVersion = pInfo!!.versionName

        GetLatestVersion().execute()

    }

    private inner class GetLatestVersion : AsyncTask<String, String, JSONObject>() {
        private val progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): JSONObject {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                val doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.app.delhidarbar").get()
                latestVersion = doc.getElementsByClass("htlgb").get(6).text()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return JSONObject()
        }

        override fun onPostExecute(jsonObject: JSONObject) {
            if (latestVersion != null) {
                if (!currentVersion.equals(latestVersion, ignoreCase = true)) {
                    if (!isFinishing) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog()
                    }
                } else
                    nextScreen()
            } else
                nextScreen()

            super.onPostExecute(jsonObject)
        }
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("A New Update is Available")
        builder.setPositiveButton("Update") { dialog, which ->
            update = true
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.app.delhidarbar")))
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which -> nextScreen() }

        builder.setCancelable(false)
        dialog = builder.show()
    }

    fun requestPermission79() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Marshmallow+
            requestPermissions()
        } else {
            //below Marshmallow
            val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
            val messengerIncoming = Messenger(mHandler)
            startServiceIntent.putExtra(ConstantKeys.MESSENGER_INTENT_KEY, messengerIncoming)
            startService(startServiceIntent)
        }
    }

    private fun hasGPSDevice(context: Context): Boolean {
        val mgr = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    private fun enableLoc() {
        val googleApiClient = GoogleApiClient
                .Builder(this@SplashScrren)
                .addApi(LocationServices.API)
                .build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(result: LocationSettingsResult) {
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        Log.e(TAG, "All location settings are satisfied.")
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                        try {
                            status.startResolutionForResult(this@SplashScrren, REQUEST_LOCATION)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            // Marshmallow+
                            requestPermissions()
                        } else {
                            //below Marshmallow
                            val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                            val messengerIncoming = Messenger(mHandler)
                            startServiceIntent.putExtra(ConstantKeys.MESSENGER_INTENT_KEY, messengerIncoming)
                            startService(startServiceIntent)
                        }
                    }

                    Activity.RESULT_CANCELED -> {
                        Log.e("GPS", "User denied to access location")
                        finish()
                    }
                }
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                Log.e(TAG, "User interaction was cancelled.")
//              finish()
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: 90-=>")
                if (!progress_bar.isShown) {
                    progress_bar.visibility = View.VISIBLE
                }
//              if (isLocationEnabled(this@SplashScrren)) {
                val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                val messengerIncoming = Messenger(mHandler)
                startServiceIntent.putExtra(ConstantKeys.MESSENGER_INTENT_KEY, messengerIncoming)
                startService(startServiceIntent)
//                } else if (!isLocationEnabled(this@SplashScrren)) {
//                    showAlertDialogButtonClicked()
//                }
            } else {
                // Permission denied.
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler = null
    }

    internal inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
//          Log.e(TAG, "handleMessage...$msg")
            super.handleMessage(msg)
            when (msg.what) {
                LocationUpdatesService.LOCATION_MESSAGE -> {
                    val obj = msg.obj as Location
                    /*Log.e(TAG, "handleMessage: -=>(lat)->" + (msg.obj as Location).latitude
                            + "\n(lng)->" + (msg.obj as Location).longitude)*/
                    if (progress_bar.isShown) {
                        progress_bar.visibility = View.GONE
                    }
                    if (counter == 0) {
                        counter += 1
                        DelhiDarbar.instance?.mySharedPrefrence?.putString(ConstantKeys.LATITUDE, obj.latitude.toString())
                        DelhiDarbar.instance?.mySharedPrefrence?.putString(ConstantKeys.LONGITUDE, obj.longitude.toString())
                        Handler().postDelayed({
                            if (DelhiDarbar.instance?.mySharedPrefrence?.getString("LoginUserId")!!.length > 0) {
                            val intent = Intent(this@SplashScrren, Dashboard::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            val intent = Intent(this@SplashScrren, SelectLanguage::class.java)
                            startActivity(intent)
                            finish()
                        }
                        }, (secondsDelayed * 1000).toLong())
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.e(TAG, "Displaying permission rationale to provide additional context.")
            // Request permission
            ActivityCompat.requestPermissions(this@SplashScrren,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        } else {
            Log.e(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@SplashScrren,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isFromSetting == true) {
            mHandler = null
            finish()
            startActivity(intent)
            isFromSetting = false
        }
    }

    companion object {
        val TAG = SplashScrren::class.java.simpleName
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    }
}

