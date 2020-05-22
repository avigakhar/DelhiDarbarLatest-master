package com.app.delhidarbar.view.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.model.dashboard.home.Category
import com.app.delhidarbar.model.dashboard.home.Parent_Home
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.*
import com.app.delhidarbar.view.CartActivity
import com.app.delhidarbar.view.Dashboard
import com.app.delhidarbar.view.EditProfileActivity
import com.app.delhidarbar.view.adapter.AdapterCategory
import com.app.delhidarbar.view.adapter.dashboard_parent.DashboardParentAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dasboard_view_cart.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var edt_search: EditText
    var noItemsDialog: AlertDialog? = null
    lateinit var rv_dashboardRecyclerView: RecyclerView

    companion object {
        var preveg = "0"
        var lastCheckedPos = 0
        var vegOrNonVeglocal = "5"
        lateinit var ll_bottomBar: LinearLayout
        lateinit var rr_viewCart: RelativeLayout
        lateinit var pb_progressBar1: ProgressBar

        val TAG = HomeFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    lateinit var relo: RealmController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Dashboard.isactive = true
        initViews(view)
        //covid_popup()

        pb_progressBar1 = view.findViewById(R.id.pb_progressBar)

        ll_bottomBar = activity?.findViewById(R.id.ll_bottomBar) as LinearLayout
        rr_viewCart = activity?.findViewById(R.id.rr_viewMyCart) as RelativeLayout
        relo = RealmController.getInstance()
        view.findViewById<Button>(R.id.btn_viewCart).setOnClickListener {
            startActivity(Intent(activity, CartActivity::class.java))
        }

    }

    lateinit var linearLayoutManager: LinearLayoutManager

    private fun initViews(view: View) {
        edt_search = view.findViewById(R.id.edt_search)
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 2) {
                    apiSearch(s.toString())
                } else if (s.toString().length == 0) {
                    DelhiDarbar.instance!!.commonMethods.hideKeyboard(activity)
                    ApiDashboard(true, "", vegOrNonVeglocal, "")
                }
            }
        })

        rv_dashboardRecyclerView = view.findViewById(R.id.rv_dashboardRecyclerView)
        linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_dashboardRecyclerView.layoutManager = linearLayoutManager

        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
            ApiDashboard(true, "", vegOrNonVeglocal, "")

        } else {
            Dialogs.showNetworkErrorDialog(context)
        }
    }

    lateinit var dashboardParentAdapter: DashboardParentAdapter
    var actualPriceOfItemsMY = 0.0

    private fun covid_popup() {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.covid_popup)
        dialog.setCancelable(false)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun ApiDashboard(isFirstTime: Boolean, category_id: String, vegOrNonVeg: String, offer_id: String) {
//      type=> 0->veg
//      1->nonveg

        if (isFirstTime) {
            Log.e(TAG, "96::$isFirstTime")
            pb_progressBar.visibility = View.VISIBLE
            DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)
        }

        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.dashboard(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, category_id, vegOrNonVeg, offer_id)?.enqueue(object : Callback<Parent_Home> {
            override fun onFailure(call: Call<Parent_Home>, t: Throwable) {
                if (isFirstTime) {
                    pb_progressBar.visibility = View.GONE
                    DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                }

                Dialogs.showInformationDialog(context, t.localizedMessage)
                        ?.setButton(DialogInterface.BUTTON_POSITIVE, context?.getString(R.string.ok)) { dialog, _ ->
                            ApiDashboard(true, "", vegOrNonVeglocal, "")
                            dialog.dismiss()
                        }
            }

            override fun onResponse(call: Call<Parent_Home>, response: Response<Parent_Home>) {
                if (!response.isSuccessful) {
                    if (isFirstTime) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                    }

                    Dialogs.showInformationDialog(context, R.string.Please_try_again_later_)
                    return

                } else {

                    if (relo.count > 0) {
                        actualPriceOfItemsMY = 0.0 //newnew
                        for (i in 0 until relo.count) {
                            actualPriceOfItemsMY += relo.all[i].myFinalPrice
                        }
                        if (relo.count > 0) {
                            lay_cart.visibility = View.VISIBLE
                            txt_cartcount.text = relo.count.toString()

                            var s: String = String.format("%.2f", actualPriceOfItemsMY)

                            if (relo.count > 1)
                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                            else
                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"

                        } else {
                            rr_viewCart.visibility = View.GONE
                            lay_cart.visibility = View.GONE

                        }
                    }

                    if(response.body()!!.error){

                        Dialogs.showInformationDialog(context, resources.getString(R.string.server_error))

                    }else{
                        dashboardParentAdapter = DashboardParentAdapter(activity!!, preveg, response.body()!!, vegOrNonVeglocal, isFirstTime)
                        rv_dashboardRecyclerView.adapter = dashboardParentAdapter

                        dashboardParentAdapter.performItemClickIhaveToVisible(object : ClickVIewCartInterface {
                            override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                                when (from) {
                                    "childItemsAdapter" -> {
                                        actualPriceOfItemsMY = 0.0
                                        for (i in 0 until relo.count) {
                                            actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity
                                        }
                                        if (relo.count > 0) {
                                            lay_cart.visibility = View.VISIBLE
                                            txt_cartcount.text = relo.count.toString()

                                            var s: String = String.format("%.2f", actualPriceOfItemsMY)

                                            if (relo.count > 1) {
                                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                                            } else {
                                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"
                                            }

                                        } else {
                                            rr_viewCart.visibility = View.GONE
                                            lay_cart.visibility = View.GONE
                                        }

                                    }

                                    "childCategoriesAdapter" -> {
                                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                                            ApiDashboard(true, response.body()!!.categories[position].id.toString(), vegOrNonVeglocal, "")

                                        } else {
                                            Dialogs.showNetworkErrorDialog(context)
                                        }
                                    }

                                    "childOfferAdapter" -> {
                                        ApiDashboard(true, "", vegOrNonVeglocal, response.body()!!.offers[position].id.toString())
                                    }
                                }

                            }
                        })
                        dashboardParentAdapter.isVegOrNonVeg(object : IsVegOrNonVeg {
                            override fun iWant2EnableVegOrNon(nonVeg: String, cat_id: String) {
                                vegOrNonVeglocal = nonVeg
                                preveg = nonVeg
                                ApiDashboard(true, cat_id, vegOrNonVeglocal, "")
                            }
                        })
                        btn_viewCart.setOnClickListener {
                            if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                                val mutableList: MutableList<RealmProduct> = arrayListOf()
                                for (i in 0 until relo.count) {
                                    val myRealm = RealmProduct()
                                    myRealm.realmid = relo.all[i].realmid
                                    myRealm.id = relo.all[i].id
                                    myRealm.category_id = relo.all[i].category_id
                                    myRealm.reviews = relo.all[i].reviews
                                    myRealm.rating = relo.all[i].rating
                                    myRealm.quantity = relo.all[i].quantity
                                    myRealm.description = relo.all[i].description
                                    myRealm.image = relo.all[i].image
                                    myRealm.name = relo.all[i].name
                                    myRealm.type = relo.all[i].type
                                    myRealm.all_type = relo.all[i].all_type
                                    myRealm.select_type = relo.all[i].select_type
                                    myRealm.price = relo.all[i].price
                                    myRealm.myFinalPrice = relo.all[i].myFinalPrice
                                    mutableList.add(myRealm)
                                }

                                val example = PrePareMYCart(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, mutableList)
                                val gson = Gson()
                                jsonString = gson.toJson(example)

                                startActivity(Intent(activity, CartActivity::class.java))

                            } else {
                                Dialogs.showNetworkErrorDialog(context)
                            }

                        }
                        iv_floatSpoons.setOnClickListener {
                            openFullScreenDialogForGetCategory(response.body()!!.categories)
                        }

                        lay_cart.setOnClickListener {
                            if (relo.count > 0) {
                                startActivity(Intent(activity, CartActivity::class.java))

                                if (ll_bottomBar.visibility == View.GONE) {
                                    rr_viewCart.visibility = View.GONE
                                    //ll_bottomBar.visibility = View.VISIBLE
                                } else {
                                    //ll_bottomBar.visibility = View.GONE
                                    //rr_viewCart.visibility = View.VISIBLE
                                }
                            }

                        }

                    }

                    if (isFirstTime) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                    }

                }
            }

        })
    }

    var jsonString = ""
    data class PrePareMYCart(var user_id: String, var items: List<RealmProduct>)

    private fun apiSearch(search: String) {
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.search(search)?.enqueue(object : Callback<Parent_Home> {
            override fun onFailure(call: Call<Parent_Home>, t: Throwable) {
                Log.e(TAG, "78>>$t")
            }

            override fun onResponse(call: Call<Parent_Home>, response: Response<Parent_Home>) {
                if (!response.isSuccessful)
                    return
                if (!response.body()!!.error) {
                    if (relo.count > 0) {
                        actualPriceOfItemsMY = 0.0
                        for (i in 0 until relo.count) {
                            actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity
                        }
                        if (relo.count > 0) {
                            //ll_bottomBar.visibility = View.GONE
                            //rr_viewCart.visibility = View.VISIBLE

                            lay_cart.visibility = View.VISIBLE
                            txt_cartcount.text = relo.count.toString()

                            var s: String = String.format("%.2f", actualPriceOfItemsMY)

                            if (relo.count > 1)
                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                            else
                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"

                        } else {
                            rr_viewCart.visibility = View.GONE
                            lay_cart.visibility = View.GONE
                            //ll_bottomBar.visibility = View.VISIBLE
                        }
                    }

                    dashboardParentAdapter = DashboardParentAdapter(activity!!, preveg, response.body()!!, vegOrNonVeglocal, false)
                    rv_dashboardRecyclerView.adapter = dashboardParentAdapter

                    dashboardParentAdapter.performItemClickIhaveToVisible(object : ClickVIewCartInterface {
                        override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                            when (from) {
                                "childItemsAdapter" -> {
                                    actualPriceOfItemsMY = 0.0
                                    for (i in 0 until relo.count) {
                                        // actualPriceOfItemsMY += realmController.all[i].myFinalPrice
                                        actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity

                                    }
                                    if (relo.count > 0) {
                                        //ll_bottomBar.visibility = View.GONE
                                        //rr_viewCart.visibility = View.VISIBLE

                                        lay_cart.visibility = View.VISIBLE
                                        txt_cartcount.text = relo.count.toString()

                                        var s: String = String.format("%.2f", actualPriceOfItemsMY)

                                        if (relo.count > 1)
                                            tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                                        else
                                            tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"

                                    } else {
                                        rr_viewCart.visibility = View.GONE
                                        lay_cart.visibility = View.GONE
                                        //ll_bottomBar.visibility = View.VISIBLE
                                    }
                                }
                                "childCategoriesAdapter" -> {
                                    if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                                        ApiDashboard(true, response.body()!!.categories[position].id.toString(), vegOrNonVeglocal, "")
                                    } else {
                                        Dialogs.showNetworkErrorDialog(context)
                                    }
                                }
                                "childOfferAdapter" -> {
//                                  DelhiDarbar.instance!!.commonMethods.showToast(response.body()!!.offers[position].code)
                                    ApiDashboard(true, "", vegOrNonVeglocal, response.body()!!.offers[position].id.toString())
                                }
                            }

                        }
                    })

                    dashboardParentAdapter.isVegOrNonVeg(object : IsVegOrNonVeg {
                        override fun iWant2EnableVegOrNon(nonVeg: String, cat_id: String) {
                            vegOrNonVeglocal = nonVeg
                            preveg = nonVeg
                            ApiDashboard(true, cat_id, vegOrNonVeglocal, "")
                        }
                    })

                    btn_viewCart.setOnClickListener {
                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                            val mutableList: MutableList<RealmProduct> = arrayListOf()
                            for (i in 0 until relo.count) {
                                val myRealm = RealmProduct()
                                myRealm.realmid = relo.all[i].realmid
                                myRealm.id = relo.all[i].id
                                myRealm.category_id = relo.all[i].category_id
                                myRealm.reviews = relo.all[i].reviews
                                myRealm.rating = relo.all[i].rating
                                myRealm.quantity = relo.all[i].quantity
                                myRealm.description = relo.all[i].description
                                myRealm.image = relo.all[i].image
                                myRealm.name = relo.all[i].name
                                myRealm.type = relo.all[i].type
                                myRealm.all_type = relo.all[i].all_type
                                myRealm.select_type = relo.all[i].select_type
                                myRealm.price = relo.all[i].price
                                myRealm.myFinalPrice = relo.all[i].myFinalPrice
                                mutableList.add(myRealm)
                            }
                            val example = PrePareMYCart(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, mutableList)
                            val gson = Gson()
                            jsonString = gson.toJson(example)
                            // ApiAddToMyCart(isQty)

                            startActivity(Intent(activity, CartActivity::class.java))

                        } else {
                            Dialogs.showNetworkErrorDialog(context)
                        }
                    }

                    iv_floatSpoons.setOnClickListener {
                        openFullScreenDialogForGetCategory(response.body()!!.categories)
                    }
                } else {
                    DelhiDarbar.instance!!.commonMethods.hideKeyboard(activity)

                    if (noItemsDialog == null) {

                        val dialogBuilder = AlertDialog.Builder(this@HomeFragment.activity!!)
                        dialogBuilder
                                .setTitle(resources.getString(R.string.app_name))
                                .setMessage(R.string.no_item_found_)
                                .setIcon(R.mipmap.delhi_darbar)
                                .setCancelable(false)
                                .setPositiveButton(resources.getString(R.string.ok), DialogInterface.OnClickListener { dialog, id ->
                                    dialog.cancel()
                                })

                        noItemsDialog = dialogBuilder.create()
                    }

                    if (!noItemsDialog!!.isShowing) {
                        noItemsDialog?.show()
                    }
                }
            }
        })
    }

    private fun openFullScreenDialogForGetCategory(categories: MutableList<Category>) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_category)
        dialog.setCancelable(false)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val tv_myToolbarTxt by lazy { dialog.findViewById<TextView>(R.id.tv_myToolbarTxt) as TextView }
        val rv_haveAnCoupon by lazy { dialog.findViewById<RecyclerView>(R.id.rv_haveAnCoupon) as RecyclerView }
        val iv_BackButtonToolbar by lazy { dialog.findViewById<ImageView>(R.id.iv_BackButtonToolbar) as ImageView }
        iv_BackButtonToolbar.setOnClickListener { dialog.dismiss() }

        val adapterCategory = AdapterCategory(activity!!, categories)
        rv_haveAnCoupon.adapter = adapterCategory
        adapterCategory.performItemClick(object : ClickWithText {
            override fun itemClick(postion: Int, name: String) {
                if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                    dialog.dismiss()
                    DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.SELECTED_VALUE, categories[postion].name)
                    lastCheckedPos = postion
                    ApiDashboard(true, categories[postion].id.toString(), vegOrNonVeglocal, "")
                } else {
                    Dialogs.showNetworkErrorDialog(context)
                }
            }
        })

        tv_myToolbarTxt.text = resources.getString(R.string.select_category)
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

    override fun onResume() {
        super.onResume()

        DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),activity)
        edt_search.hint = resources.getString(R.string.search)

        Dashboard.isactive = true

        if (CartActivity.isMyPaymentSucccessORnot) {
            CartActivity.isMyPaymentSucccessORnot = false
            rr_viewCart.visibility = View.GONE
            lay_cart.visibility = View.GONE
            //ll_bottomBar.visibility = View.VISIBLE
            lastCheckedPos = 0
            ApiDashboard(false, "", vegOrNonVeglocal, "")

        } else {
            ll_bottomBar = activity?.findViewById(R.id.ll_bottomBar) as LinearLayout

            actualPriceOfItemsMY = 0.0
            for (i in 0 until relo.count) {
                actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity

                if (::dashboardParentAdapter.isInitialized)
                    dashboardParentAdapter.notifyDataSetChanged()
            }
            if (relo.count > 0) {
                //ll_bottomBar.visibility = View.GONE
                //rr_viewCart.visibility = View.VISIBLE

                lay_cart.visibility = View.VISIBLE
                txt_cartcount.text = relo.count.toString()

                var s: String = String.format("%.2f", actualPriceOfItemsMY)

                if (relo.count > 1)
                    tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                else
                    tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"

            } else {
                rr_viewCart.visibility = View.GONE
                lay_cart.visibility = View.GONE
                //ll_bottomBar.visibility = View.VISIBLE

            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (this.isVisible) {
            Dashboard.isactive = true
            actualPriceOfItemsMY = 0.0
            for (i in 0 until relo.count) {
                actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity

                if (::dashboardParentAdapter.isInitialized)
                    dashboardParentAdapter.notifyDataSetChanged()
            }
            if (relo.count > 0) {
                //ll_bottomBar.visibility = View.GONE
                //rr_viewCart.visibility = View.VISIBLE

                lay_cart.visibility = View.VISIBLE
                txt_cartcount.text = relo.count.toString()

                var s: String = String.format("%.2f", actualPriceOfItemsMY)

                if (relo.count > 1)
                    tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Items"
                else
                    tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"

            } else {
                rr_viewCart.visibility = View.GONE
                lay_cart.visibility = View.GONE
                //ll_bottomBar.visibility = View.VISIBLE
            }

            if (EditProfileActivity.updateprofile == true) {
                lastCheckedPos = 0
                ApiDashboard(true, "", vegOrNonVeglocal, "")
                EditProfileActivity.updateprofile = false
            }

        }
    }

}
