package com.app.delhidarbar.view

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.*
import com.app.delhidarbar.model.my_static_view_cart1.PrepareCartResponse
import com.app.delhidarbar.model.profile_model.OrderItem
import com.app.delhidarbar.model.profile_model.ProfileResponeModel
import com.app.delhidarbar.model.restaurent.Restaurant
import com.app.delhidarbar.model.restaurent.SelectRestaurentModel
import com.app.delhidarbar.model.select_restaurent_model.SelectRestaurantResponse
import com.app.delhidarbar.model.view_all_coupons.ViewAllCouponResponse
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.*
import com.app.delhidarbar.view.adapter.AdapterAllCoupons
import com.app.delhidarbar.view.adapter.AdapterSelectRest
import com.app.delhidarbar.view.adapter.CartAdapter
import com.app.delhidarbar.view.adapter.ReOrderCartAdapter
import com.app.delhidarbar.view.fragments.LocationFragment
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.dropin.utils.PaymentMethodType
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PaymentMethodNonce
import com.dm.emotionrating.library.EmotionView
import com.dm.emotionrating.library.GradientBackgroundView
import com.dm.emotionrating.library.RatingView
import kotlinx.android.synthetic.main.activity_view_cart_.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class CartActivity : AppCompatActivity() {
    var amount = ""
    var order_id = ""
    var product_id: String = ""
    var coupon_id: String = ""
    var quantity: String = ""
    var re_itemid: String = ""
    var re_quantity: String = ""
    var active: Boolean = true
    var order_amount: Double = 0.0
    var total_order_amount: Double = 0.0
    lateinit var delCoupon: TextView
    lateinit var tv_restarunatAddress: TextView
    lateinit var nextHaveCoupopn: ImageView
    lateinit var btn_placeOrder: Button
    lateinit var btn_next: Button
    lateinit var tv_couponApplied: TextView
    lateinit var ll_couponApplied: LinearLayout
    private var mNonce: PaymentMethodNonce? = null
    lateinit var cartAdapter: CartAdapter
    lateinit var adapterAllCoupon: AdapterAllCoupons
    var updated_qty: ArrayList<String> = ArrayList()
    var order_item_id: ArrayList<String> = ArrayList()
    private var mPaymentMethodType: PaymentMethodType? = null
    var viewAllCouponResponse: ViewAllCouponResponse? = null
    lateinit var restaurants: List<Restaurant>
    lateinit var progress_bar: ProgressBar
    var CLIENT_TOKEN = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIxNGRiNThjZjlmZjU1ZGI3OTMxOTg0Nzk4Mzk5OTE3Yzk1MmIwMDQwOGNhOWYzNGE3MzA1ZWI3ODk3MWZlZTcyfGNyZWF0ZWRfYXQ9MjAxOC0xMi0wNVQwNzoxMzoyMC4wMjE1NjAyOTUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTdnMzJneXljOGJwOXp3anZcdTAwMjZwdWJsaWNfa2V5PWhyeHNmNGg5eXQ4NG55N3EiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvN2czMmd5eWM4YnA5endqdi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJncmFwaFFMIjp7InVybCI6Imh0dHBzOi8vcGF5bWVudHMuc2FuZGJveC5icmFpbnRyZWUtYXBpLmNvbS9ncmFwaHFsIiwiZGF0ZSI6IjIwMTgtMDUtMDgifSwiY2hhbGxlbmdlcyI6W10sImVudmlyb25tZW50Ijoic2FuZGJveCIsImNsaWVudEFwaVVybCI6Imh0dHBzOi8vYXBpLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb206NDQzL21lcmNoYW50cy83ZzMyZ3l5YzhicDl6d2p2L2NsaWVudF9hcGkiLCJhc3NldHNVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImF1dGhVcmwiOiJodHRwczovL2F1dGgudmVubW8uc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tLzdnMzJneXljOGJwOXp3anYifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoidGhpbmszNjAiLCJjbGllbnRJZCI6bnVsbCwicHJpdmFjeVVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS9wcCIsInVzZXJBZ3JlZW1lbnRVcmwiOiJodHRwOi8vZXhhbXBsZS5jb20vdG9zIiwiYmFzZVVybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9jaGVja291dC5wYXlwYWwuY29tIiwiZGlyZWN0QmFzZVVybCI6bnVsbCwiYWxsb3dIdHRwIjp0cnVlLCJlbnZpcm9ubWVudE5vTmV0d29yayI6dHJ1ZSwiZW52aXJvbm1lbnQiOiJvZmZsaW5lIiwidW52ZXR0ZWRNZXJjaGFudCI6ZmFsc2UsImJyYWludHJlZUNsaWVudElkIjoibWFzdGVyY2xpZW50MyIsImJpbGxpbmdBZ3JlZW1lbnRzRW5hYmxlZCI6dHJ1ZSwibWVyY2hhbnRBY2NvdW50SWQiOiJ0aGluazM2MCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJtZXJjaGFudElkIjoiN2czMmd5eWM4YnA5endqdiIsInZlbm1vIjoib2ZmIn0="
    var profileResponeModel: ProfileResponeModel? = null

    companion object {
        var isMyPaymentSucccessORnot = false
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cart_)

        active = true

        nextHaveCoupopn = findViewById(R.id.nextHaveCoupopn)
        delCoupon = findViewById(R.id.delCoupon)
        tv_restarunatAddress = findViewById(R.id.tv_restarunatAddress)
        progress_bar = findViewById(R.id.progress_bar)
        ll_couponApplied = findViewById(R.id.ll_couponApplied)

      //  allRestaurantsApi()
        //getProfileDataApi()

        if (intent.hasExtra("order_id")) {
            order_id = intent.getStringExtra("order_id")
            val orderItems = intent.getParcelableArrayListExtra<OrderItem>("items")

            reorderData(orderItems)

        } else {
            updateCartDataFromRealms()
        }

        otherFun()

        tv_restarunatAddress.text = DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.restaurantName) + "\n" + DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.restaurantAddress)
        tv_restarunatAddress.paintFlags = tv_restarunatAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tv_restarunatAddress.setOnClickListener(View.OnClickListener {
            if (DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation) != null) {
                DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                        this@CartActivity,
                        resources.getString(R.string.do_u_want_to_change_restaurant)
                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                    override fun invoke(p1: DialogInterface, p2: Int) {
                        when (p2) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                Log.e("111", "ASDASD")
                                p1.dismiss()

                                restaurantUpdate()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                Log.e("111", "BUTTON_NEGATIVE")
                            }
                        }
                    }

                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                }), resources.getString(R.string.ok), resources.getString(R.string.Cancel))
            }


        })

        tv_userAddressHere.text = DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation)
        tv_userAddressHere.paintFlags = tv_userAddressHere.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tv__userNameNphone.text = DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userName) + " , " + DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userPhone)
        tv__userNameNphone.paintFlags = tv__userNameNphone.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tv_userAddressHere.setOnClickListener {
            if (DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation) != null) {
                DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                        this@CartActivity,
                        resources.getString(R.string.do_u_want_to_change_address)
                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                    override fun invoke(p1: DialogInterface, p2: Int) {
                        when (p2) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                Log.e("111", "ASDASD")
                                p1.dismiss()
                             //   openFullScreenDialogForUpdateAddress()
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                                Log.e("111", "BUTTON_NEGATIVE")
                            }
                        }
                    }

                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }

                }), resources.getString(R.string.ok), resources.getString(R.string.Cancel))
            }
        }
    }

    fun reorderData(orderItems: ArrayList<OrderItem>) {
        if (orderItems.size > 0) {
            val reOrderCartAdapter = ReOrderCartAdapter(this@CartActivity, orderItems)
            rv_cartItems.adapter = reOrderCartAdapter
            reOrderCartAdapter.performItemClick(object : ClickVIewCartInterface {
                override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, qty: Double, from: String) {
                    try {
                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {

                            if(isEnable >= 0){
                                order_amount = 0.0
                                if (orderItems.size > 0) {
                                    for (i in 0 until orderItems.size) {
                                        val new_cost: Double = orderItems[i].item_price!!.toDouble() * orderItems[i].quantity
                                        order_amount += new_cost
                                    }

                                    total_order_amount = order_amount

                                    tv_price.text = Util.toFormattedPrice(total_order_amount)

                                    if(discount_amount > 0) {
                                        discount_amount = (str_coupon_percentage.toDouble() / 100) * order_amount
                                        tv_couponApplied.text = "-" + "${DecimalFormat("##.00").format(discount_amount)} ${resources.getString(R.string.Price_symbol)}"
                                        order_amount = order_amount - discount_amount
                                    }

                                    tv_GrandTotal.text = Util.toFormattedPrice(order_amount)

                                    if(total_order_amount <= 0){
                                        ll_couponApplied.visibility = View.GONE
                                        nextHaveCoupopn.visibility = View.VISIBLE
                                        delCoupon.visibility = View.GONE
                                        tv_haveAnCoupon.text = resources.getString(R.string.have_a_coupon)

                                        nextHaveCoupopn.visibility = View.VISIBLE
                                        tv_GrandTotal.text = String.format("0.0 " + resources.getString(R.string.Price_symbol))

                                    }
                                }

                                Log.e("updated_qty:", "" + updated_qty)
                                updated_qty.set(position, isEnable.toString())

                            }else{
                                updated_qty.removeAt(position)
                                order_item_id.removeAt(position)

                                order_amount = 0.0
                                if (orderItems.size > 0) {
                                    for (i in 0 until orderItems.size) {
                                        val new_cost: Double = orderItems[i].item_price!!.toDouble() * orderItems[i].quantity
                                        order_amount += new_cost
                                    }

                                    total_order_amount = order_amount

                                    tv_price.text = Util.toFormattedPrice(total_order_amount)

                                    if(discount_amount > 0) {
                                        discount_amount = (str_coupon_percentage.toDouble() / 100) * order_amount
                                        tv_couponApplied.text = "-" + "${DecimalFormat("##.00").format(discount_amount)} ${resources.getString(R.string.Price_symbol)}"

                                        order_amount = order_amount - discount_amount

                                    }

                                    tv_GrandTotal.text = Util.toFormattedPrice(order_amount)

                                    if(total_order_amount <= 0){
                                        ll_couponApplied.visibility = View.GONE
                                        nextHaveCoupopn.visibility = View.VISIBLE
                                        delCoupon.visibility = View.GONE
                                        tv_haveAnCoupon.text = resources.getString(R.string.have_a_coupon)

                                        tv_GrandTotal.text = String.format("0.0 " + resources.getString(R.string.Price_symbol))

                                    }
                                }

                                Log.e("updated_qty:", "" + updated_qty)

                            }

                        } else {
                            Dialogs.showInformationDialog(getContext(), R.string.Please_try_again_later_)
                        }

                    } catch (e: java.lang.Exception) {
                        e.message
                    }
                }
            })

            updated_qty = ArrayList()
            order_item_id = ArrayList()

            order_amount = 0.0
            if (orderItems.size > 0) {
                for (i in 0 until orderItems.size) {

                    if (orderItems[i].quantity!!.toInt() > 1) {
                        var new_cost: Double = orderItems[i].item_price!!.toDouble() * orderItems[i].quantity!!.toInt()
                        order_amount += new_cost

                    } else {
                        order_amount += orderItems[i].item_price!!.toDouble()
                    }

                    if(orderItems[i].quantity > 0){
                        updated_qty.add(orderItems[i].quantity.toString())

                        if (orderItems[i].in_item_id.toString().equals("0"))
                            order_item_id.add(orderItems[i].item_id.toString())
                        else if (orderItems[i].in_item_id.toString().equals("null"))
                            order_item_id.add(orderItems[i].item_id.toString())
                        else
                            order_item_id.add(orderItems[i].item_id.toString() + "<" + orderItems[i].in_item_id + ">")

                    }

                     Log.e ("order_item_id:", "" + order_item_id)
                }

                total_order_amount = order_amount

                tv_price.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)
                tv_GrandTotal.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)
            }

        } else {
            DelhiDarbar.instance!!.commonMethods.showDialogOK(
                    this@CartActivity,
                    resources.getString(R.string.opps_no_more_item_found)
                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                override fun invoke(p1: DialogInterface, p2: Int) {
                    when (p2) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            isMyPaymentSucccessORnot = true
                            p1.dismiss()
                            finish()
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

    fun getReorderData() {
        // TODO: auto CSV
        if (order_item_id != null && updated_qty!=null) {
            if (order_item_id.size.toInt() > 0 && updated_qty.size.toInt() > 0) {
                if(order_item_id.size.toInt() == updated_qty.size.toInt()){
                    for (i in 0 until order_item_id.size) {
                        if (re_itemid.equals("") && re_quantity.equals("")) {
                            if(!updated_qty[i].equals("0")){
                                re_itemid += order_item_id[i]
                                re_quantity += updated_qty[i].toString()
                            }
                        }else {
                            if(!updated_qty[i].equals("0")){
                                re_itemid = re_itemid + "," + order_item_id[i]
                                re_quantity = re_quantity + "," + updated_qty[i]
                            }
                        }

                    }

                }
            }
        }

    }

    fun updateCartDataFromRealms() {
        Log.d("ActivityViewCart", "updateCartDataFromRealms()")
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this)) {

            val realmProducts: MutableList<RealmProduct> = RealmController.getInstance().all
            product_id = ""
            quantity = ""
            for (myRealm in realmProducts) {

                if (product_id.equals("")) {
                    if (myRealm.addon_id.toString().equals("")) {
                        if(!myRealm.quantity.toString().equals("0"))
                            product_id += myRealm.id.toString()
                    }else {
                        if(!myRealm.quantity.toString().equals("0"))
                            product_id += myRealm.id.toString() + "<" + myRealm.addon_id + ">"
                    }

                } else {
                    if (myRealm.addon_id.toString().equals("")) {
                        if(!myRealm.quantity.toString().equals("0"))
                            product_id = product_id + "," + myRealm.id.toString()
                    }else {
                        if(!myRealm.quantity.toString().equals("0"))
                            product_id = product_id + "," + myRealm.id.toString() + "<" + myRealm.addon_id + ">"
                    }

                }

                if (quantity.equals("")) {
                    if(!myRealm.quantity.toString().equals("0"))
                        quantity += myRealm.quantity.toString()
                }else {
                    if(!myRealm.quantity.toString().equals("0"))
                        quantity = quantity + "," + myRealm.quantity.toString()
                }
            }

            setAdapter(realmProducts)

        } else {
            Dialogs.showNetworkErrorDialog(getContext())
        }
    }

    private fun setAdapter(mutableList: MutableList<RealmProduct>) {
        if (mutableList != null && mutableList.size > 0) {
            cartAdapter = CartAdapter(this@CartActivity, mutableList)
            rv_cartItems.adapter = cartAdapter

            txt_items.setText(resources.getString(R.string.items) + " (" +mutableList.size+ ")")

            cartAdapter.performItemClick(object : ClickVIewCartInterface {
                override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                    try {
                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {

                            updateCartDataFromRealms()

                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(this@CartActivity, getString(R.string.Please_try_again_later_)
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

                    } catch (e: java.lang.Exception) {
                        e.message
                    }
                }
            })

            order_amount = 0.0
            if (mutableList.size > 0) {
                for (i in 0 until mutableList.size) {

                    if (mutableList[i].quantity >= 0) {
                        var new_cost: Double = mutableList[i].price * mutableList[i].quantity
                        order_amount += new_cost

                    } else {
                        order_amount += mutableList[i].price
                    }

                }

                total_order_amount = order_amount

                tv_price.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)

                if(discount_amount > 0) {
                    discount_amount = (str_coupon_percentage.toDouble() / 100) * order_amount
                    tv_couponApplied.text = "-" + "${DecimalFormat("##.00").format(discount_amount)} ${resources.getString(R.string.Price_symbol)}"

                    order_amount = order_amount - discount_amount

                }

                tv_GrandTotal.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)


                if(total_order_amount <= 0){
                    ll_couponApplied.visibility = View.GONE
                    nextHaveCoupopn.visibility = View.VISIBLE
                    delCoupon.visibility = View.GONE
                    tv_haveAnCoupon.text = resources.getString(R.string.have_a_coupon)

                    tv_GrandTotal.text = String.format("0.0 " + resources.getString(R.string.Price_symbol))

                }

            }
        } else {
            DelhiDarbar.instance!!.commonMethods.showDialogOK(
                    this@CartActivity,
                    resources.getString(R.string.opps_no_more_item_found)
                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                override fun invoke(p1: DialogInterface, p2: Int) {
                    when (p2) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            isMyPaymentSucccessORnot = true
                            p1.dismiss()
                            finish()
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

//        tv_price.text = "${"0"} ${resources.getString(R.string.Price_symbol)}"
//        tv_GrandTotal.text = "${"0"} ${resources.getString(R.string.Price_symbol)}"

    }

    fun otherFun() {
        tv_haveAnCoupon.setOnClickListener {
            nextHaveCoupopn.performClick()
        }

        nextHaveCoupopn.setOnClickListener {
            if(total_order_amount > 0){
                openFullScreenDialogForAllCoupon()
            }else
                Toast.makeText(this, resources.getString(R.string.order_greater), Toast.LENGTH_SHORT).show()
        }

        delCoupon.setOnClickListener {
            order_amount = order_amount + discount_amount

            total_order_amount = order_amount.toDouble()

            tv_GrandTotal.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)

            ll_couponApplied.visibility = View.GONE
            nextHaveCoupopn.visibility = View.VISIBLE
            delCoupon.visibility = View.GONE
            tv_haveAnCoupon.text = resources.getString(R.string.have_a_coupon)
        }

        viewAllCoupons()
        tv_creatMyPayment.setOnClickListener {
            getReorderData()

            if(order_amount.toDouble() > 14) {
                var intent: Intent = Intent(this, LocationActivity::class.java)
                intent.putExtra("amount", order_amount.toString())

                // CHANGE LOG: Do not send any Order ID
                intent.putExtra("order_id", "")
                intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
                intent.putExtra("coupon_id", coupon_id)
                intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
                intent.putExtra("instructions", edt_specialInstructions.text.toString())
                startActivity(intent)

            }else{
                minimumQty(order_amount.toString())
            }
        }

        var my_haveAnCoupon = ""
        if (!tv_haveAnCoupon.text.toString().trim().equals(resources.getString(R.string.have_a_coupon))) {
            my_haveAnCoupon = tv_haveAnCoupon.text.toString().trim()
        }

        edt_specialInstructions.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edt_specialInstructions.lineCount >= 3) {
                    edt_specialInstructions.getText().delete(edt_specialInstructions.getSelectionEnd() - 1, edt_specialInstructions.getSelectionStart());
                }
            }
        })

        //user_id, order_id, product_id, coupon_id, quantity, delivery_address, instructions

        btn_placeOrder = findViewById(R.id.btn_placeOrder)
        btn_next = findViewById(R.id.btn_next)
        btn_next.setOnClickListener{
            if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {
                if(order_amount.toDouble() > 14){
                    getReorderData()

                      var intent: Intent = Intent(this, LocationActivity::class.java)
                      intent.putExtra("amount", order_amount.toString())

                      // CHANGE LOG: Do not send any Order ID
                      intent.putExtra("order_id", "")
                      intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
                      intent.putExtra("coupon_id", coupon_id)
                      intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
                      intent.putExtra("instructions", edt_specialInstructions.text.toString())
                      startActivity(intent)
                }else{
                    minimumQty(order_amount.toString())
                }

            } else {
                Dialogs.showNetworkErrorDialog(getContext())
            }
        }
        btn_placeOrder.setOnClickListener {
            if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {
                if(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userPhone).equals("")){
                    addPhone()

                } else if(order_amount.toDouble() > 14){
                    getReorderData()

                  /*  var intent: Intent = Intent(this, PaymentOptionActivity::class.java)
                    intent.putExtra("amount", order_amount.toString())

                    // CHANGE LOG: Do not send any Order ID
                    intent.putExtra("order_id", "")
                    intent.putExtra("product_id", if (product_id.isNotBlank()) product_id else re_itemid)
                    intent.putExtra("coupon_id", coupon_id)
                    intent.putExtra("quantity", if (quantity.isNotBlank()) quantity else re_quantity)
                    intent.putExtra("delivery_address", DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation).toString())
                    intent.putExtra("instructions", edt_specialInstructions.text.toString())
                    startActivity(intent)
                    */
                }else{
                    minimumQty(order_amount.toString())
                }

            } else {
                Dialogs.showNetworkErrorDialog(getContext())
            }
        }

    }

    private fun addPhone() {
        val dialog = Dialog(this@CartActivity)
        dialog.setContentView(R.layout.update_phone)
        dialog.setCancelable(true)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (!dialog.isShowing) {
            dialog.show()
        }

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btn_continue).setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle()
            bundle.putParcelable("profileResponeModel", profileResponeModel)
            startActivity(Intent(this, EditProfileActivity::class.java).putExtras(bundle))

        }
    }

    fun getProfileDataApi() {

        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)?.getProfileDataApi(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : Callback<ProfileResponeModel> {
                    override fun onFailure(call: Call<ProfileResponeModel>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<ProfileResponeModel>, response: Response<ProfileResponeModel>) {
                        if (!response.isSuccessful)
                            return
                        profileResponeModel = response.body()

                    }

                })

    }

    private fun minimumQty(amount:String) {
        val dialog = Dialog(this@CartActivity)
        dialog.setContentView(R.layout.min_qty)
        dialog.setCancelable(true)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (!dialog.isShowing) {
            dialog.show()
        }

        dialog.findViewById<TextView>(R.id.txt_min_qty).setText(resources.getString(R.string.minimum_qty) + " "+amount +resources.getString(R.string.Price_symbol))
        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btn_continue).setOnClickListener {
            finish()
        }
    }

    private fun restaurantUpdate() {
        val dialog = Dialog(this@CartActivity)
        dialog.setContentView(R.layout.select_rest1)
        dialog.setCancelable(true)
        if (!dialog.isShowing) {
            dialog.show()
        }

        var recyler_select_restaurent: RecyclerView = dialog.findViewById(R.id.rv_restaurent_list)
        val progress_bar: ProgressBar = dialog.findViewById(R.id.progress_bar)

        if (restaurants != null) {
            var selectRestAdapter: AdapterSelectRest = AdapterSelectRest(this@CartActivity!!, restaurants)
            recyler_select_restaurent!!.adapter = selectRestAdapter
            selectRestAdapter!!.performItemClick(object : ClickWithText {
                override fun itemClick(pos: Int, tesxt: String) {
                    if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {

                        sendRestaurentDataToServer(restaurants[pos].id, dialog)

                        tv_restarunatAddress.text = restaurants[pos].name.toString() + "\n" + restaurants[pos].address.toString()

                        DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.restaurantAddress, restaurants[pos].address)
                        DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.restaurantName, restaurants[pos].name)

                    } else {
                        Dialogs.showNetworkErrorDialog(getContext())
                    }

                }
            })
        }

    }


    private fun sendRestaurentDataToServer(id: Int, dialog: Dialog) {
        progress_bar.visibility = View.VISIBLE
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)
                ?.selectRestaurentApi(
                        DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!,
                        id)
                ?.enqueue(object : Callback<SelectRestaurantResponse> {
                    override fun onResponse(call: Call<SelectRestaurantResponse>, response: Response<SelectRestaurantResponse>) {
                        progress_bar.visibility = View.GONE
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error == false) {
                            dialog.dismiss()
                        } else {
                            DelhiDarbar?.instance?.commonMethods?.showDialogOK(
                                    this@CartActivity,
                                    response.body()!!.message,
                                    DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            when (p1) {
                                                DialogInterface.BUTTON_POSITIVE -> {
                                                    p0?.dismiss()
                                                }
                                                DialogInterface.BUTTON_NEGATIVE -> {
                                                    p0?.dismiss()

                                                }
                                            }

                                        }

                                        override fun invoke(p1: DialogInterface, p2: Int) {
                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                        }

                                    }), resources.getString(R.string.ok))
                        }

                    }

                    override fun onFailure(call: Call<SelectRestaurantResponse>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                    }

                })
    }

    private fun allRestaurantsApi() {
        RetrofitUtils.getRetrofitUtils()!!.create(Api::class.java).allRestaurants().enqueue(object : Callback<SelectRestaurentModel> {
            override fun onResponse(call: Call<SelectRestaurentModel>, response: Response<SelectRestaurentModel>) {

                if (!response.isSuccessful)
                    return

                restaurants = response.body()!!.restaurants
            }

            override fun onFailure(call: Call<SelectRestaurentModel>, t: Throwable) {
                progress_bar.visibility = View.GONE
                DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@CartActivity)
            }
        })

    }

