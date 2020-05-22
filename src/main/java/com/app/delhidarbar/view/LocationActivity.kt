package com.app.delhidarbar.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.getContext
import com.app.delhidarbar.model.recent_location.RecentLocation
import com.app.delhidarbar.model.update_user_location.UpdateLocationResponseModel
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickWithText
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.view.adapter.recent_location.AdapterRecentLocation
import kotlinx.android.synthetic.main.activity_view_cart_.*
import kotlinx.android.synthetic.main.fragment_location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationActivity : AppCompatActivity() {
    lateinit var pb_location: ProgressBar
    lateinit var rv_location:RecyclerView
    lateinit var btn_add:Button
    lateinit var btn_next:Button
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
        val TAG = LocationActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        pb_location = findViewById(R.id.pb_location)
        rv_location= findViewById(R.id.rv_location)
        btn_add= findViewById(R.id.btn_add_new)
        btn_next= findViewById(R.id.btn_next)

        order_id = intent.getStringExtra("order_id")
        if (order_id.equals("")) {
            product_id = intent.getStringExtra("product_id")
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = intent.getStringExtra("quantity")
            amt = intent.getStringExtra("amount")
            instructins = intent.getStringExtra("instructions")

        } else {
            re_quantity = intent.getStringExtra("re_quantity")
            re_itemid = intent.getStringExtra("re_itemid")

            product_id = re_itemid
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = re_quantity
            amt = intent.getStringExtra("amount")
            instructins = intent.getStringExtra("instructions")

        }

        onClick()

    }

    private fun onClick() {
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this)) {
            apiRecentLocation()
        } else {
            DelhiDarbar.instance!!.commonMethods.showDialogOK(this, getString(R.string.Please_try_again_later_)
                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
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

        btn_add.setOnClickListener {
            var intent: Intent = Intent(this, AddLocation::class.java)
            intent.putExtra("amount", amt)

            // CHANGE LOG: Do not send any Order ID
            intent.putExtra("order_id", order_id)
            intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
            intent.putExtra("coupon_id", coupon_id)
            intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
            intent.putExtra("delivery_address", DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation).toString())
            intent.putExtra("instructions", instructins)
            startActivity(intent)
        }

        btn_next.setOnClickListener {

            var intent: Intent = Intent(this, PaymentOptionActivity::class.java)
            intent.putExtra("amount", amt)

            // CHANGE LOG: Do not send any Order ID
            intent.putExtra("order_id", order_id)
            intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
            intent.putExtra("coupon_id", coupon_id)
            intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
            intent.putExtra("delivery_address", DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation).toString())
            intent.putExtra("instructions", instructins)
            startActivity(intent)
        }

    }

    private fun apiRecentLocation() {
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.locationApi(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : Callback<RecentLocation> {

            override fun onResponse(call: Call<RecentLocation>, response: Response<RecentLocation>) {
                Log.e(TAG, "78>>${response.body()}")
                if (!response.isSuccessful)
                    return
                val recentLocationAdpter = AdapterRecentLocation(getContext(), response.body()!!.locations)
                rv_location.adapter = recentLocationAdpter
                recentLocationAdpter.performItemClick(object : ClickWithText {
                    override fun itemClick(postion: Int, name: String) {

                        DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.userLocation, response.body()!!.locations!![postion]!!.locationAddress!!)
                        delivery_address = response.body()!!.locations!![postion]!!.locationAddress!!
                    }
                })
            }

            override fun onFailure(call: Call<RecentLocation>, t: Throwable) {
                Log.e(TAG, "78>>$t")
            }

        })
    }

}
