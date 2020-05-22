package com.app.delhidarbar.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.helper.getContext
import com.app.delhidarbar.model.my_static_view_cart1.PrepareCartResponse
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.RealmController
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.dropin.utils.PaymentMethodType
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PaymentMethodNonce
import kotlinx.android.synthetic.main.activity_view_cart_.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentOptionActivity : AppCompatActivity(), View.OnClickListener {
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
    lateinit var relo: RealmController
    internal lateinit var txt_amount: TextView
    internal lateinit var lay_cod: LinearLayout
    internal lateinit var lay_card: LinearLayout
    internal lateinit var lay_paypal: LinearLayout
    private var mNonce: PaymentMethodNonce? = null
    internal lateinit var lay_creditCard: LinearLayout
    private var mPaymentMethodType: PaymentMethodType? = null
    var CLIENT_TOKEN = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIxNGRiNThjZjlmZjU1ZGI3OTMxOTg0Nzk4Mzk5OTE3Yzk1MmIwMDQwOGNhOWYzNGE3MzA1ZWI3ODk3MWZlZTcyfGNyZWF0ZWRfYXQ9MjAxOC0xMi0wNVQwNzoxMzoyMC4wMjE1NjAyOTUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTdnMzJneXljOGJwOXp3anZcdTAwMjZwdWJsaWNfa2V5PWhyeHNmNGg5eXQ4NG55N3EiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvN2czMmd5eWM4YnA5endqdi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJncmFwaFFMIjp7InVybCI6Imh0dHBzOi8vcGF5bWVudHMuc2FuZGJveC5icmFpbnRyZWUtYXBpLmNvbS9ncmFwaHFsIiwiZGF0ZSI6IjIwMTgtMDUtMDgifSwiY2hhbGxlbmdlcyI6W10sImVudmlyb25tZW50Ijoic2FuZGJveCIsImNsaWVudEFwaVVybCI6Imh0dHBzOi8vYXBpLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb206NDQzL21lcmNoYW50cy83ZzMyZ3l5YzhicDl6d2p2L2NsaWVudF9hcGkiLCJhc3NldHNVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImF1dGhVcmwiOiJodHRwczovL2F1dGgudmVubW8uc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tLzdnMzJneXljOGJwOXp3anYifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoidGhpbmszNjAiLCJjbGllbnRJZCI6bnVsbCwicHJpdmFjeVVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS9wcCIsInVzZXJBZ3JlZW1lbnRVcmwiOiJodHRwOi8vZXhhbXBsZS5jb20vdG9zIiwiYmFzZVVybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9jaGVja291dC5wYXlwYWwuY29tIiwiZGlyZWN0QmFzZVVybCI6bnVsbCwiYWxsb3dIdHRwIjp0cnVlLCJlbnZpcm9ubWVudE5vTmV0d29yayI6dHJ1ZSwiZW52aXJvbm1lbnQiOiJvZmZsaW5lIiwidW52ZXR0ZWRNZXJjaGFudCI6ZmFsc2UsImJyYWludHJlZUNsaWVudElkIjoibWFzdGVyY2xpZW50MyIsImJpbGxpbmdBZ3JlZW1lbnRzRW5hYmxlZCI6dHJ1ZSwibWVyY2hhbnRBY2NvdW50SWQiOiJ0aGluazM2MCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJtZXJjaGFudElkIjoiN2czMmd5eWM4YnA5endqdiIsInZlbm1vIjoib2ZmIn0="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        relo = RealmController.getInstance()

        DelhiDarbar.instance?.hideStatusBar(this)
        initView()
    }

    private fun initView() {
        lay_creditCard = findViewById(R.id.lay_credit_card)
        lay_cod = findViewById(R.id.lay_cash_onDelivery)
        lay_card = findViewById(R.id.lay_card_onDelivery)
        lay_paypal = findViewById(R.id.lay_paypal)

        txt_amount = findViewById(R.id.txt_amount)
        amt = intent.getStringExtra("amount")
        if(!amt.isEmpty())
            amt = String.format("%.2f", amt.toDouble()).toString()
        txt_amount.text = resources.getString(R.string.pay) + " " + amt + " " + resources.getString(R.string.Price_symbol)

        order_id = intent.getStringExtra("order_id")
        if (order_id.equals("")) {
            product_id = intent.getStringExtra("product_id")
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = intent.getStringExtra("quantity")
            delivery_address = intent.getStringExtra("delivery_address")
            instructins = intent.getStringExtra("instructions")

        } else {
            re_quantity = intent.getStringExtra("re_quantity")
            re_itemid = intent.getStringExtra("re_itemid")

            product_id = re_itemid
            coupon_id = intent.getStringExtra("coupon_id")
            quantity = re_quantity
            delivery_address = intent.getStringExtra("delivery_address")
            instructins = intent.getStringExtra("instructions")

        }

        lay_card.setOnClickListener(this)
        lay_cod.setOnClickListener(this)
        lay_paypal.setOnClickListener(this)
        lay_creditCard.setOnClickListener(this)
    }

    fun callApi() {
        /* if(order_id.equals(""))
             placeAnOrder(order_id, product_id, coupon_id, quantity, delivery_address, instructins, pay_status, amt)
         else
             placeAnOrder(order_id, "", "", "", "", "", pay_status, amt)
 */
        placeAnOrder(order_id, product_id, coupon_id, quantity, delivery_address, instructins, pay_status, amt)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.lay_cash_onDelivery -> {
                pay_status = "1"
                callApi()
            }

            R.id.lay_credit_card -> {
                pay_status = "0"
                callApi()
            }

            R.id.lay_card_onDelivery -> {
                pay_status = "2"
                callApi()
            }

            R.id.lay_paypal -> {
                pay_status = "0"
                callApi()
            }
        }
    }

    private fun placeAnOrder(order_id: String, product_id: String, coupon_id: String, quantity: String, delivery_address: String, instructins: String, pay_status: String, order_amount: String) {
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(this@PaymentOptionActivity)
        progress_bar.visibility = View.VISIBLE
        RetrofitUtils
                .getRetrofitUtils()!!
                .create(Api::class.java)
                .placeAnOrder(
                        DelhiDarbar.instance?.mySharedPrefrence!!.getString(ConstantKeys.LoginUserIdKey)!!,
                        order_id,
                        product_id,
                        coupon_id,
                        quantity,
                        delivery_address,
                        instructins,
                        pay_status,
                        "android"
                )

                .enqueue(object : Callback<PrepareCartResponse> {
                    override fun onFailure(call: Call<PrepareCartResponse>, t: Throwable) {
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@PaymentOptionActivity)
                        progress_bar.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<PrepareCartResponse>, response: Response<PrepareCartResponse>) {
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@PaymentOptionActivity)
                        progress_bar.visibility = View.GONE
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error != false) {
                            Dialogs.showInformationDialog(getContext(), response.body()!!.message)
                        } else {

                            CLIENT_TOKEN = response.body()!!.client_token.toString()

                            if (pay_status.equals("0")) {
                                onBraintreeSubmit(CLIENT_TOKEN, order_amount)
                            } else {
                                orderPlaced()
                                relo.clearAll()
                                CartActivity.isMyPaymentSucccessORnot = true

                            }

                        }
                    }
                })

    }

    private fun orderPlaced() {
        val dialog = Dialog(this@PaymentOptionActivity, R.style.full_screen_dialog)
        dialog.setContentView(R.layout.order_placed)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        dialog.setCancelable(false)

        var lay_exit: RelativeLayout = dialog.findViewById(R.id.lay_congratulations)
        lay_exit.setOnClickListener(View.OnClickListener {
            val mainIntent = Intent(this, Dashboard::class.java)
            startActivity(mainIntent)
            //dialog.dismiss()
            finish()
        })

        DelhiDarbar.instance?.hideStatusBar(this)

        if (!dialog.isShowing) {
            dialog.show()
            DelhiDarbar.instance?.hideStatusBar(this)
        }

/*
        Handler().postDelayed({
            */
/* Create an Intent that will start the Menu-Activity. *//*

            val mainIntent = Intent(this, Dashboard::class.java)
            startActivity(mainIntent)
            dialog.dismiss()
            finish()
        }, 2000)
*/

        dialog.setOnKeyListener { dialogInterface, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_BACK) {
                startActivity(Intent(PaymentOptionActivity@ this, Dashboard::class.java))
            }
            false
        }

    }

    private fun displayResult(paymentMethodNonce: PaymentMethodNonce?, deviceData: String) {
        mNonce = paymentMethodNonce
        mPaymentMethodType = PaymentMethodType.forType(mNonce)
        //        String details = "";
        if (mNonce is CardNonce) {
            val cardNonce = mNonce as CardNonce
            Log.e("TAG", "displayResult: 93-=>" + cardNonce.nonce)
            // clearMycartHere(cardNonce.nonce)
            relo.clearAll()
        } else if (mNonce is PayPalAccountNonce) {
            val paypalAccountNonce = mNonce as PayPalAccountNonce
            Log.e("TAG", "displayResult: 106-=>" + paypalAccountNonce.nonce)
            // clearMycartHere(paypalAccountNonce.nonce)
            relo.clearAll()
        }
    }

    fun onBraintreeSubmit(CLIENT_TOKEN: String, order_amount: String) {
        val dropInRequest = DropInRequest().clientToken(CLIENT_TOKEN)
        dropInRequest.amount(order_amount)
        startActivityForResult(dropInRequest.getIntent(this), 1501)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1501) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)

                orderPlaced()
                displayResult(result.paymentMethodNonce, "")
                CartActivity.isMyPaymentSucccessORnot = true

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                CartActivity.isMyPaymentSucccessORnot = false
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                // handle errors here, an exception may be available in
                val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                CartActivity.isMyPaymentSucccessORnot = false
                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
