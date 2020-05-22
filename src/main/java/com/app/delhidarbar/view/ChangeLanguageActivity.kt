package com.app.delhidarbar.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.profile_model.ProfileResponeModel
import com.app.delhidarbar.model.update_user.UpdateUser
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ConstantKeys
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*

class ChangeLanguageActivity : AppCompatActivity(), View.OnClickListener {
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
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("en", this@ChangeLanguageActivity)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"en")

                editProfileApi("en")
            }
            R.id.btnGermen -> {
                progress_bar!!.visibility = View.VISIBLE
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("de", this@ChangeLanguageActivity)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"de")

                editProfileApi("de")
            }
            R.id.btnSpanish -> {
                progress_bar!!.visibility = View.VISIBLE
                DelhiDarbar!!.instance!!.commonMethods!!.setLocale("es", this@ChangeLanguageActivity)
                DelhiDarbar!!.instance!!.mySharedPrefrence.putString(ConstantKeys.language,"es")

                editProfileApi("es")
            }
        }
    }

    private fun editProfileApi(language:String) {
        progress_bar!!.visibility = View.VISIBLE
        DelhiDarbar!!.instance!!.commonMethods!!.disableScreenInteraction(this@ChangeLanguageActivity)
        val map = java.util.HashMap<String, RequestBody>()

        map.put(ConstantKeys.language, RequestBody.create(MediaType.parse("text/plain"), language))
        map.put(ConstantKeys.name, RequestBody.create(MediaType.parse("text/plain"), DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.userName)))
        map.put(ConstantKeys.email, RequestBody.create(MediaType.parse("text/plain"), DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.email)))
       // map.put(ConstantKeys.address, RequestBody.create(MediaType.parse("text/plain"), ""))
        map.put(ConstantKeys.phone, RequestBody.create(MediaType.parse("text/plain"), DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.userPhone)))

        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.updateUser(
                map, DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : retrofit2.Callback<UpdateUser> {
            override fun onFailure(call: Call<UpdateUser>, t: Throwable) {
                DelhiDarbar!!.instance!!.commonMethods!!.enableScreenInteraction(this@ChangeLanguageActivity)
                progress_bar!!.visibility = View.GONE
            }

            override fun onResponse(call: Call<UpdateUser>, response: Response<UpdateUser>) {
                DelhiDarbar!!.instance!!.commonMethods!!.enableScreenInteraction(this@ChangeLanguageActivity)
                progress_bar!!.visibility = View.GONE
                Log.e("Edit profile activity ", "onSuccess" + response.body())
                if (!response.isSuccessful)
                    return
                if (response.body()!!.error == false) {

                    EditProfileActivity.isFromUpdateDatabase = true
                    EditProfileActivity.updateprofile = true
                    finish()

                } else {
                    DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ChangeLanguageActivity,
                            response.body()!!.message, DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                        override fun invoke(p1: DialogInterface, p2: Int) {
                            when (p2) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    p1.dismiss()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    p1.dismiss()
                                }
                            }
                        }

                        override fun onClick(p0: DialogInterface?, p1: Int) {
                        }
                    }), resources.getString(R.string.ok))
                }
            }
        })
    }

}
