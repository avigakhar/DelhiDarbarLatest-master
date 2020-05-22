package com.app.delhidarbar.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.update_user_location.UpdateLocationResponseModel
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ConstantKeys
import kotlinx.android.synthetic.main.activity_add_location.et_email
import kotlinx.android.synthetic.main.activity_add_location.et_name
import kotlinx.android.synthetic.main.fragment_location.btn_next
import kotlinx.android.synthetic.main.fragment_location.et_building
import kotlinx.android.synthetic.main.fragment_location.et_city
import kotlinx.android.synthetic.main.fragment_location.et_contact
import kotlinx.android.synthetic.main.fragment_location.et_note
import kotlinx.android.synthetic.main.fragment_location.et_postalCode
import kotlinx.android.synthetic.main.fragment_location.et_street
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocation : AppCompatActivity() {
    lateinit var pb_location: ProgressBar
    var amt: String = ""
    var order_id: String = ""
    var product_id: String = ""
    var coupon_id: String = ""
    var quantity: String = ""
    var re_quantity: String = ""
    var re_itemid: String = ""
    var instructins: String = ""
    var pay_status: String = ""
    var delivery_address: String = ""

    companion object {
        val TAG = AddLocation::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        pb_location = findViewById(R.id.pb_location)

        order_id = intent.getStringExtra("order_id")
        if (order_id.equals("")) {
            product_id = intent.getStringExtra("product_id")
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = intent.getStringExtra("quantity")
            delivery_address = intent.getStringExtra("delivery_address")
            amt = intent.getStringExtra("amount")

            instructins = intent.getStringExtra("instructions")

        } else {
            re_quantity = intent.getStringExtra("re_quantity")
            re_itemid = intent.getStringExtra("re_itemid")

            product_id = re_itemid
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = re_quantity
            amt = intent.getStringExtra("amount")

            delivery_address = intent.getStringExtra("delivery_address")
            instructins = intent.getStringExtra("instructions")

        }

        onClick()

    }

    private fun onClick() {
        et_name.setText(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userName))
        et_email.setText(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.email))
        et_contact.setText(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userPhone))

        btn_next.setOnClickListener {

            if (et_name!!.text!!.toString().trim().length == 0) {
                et_name!!.error = getString(R.string.Please_enter_the_name)
            }else  if (et_email!!.text!!.toString().trim().length == 0) {
                et_email!!.error = getString(R.string.Please_enter_the_email)
            } else  if (!Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()) {
                et_email!!.error = getString(R.string.Please_enter_the_email)
            } else if (et_street!!.text!!.toString().trim().length == 0) {
                et_street!!.error = getString(R.string.Please_enter_street)
            }else if(et_building!!.text.toString().trim().length == 0) {
                et_building!!.error = getString(R.string.Please_enter_building)
            }else if(et_city!!.text.toString().trim().length == 0) {
                et_city!!.error = getString(R.string.Please_enter_city)
            }else if(et_contact!!.text.toString().trim().length == 0) {
                et_contact!!.error = getString(R.string.Please_enter_phone)
            } else if(et_contact!!.text.toString().trim().length <9) {
                et_contact!!.error = getString(R.string.Please_enter_valid_phone)
            } else {
                var add = et_name!!.text!!.toString()+ ", "+et_street!!.text!!.toString() + ", "+ et_building!!.text!!.toString()+
                 ", "+ et_city!!.text!!.toString() + ", "+ et_postalCode!!.text!!.toString()+ ", "+ et_contact!!.text!!.toString();
                sendUserUserLocationToTheServer(address = add, contact = et_contact!!.text!!.toString(), note = et_note!!.text!!.toString(), name = et_name!!.text.toString(), email = et_email!!.text.toString())

                DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.userLocation, add+ ", "+ et_contact!!.text!!.toString());

            }
        }

    }

    fun sendUserUserLocationToTheServer(address: String, contact:String, note:String, name:String, email:String) {
        pb_location.visibility = View.VISIBLE
        DelhiDarbar.instance!!.commonMethods.disableScreenInteraction(this!!)
        RetrofitUtils.getRetrofitUtils()
                ?.create(Api::class.java)
                ?.updateUserLocation(
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, address, contact, note, name, email)
                ?.enqueue(object : Callback<UpdateLocationResponseModel> {
                    override fun onFailure(call: Call<UpdateLocationResponseModel>, t: Throwable) {
                        pb_location.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<UpdateLocationResponseModel>, response: Response<UpdateLocationResponseModel>) {

                        pb_location.visibility = View.GONE
                        if (!response.isSuccessful)
                            return

                       nextscreen(address)
                    }

                })

    }

    fun nextscreen(address:String){
        var intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("amount", amt)

        // CHANGE LOG: Do not send any Order ID
        intent.putExtra("order_id", order_id)
        intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
        intent.putExtra("coupon_id", coupon_id)
        intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
        intent.putExtra("delivery_address", address)
        intent.putExtra("instructions", instructins)
        startActivity(intent)
    }
}