/*
    private fun openFullScreenDialogForUpdateAddress() {
        val dialog = Dialog(this@CartActivity*/
/*, R.style.full_screen_dialog*//*
)
        dialog.setContentView(R.layout.dialog_update_address)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }
        val edt_EnterLocation_edit_text by lazy { dialog.findViewById<EditText>(R.id.edt_EnterLocation_edit_text) as EditText }
        val tv_userCurrentLocation by lazy { dialog.findViewById<EditText>(R.id.tv_userCurrentLocation) as TextView }

        val progress_bar: ProgressBar = dialog.findViewById(R.id.pb_progressBar)


        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btn_next).setOnClickListener {
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)!!.toDouble(),
                    DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)!!.toDouble())

            if (edt_EnterLocation_edit_text.text!!.toString().trim().length == 0 && tv_userCurrentLocation.text.toString().trim().length == 0) {
                tv_userCurrentLocation.error = getString(R.string.Please_enter_location)
                edt_EnterLocation_edit_text.error = getString(R.string.Please_enter_location)

            } else {
                if (edt_EnterLocation_edit_text.text!!.toString().trim().length > 0) {
                    sendUserUserLocationToTheServer(address = edt_EnterLocation_edit_text.text!!.toString(), latitude = "", longitude = "", progress_bar = progress_bar, dialog = dialog)
                } else if (tv_userCurrentLocation.text.toString().trim().length > 0) {
                    sendUserUserLocationToTheServer(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)!!,
                            DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)!!,
                            str_currrentAddress!!, dialog, progress_bar)
                }
            }
        }

        tv_userCurrentLocation.setOnClickListener {
            Log.i("tvEnterLocation", "onViewClicked: Clicked")
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)!!.toDouble(),
                    DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)!!.toDouble())
            Log.e(LocationFragment.TAG, "onTvClick: 93->$str_currrentAddress")
            tv_userCurrentLocation.text = str_currrentAddress
        }
    }
*/

