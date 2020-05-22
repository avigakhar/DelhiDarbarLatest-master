package com.app.delhidarbar.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.utils.ConstantKeys

class SelectLanguage : AppCompatActivity(), View.OnClickListener {
    var lang = "language"
    private var btnEnglish: Button? = null
    private var btnGermen: Button? = null
    private var btnSpanish: Button? = null
    private var progress_bar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

       // DelhiDarbar.instance?.hideKeyboard(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        findControlls()
    }

    /***
     * find ids
     */
    private fun findControlls() {
        btnEnglish = findViewById(R.id.btnEnglish)
        btnGermen = findViewById(R.id.btnGermen)
        btnSpanish = findViewById(R.id.btnSpanish)
        progress_bar = findViewById(R.id.progress_bar)
        btnEnglish!!.setOnClickListener(this)
        btnGermen!!.setOnClickListener(this)
        btnSpanish!!.setOnClickListener(this)

    }

    /**
     *
     *  Onclick
     *
     * **/
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnEnglish -> {
                progress_bar!!.visibility = View.VISIBLE
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("en", this@SelectLanguage)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"en")
                startActivity(Intent(this@SelectLanguage, BaseActivity::class.java))
            }
            R.id.btnGermen -> {
                progress_bar!!.visibility = View.VISIBLE
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("de", this@SelectLanguage)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"de")
                startActivity(Intent(this@SelectLanguage, BaseActivity::class.java))
            }
            R.id.btnSpanish -> {
                progress_bar!!.visibility = View.VISIBLE
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("es", this@SelectLanguage)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"es")
                startActivity(Intent(this@SelectLanguage, BaseActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progress_bar!!.visibility = View.GONE
        if (RegisterActivity.isFromBackPressed) {
            RegisterActivity.isFromBackPressed = true
            startActivity(Intent(this@SelectLanguage, BaseActivity::class.java))
            finish()
        }
    }

}
