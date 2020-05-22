package com.app.delhidarbar.helper

import android.app.Activity
import android.app.Application
import android.content.DialogInterface
import android.os.Build
import android.view.WindowManager
import com.app.delhidarbar.utils.CommonMethods
import com.app.delhidarbar.utils.SharedPreferenceUtils
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File

class DelhiDarbar : Application() {
    lateinit var commonMethods: CommonMethods
    lateinit var mySharedPrefrence: SharedPreferenceUtils

    override fun onCreate() {
        super.onCreate()
        instance = this
        commonMethods = CommonMethods(this)
        initialize()
        if (!commonMethods.checkInternetConnect(applicationContext)) {
            return

        } else {
            DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun invoke(p1: DialogInterface, p2: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        // built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        Realm.init(this)
        val realm: Realm by lazy {
            val config = RealmConfiguration.Builder()
                    .name("bett.realm")
                    .schemaVersion(2)
                    .build()
            Realm.getInstance(config)
//          Realm.getDefaultInstance()
        }
    }

    private fun initialize() {
        mySharedPrefrence = SharedPreferenceUtils(applicationContext)
    }

    fun clearApplicationData() {
        val cache = cacheDir
        val appDir = File(cache.parent)
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                }
            }
        }
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            var i = 0
            while (i < children.size) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }

                i++
            }
        }

        assert(dir != null)
        return dir!!.delete()
    }

    fun hideStatusBar(context: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = context.getWindow()
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    companion object {
        @get:Synchronized
        var instance: DelhiDarbar? = null
            private set
        val TAG = DelhiDarbar::class.java.getSimpleName()
    }

}