/*
    fun sendUserUserLocationToTheServer(latitude: String, longitude: String, address: String, dialog: Dialog, progress_bar: ProgressBar) {
        RetrofitUtils.getRetrofitUtils()
                ?.create(Api::class.java)
                ?.updateUserLocation(
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!,
                        latitude, longitude, address)
                ?.enqueue(object : Callback<UpdateLocationResponseModel> {
                    override fun onFailure(call: Call<UpdateLocationResponseModel>, t: Throwable) {
                        Log.e("Location Fragment fail", "onFail" + t)
                    }

                    override fun onResponse(call: Call<UpdateLocationResponseModel>, response: Response<UpdateLocationResponseModel>) {
                        if (!response.isSuccessful)
                            return
                        Log.e("Location Fragment ", "onResponse" + response.body())
                        if (response.body()!!.error == false) {
                            DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.userLocation, response.body()!!.user.location_address)
                            tv_userAddressHere.text = DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.userLocation)
                            tv_userAddressHere.paintFlags = tv_userAddressHere.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                            dialog.dismiss()
                        }
                    }
                })

    }
*/

    var str_coupon_percentage: String = ""
    var discount_amount: Double = 0.0

    private fun openFullScreenDialogForAllCoupon() {
        val dialog = Dialog(this@CartActivity, R.style.full_screen_dialog)
        dialog.setContentView(R.layout.dialog_have_a_coupon)
        dialog.setCancelable(false)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val rv_haveAnCoupon by lazy { dialog.findViewById<RecyclerView>(R.id.rv_haveAnCoupon) as RecyclerView }
        val iv_BackButtonToolbar by lazy { dialog.findViewById<ImageView>(R.id.iv_BackButtonToolbar) as ImageView }
        iv_BackButtonToolbar.setOnClickListener { dialog.dismiss() }
        adapterAllCoupon = AdapterAllCoupons(this@CartActivity, viewAllCouponResponse!!.coupons)
        rv_haveAnCoupon.adapter = adapterAllCoupon

        adapterAllCoupon.performItemClick(object : ClickWithText {
            override fun itemClick(postion: Int, name: String) {
                tv_couponApplied = findViewById(R.id.tv_couponApplied)
                ll_couponApplied = findViewById(R.id.ll_couponApplied)

                tv_haveAnCoupon.text = viewAllCouponResponse!!.coupons!![postion]!!.title

                str_coupon_percentage = viewAllCouponResponse!!.coupons!![postion]!!.percentage.toString()
                coupon_id = viewAllCouponResponse!!.coupons!![postion]!!.id.toString()

                discount_amount = (str_coupon_percentage.toDouble() / 100) * order_amount
                order_amount = order_amount - discount_amount

                total_order_amount = order_amount.toDouble()

                tv_GrandTotal.text = String.format("%.2f", order_amount).toString() + " " + resources.getString(R.string.Price_symbol)
                //tv_price.text = String.format("%.2f", order_amount).toString() + " "+ resources.getString(R.string.Price_symbol)

                tv_couponApplied.text = "-" + "${DecimalFormat("##.00").format(discount_amount)} ${resources.getString(R.string.Price_symbol)}"
                ll_couponApplied.visibility = View.VISIBLE
                nextHaveCoupopn.visibility = View.GONE
                delCoupon.visibility = View.VISIBLE

                dialog.dismiss()

                // hitApiSelectCoupon(dialog, coupoun_id = viewAllCouponResponse!!.coupons!![postion]!!.id.toString(), percentage = viewAllCouponResponse!!.coupons!![postion]!!.percentage.toString())
            }
        })
        if (!dialog.isShowing) {
            dialog.show()
        }
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, keyCode: Int, p2: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    p0!!.dismiss()
                }
                return true
            }
        })
    }


    val TAG = CartActivity::class.java.simpleName

    private fun viewAllCoupons() {
        RetrofitUtils
                .getRetrofitUtils()!!
                .create(Api::class.java)
                .viewAllCoupons()
                .enqueue(object : Callback<ViewAllCouponResponse> {

                    override fun onFailure(call: Call<ViewAllCouponResponse>, t: Throwable) {
                        Log.e(TAG, "TAG-quantity->$t")

                    }

                    override fun onResponse(call: Call<ViewAllCouponResponse>, response: Response<ViewAllCouponResponse>) {
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error == false) {
                            viewAllCouponResponse = response.body()
                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(this@CartActivity,
                                    response.body()!!.message
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
                    }
                })
    }

    fun onBraintreeSubmit(CLIENT_TOKEN: String) {
        val dropInRequest = DropInRequest()
                .clientToken(CLIENT_TOKEN)
        dropInRequest.amount("0.0")
        startActivityForResult(dropInRequest.getIntent(this), 1501)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1501) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                // use the result to update your UI and send the payment method nonce to your server
                // Toast.makeText(this, "Successfull", Toast.LENGTH_SHORT).show()

                orderPlaced()
                displayResult(result.paymentMethodNonce, "")
                isMyPaymentSucccessORnot = true


/*
                DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ActivityViewCart,
                        resources.getString(R.string.order_places_success)
                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                    override fun invoke(p1: DialogInterface, p2: Int) {
                        when (p2) {
                            DialogInterface.BUTTON_POSITIVE -> {

                                p1.dismiss()
                                displayResult(result.paymentMethodNonce, "")
                                isMyPaymentSucccessORnot = true
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                p1.dismiss()
                            }
                        }
                    }

                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                }), resources.getString(R.string.ok))
*/


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                isMyPaymentSucccessORnot = false
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                // handle errors here, an exception may be available in
                val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                isMyPaymentSucccessORnot = false
                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun orderPlaced() {
        val dialog = Dialog(this@CartActivity, R.style.full_screen_dialog)
        dialog.setContentView(R.layout.order_placed)
        dialog.setCancelable(false)

        DelhiDarbar.instance?.hideStatusBar(this)

        if (!dialog.isShowing) {
            dialog.show()
        }

        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this, Dashboard::class.java)
            startActivity(mainIntent)
            finish()
        }, 5000)

        dialog.setOnKeyListener { dialogInterface, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_BACK) {
                startActivity(Intent(ActivityViewCart@ this, Dashboard::class.java))
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
            Log.e(TAG, "displayResult: 93-=>" + cardNonce.nonce)
            clearMycartHere(cardNonce.nonce)
            // realmController.clearAll()
        } else if (mNonce is PayPalAccountNonce) {
            val paypalAccountNonce = mNonce as PayPalAccountNonce
            Log.e(TAG, "displayResult: 106-=>" + paypalAccountNonce.nonce)
            clearMycartHere(paypalAccountNonce.nonce)
            // realmController.clearAll()
        }
    }

    /**
     * this is the  funtion for clear our cart and finish to go on HomeFragment
     * Remove function when the payment is succesfull
     */

    fun clearMycartHere(token: String) {
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {
            progress_bar.visibility = View.VISIBLE
            DelhiDarbar.instance!!.commonMethods.disableScreenInteraction(this@CartActivity)
            RetrofitUtils
                    .getRetrofitUtils()
                    ?.create(Api::class.java)
                    ?.afterPaymentSuccess(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!,
                            order_id, token, total_order_amount.toString())
                    ?.enqueue(object : Callback<SelectRestaurantResponse> {
                        override fun onFailure(call: Call<SelectRestaurantResponse>, t: Throwable) {
                            progress_bar.visibility = View.GONE
                            DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(this@CartActivity)
                        }

                        override fun onResponse(call: Call<SelectRestaurantResponse>, response: Response<SelectRestaurantResponse>) {
                            progress_bar.visibility = View.GONE
                            DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(this@CartActivity)
                            if (!response.isSuccessful)
                                return
                            else {

                                RealmController.getInstance().clearAll()

                                //  openOrderRatingDialog(viewCartResponse)
/*
                                DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ActivityViewCart,
                                        response.body()!!.message
                                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                    override fun invoke(p1: DialogInterface, p2: Int) {
                                        when (p2) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                p1.dismiss()

                                                openOrderRatingDialog(viewCartResponse)
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                p1.dismiss()
                                            }
                                        }
                                    }

                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                    }
                                }), resources.getString(R.string.ok))
*/
                            }
                        }
                    })
        } else {
            Dialogs.showNetworkErrorDialog(getContext())
        }


    }

    private fun openOrderRatingDialog(viewCartResponse: PrepareCartResponse) {
        val dialog = Dialog(this@CartActivity, R.style.full_screen_dialog)
        dialog.setContentView(R.layout.dialog_rating_or_order)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }
        var newMyRating = 0
        val gradientBackgroundView = dialog.findViewById<GradientBackgroundView>(R.id.gradientBackgroundView)
        val emotionView = dialog.findViewById<EmotionView>(R.id.emotionView)
        val ratingView = dialog.findViewById<RatingView>(R.id.ratingView)
        val editText = dialog.findViewById<EditText>(R.id.editText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        ratingView.setRatingChangeListener { previousRating, newRating ->
            emotionView.setRating(previousRating, newRating)
            gradientBackgroundView.changeBackground(previousRating, newRating)
            if (newRating > 0) {
                newMyRating = newRating
                submitButton.isEnabled = true
            }
        }
        dialog.findViewById<Button>(R.id.submitButton).setOnClickListener {
            if (editText.text.toString().trim().length > 0) {
                dialog.dismiss()
                if (DelhiDarbar!!.instance!!.commonMethods.checkInternetConnect(this@CartActivity)) {
                    hitSubmitRatingAPi(viewCartResponse, dialog, editText.text.toString().trim(), newMyRating.toString())
                } else {
                    toast(R.string.alert_internetConnection)
                }
                /**/
//                finish()
            } else {
                DelhiDarbar.instance!!.commonMethods.showDialogOK(this@CartActivity,
                        resources.getString(R.string.ur_feed_back_important_for_us)
                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                    override fun invoke(p1: DialogInterface, p2: Int) {
                        when (p2) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                p1.dismiss()
                                editText.requestFocus()
                                DelhiDarbar.instance!!.commonMethods.showKeyboardForceFully(this@CartActivity, editText)
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
    }

    private fun hitSubmitRatingAPi(viewCartResponse: PrepareCartResponse, dialog: Dialog, description: String, rating: String) {
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)
                ?.submitRating(description, rating, amount, viewCartResponse.restaurant!!.address!!, viewCartResponse.restaurant!!.address!!,
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)
                ?.enqueue(object : Callback<SelectRestaurantResponse> {
                    override fun onFailure(call: Call<SelectRestaurantResponse>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(this@CartActivity)
                    }

                    override fun onResponse(call: Call<SelectRestaurantResponse>, response: Response<SelectRestaurantResponse>) {
                        progress_bar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(this@CartActivity)
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error == false) {
                            for (i in 0 until viewCartResponse.cart!![0]!!.items!!.size) {
                                if (viewCartResponse.cart!![0]!!.items!![i]!!.quantity!!.toInt() > 0) {
                                    viewCartResponse.cart!![0]!!.items!![i]!!.quantity = "0"
                                }
                            }
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(this@CartActivity,
                                    response.body()!!.message
                                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            dialog.dismiss()
                                            p1.dismiss()
                                            finish()
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


/*
    private fun hitApiSelectCoupon(dialog: Dialog, percentage: String, coupoun_id: String) {
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(this@ActivityViewCart)
        progress_bar.visibility = View.VISIBLE
        RetrofitUtils
                .getRetrofitUtils()!!
                .create(Api::class.java)
                .selectCoupon(
                        coupoun_id,
                        percentage,
                        tempOrignameamount,
                        order_number,
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!
                )
                .enqueue(object : Callback<PrepareCartResponse> {
                    override fun onFailure(call: Call<PrepareCartResponse>, t: Throwable) {
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                        progress_bar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ActivityViewCart,
                                getString(R.string.request_time_out_error)
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

                    override fun onResponse(call: Call<PrepareCartResponse>, response: Response<PrepareCartResponse>) {
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                        progress_bar.visibility = View.GONE
                        dialog.dismiss()
                        if (!response.isSuccessful)
                            return
//
                        viewCartResponse = response.body()!!
                        if (response.body()!!.error == false) {

                            amount = viewCartResponse.cart!![0]!!.totalAmount.toString()
                            order_number = viewCartResponse.cart!![0]!!.orderNumber!!

                            //adapter called before

                            tv_couponApplied.text = "${DecimalFormat("##.00").format(viewCartResponse.cart!![0]!!.discount_amount)} ${resources.getString(R.string.Price_symbol)}"
                            ll_couponApplied.visibility = View.VISIBLE
                            tv_price.text = "${DecimalFormat("##.00").format(viewCartResponse.cart!![0]!!.totalAmount)} ${resources.getString(R.string.Price_symbol)}"
                            tv_GrandTotal.text = "${DecimalFormat("##.00").format(viewCartResponse.cart!![0]!!.discount)} ${resources.getString(R.string.Price_symbol)}"

                            tv_restarunatAddress.text = "${viewCartResponse.restaurant!!.name} ${viewCartResponse.restaurant!!.address}"
                            tv_restarunatAddress.paintFlags = tv_restarunatAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(
                                    this@ActivityViewCart,
                                    response.body()!!.message
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
                    }
                })
    }
*/

/*
    private fun initViews(bundle: Bundle) {
        // val bundle: Bundle? = intent.extras
        if (bundle != null) {
            tv_couponApplied = findViewById(R.id.tv_couponApplied)
            ll_couponApplied = findViewById(R.id.ll_couponApplied)
            if (bundle != null) {
                viewCartResponse = bundle.getParcelable("PrepareCartResponse")
                Log.e("TAG", "48<<>>$viewCartResponse")
                if (viewCartResponse.error == false) {
                    amount = viewCartResponse.cart!![0]!!.totalAmount.toString()
                    tempOrignameamount = viewCartResponse.cart!![0]!!.totalAmount.toString()
                    order_number = viewCartResponse.cart!![0]!!.orderNumber!!

                    //  adapterViewCart = AdapterViewCart(this@ActivityViewCart, viewCartResponse.cart!![0]!!.items, mutableList)
                    // rv_cartItems.adapter = adapterViewCart
*/
/*
                    adapterViewCart.performItemClick(object : ClickVIewCartInterface {
                        override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: kotlin.Double, from: String) {
                            if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@ActivityViewCart)) {
                                viewCartApi(viewCartResponse, position, isEnable, mutableList)
                            } else {
                                DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ActivityViewCart, getString(R.string.Please_try_again_later_)
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
                        }
                    })
*//*

                }
                //total_amt==null

                //  set price
*/
/*
                if (viewCartResponse.cart!!.size > 0 && viewCartResponse.cart!![0]!!.totalAmount != null) {
                    var price:String = String.format("%.2f", viewCartResponse.cart!![0]!!.totalAmount).toDouble().toString()

                    tv_price.text = "${price} ${resources.getString(R.string.Price_symbol)}"
                    tv_GrandTotal.text = "${price} ${resources.getString(R.string.Price_symbol)}"
                }
*//*


                //check restarunatAddress==null
                if (viewCartResponse.restaurant != null && viewCartResponse.restaurant!!.name != null && viewCartResponse.restaurant!!.address != null) {
                    tv_restarunatAddress.text = "${viewCartResponse.restaurant!!.name} ${viewCartResponse.restaurant!!.address}"
                    tv_restarunatAddress.paintFlags = tv_restarunatAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                }

                if (viewCartResponse.user?.locationAddress != null) {
                    tv_userAddressHere.text = viewCartResponse.user?.locationAddress
                    tv_userAddressHere.paintFlags = tv_userAddressHere.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                }
                if (viewCartResponse.user?.name != null && viewCartResponse.user?.phone != null) {
                    tv__userNameNphone.text = "${viewCartResponse.user?.name}, ${viewCartResponse.user?.phone}"
                    tv__userNameNphone.paintFlags = tv__userNameNphone.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                }
                tv_userAddressHere.setOnClickListener {
                    if (viewCartResponse.user?.locationAddress != null) {
                        DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                                this@ActivityViewCart,
                                resources.getString(R.string.do_u_want_to_change_address)
                                , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                            override fun invoke(p1: DialogInterface, p2: Int) {
                                when (p2) {
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        Log.e("111", "ASDASD")
                                        p1.dismiss()
                                        openFullScreenDialogForUpdateAddress()
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        Log.e("111", "BUTTON_NEGATIVE")
                                    }
                                }
                            }

                            override fun onClick(p0: DialogInterface?, p1: Int) {
                            }
                        }), resources.getString(R.string.ok), resources.getString(R.string.Cancel))
                    } else if (viewCartResponse.user?.locationAddress == null) {
                        DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                                this@ActivityViewCart,
                                resources.getString(R.string.no_address_found)
                                , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                            override fun invoke(p1: DialogInterface, p2: Int) {
                                when (p2) {
                                    DialogInterface.BUTTON_POSITIVE -> {

                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        p1.dismiss()
                                    }
                                }
                            }

                            override fun onClick(p0: DialogInterface?, p1: Int) {
                            }
                        }), resources.getString(R.string.ok), resources.getString(R.string.Cancel))
                    }
                }
                tv_haveAnCoupon.setOnClickListener {
                    nextHaveCoupopn.performClick()
                }
                nextHaveCoupopn.setOnClickListener {
                    openFullScreenDialogForAllCoupon()
                }
            }
            viewAllCoupons()
//            tv_creatMyPayment.setOnClickListener { onBraintreeSubmit(CLIENT_TOKEN) }
            var my_haveAnCoupon = ""
            if (!tv_haveAnCoupon.text.toString().trim().equals(resources.getString(R.string.have_a_coupon))) {
                my_haveAnCoupon = tv_haveAnCoupon.text.toString().trim()
            }
            edt_specialInstructions.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edt_specialInstructions.lineCount >= 3) {
                        edt_specialInstructions.getText().delete(edt_specialInstructions.getSelectionEnd() - 1, edt_specialInstructions.getSelectionStart());
                    }
                }
            })

            btn_placeOrder = findViewById(R.id.btn_placeOrder)
            btn_placeOrder.setOnClickListener {
                if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@ActivityViewCart)) {
                    placeAnOrder(viewCartResponse.cart!![0]!!.items!![0]!!.orderId!!, viewCartResponse.cart!![0]!!.id!!,
                            my_haveAnCoupon, amount,
                            viewCartResponse.user!!.locationAddress!!, edt_specialInstructions.text.toString().trim(),
                            str_coupon_percentage)
                } else {
                    Dialogs.showNetworkErrorDialog(getContext())
                }
            }
        }
    }
*/


/*    private fun ApiAddToMyCart(isQty: Int) {
    progress_bar.visibility = View.VISIBLE
    DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(this)
    Log.e(HomeFragment::class.java.simpleName, "171>><<" + jsonString)
    RetrofitUtils
            .getRetrofitUtils()!!
            .create(Api::class.java)
            .addUserCart(jsonString)
            .enqueue(object : Callback<PrepareCartResponse> {

                override fun onFailure(call: Call<PrepareCartResponse>, t: Throwable) {
                    progress_bar.visibility = View.GONE
                    DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                    if (t is Timeout) {
                        DelhiDarbar.instance!!.commonMethods.showDialogOK(
                                this@ActivityViewCart,
                                getString(R.string.request_time_out_error)
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

                }

                override fun onResponse(call: Call<PrepareCartResponse>, response: Response<PrepareCartResponse>) {
                    progress_bar.visibility = View.GONE
                    DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                    if (response.isSuccessful) {
                        Log.e("191", "Login :" + response.body())
                        val bundle = Bundle()
                        bundle.putParcelable("PrepareCartResponse", response.body())
                        bundle.putString(ConstantKeys.QUANTITY, "" + isQty)
                        //  startActivity(Intent(this@ActivityViewCart, ActivityViewCart::class.java).putExtras(bundle))

                        initViews(bundle)

                    } else {
                        return
                    }
                }
            })
}


    fun viewCartApi(prepareCartResponse: PrepareCartResponse, position: Int, qty: Int, mutableList: MutableList<MyRealm>) {
    DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(this@ActivityViewCart)
    Log.e(TAG, "TAG-quantity->$qty")
    Log.e(TAG, "TAG-item_id->${prepareCartResponse.cart!![0]!!.items!![position]!!.itemId!!}")
    Log.e(TAG, "TAG-order_number->${prepareCartResponse.cart[0]!!.orderNumber!!}")
    Log.e(TAG, "TAG-user_id->${DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!}")
    Log.e(TAG, "TAG-order_id->${prepareCartResponse.cart[0]!!.items!![position]!!.orderId!!}")
    RetrofitUtils
            .getRetrofitUtils()!!
            .create(Api::class.java)
            .viewUserCart(qty.toString(),
                    prepareCartResponse.cart[0]!!.items!![position]!!.itemId!!,
                    prepareCartResponse.cart[0]!!.orderNumber!!,
                    DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!,
                    prepareCartResponse.cart[0]!!.items!![position]!!.orderId!!)
            .enqueue(object : Callback<PrepareCartResponse> {
                override fun onResponse(call: Call<PrepareCartResponse>, response: Response<PrepareCartResponse>) {
                    DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                    if (!response.isSuccessful)
                        return
                    viewCartResponse = response.body()!!


                    print(response.body()!!)
                    if (response.body()!!.error == false) {
                        amount = viewCartResponse.cart!![0]!!.totalAmount.toString()
                        order_number = viewCartResponse.cart!![0]!!.orderNumber!!
                        if (viewCartResponse.cart!![0]!!.items != null) {
                            //adapterViewCart = AdapterViewCart(this@ActivityViewCart, viewCartResponse.cart!![0]!!.items, mutableList)
                            // rv_cartItems.adapter = adapterViewCart
/*
                            adapterViewCart.performItemClick(object : ClickVIewCartInterface {
                                override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: kotlin.Double, from: String) {
                                    if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(this@ActivityViewCart)) {
                                        viewCartApi(viewCartResponse, position, isEnable, mutableList)
                                    } else {
                                        DelhiDarbar.instance!!.commonMethods.showDialogOK(this@ActivityViewCart, getString(R.string.Please_try_again_later_)
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
                                }
                            })
*/
                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(
                                    this@ActivityViewCart,
                                    resources.getString(R.string.opps_no_more_item_found)
                                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            isMyPaymentSucccessORnot = true
                                            p1.dismiss()
                                            finish()
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

                        var price: String = String.format("%.2f", viewCartResponse.cart!![0]!!.totalAmount).toDouble().toString()

                        tv_price.text = "${price} ${resources.getString(R.string.Price_symbol)}"
                        tv_GrandTotal.text = "${price} ${resources.getString(R.string.Price_symbol)}"
                        tv_restarunatAddress.text = "${viewCartResponse.restaurant!!.name} ${viewCartResponse.restaurant!!.address}"
                        tv_restarunatAddress.paintFlags = tv_restarunatAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    } else {
                        DelhiDarbar.instance!!.commonMethods.showDialogOK(
                                this@ActivityViewCart,
                                response.body()!!.message
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

                }

                override fun onFailure(call: Call<PrepareCartResponse>, t: Throwable) {
                    DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(this@ActivityViewCart)
                }
            })
}

*/

}
