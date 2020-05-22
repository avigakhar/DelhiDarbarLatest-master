package com.app.delhidarbar.view.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.model.dashboard.home.Parent_Home
import com.app.delhidarbar.model.dashboard.home.Product
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.RealmController
import com.app.delhidarbar.view.CartActivity
import com.app.delhidarbar.view.Dashboard
import com.app.delhidarbar.view.adapter.dashboard_parent.ProductsAdapter
import kotlinx.android.synthetic.main.dasboard_view_cart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    lateinit var rv_search: RecyclerView
    lateinit var edt_search: EditText
    lateinit var pb_progressBar: ProgressBar
    lateinit var lay_cart: LinearLayout
    lateinit var ll_bottomBar: LinearLayout
    lateinit var txt_cartcount: TextView
    lateinit var relo: RealmController
    lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        return view
    }

    private var _hasLoadedOnce = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (this.isVisible) {
            Dashboard.isactive = false

            relo = RealmController.getInstance()

            if (isVisibleToUser && !_hasLoadedOnce) {
                viewDidAppear()
                _hasLoadedOnce = true

            }else if(this.isVisible && ::productsAdapter.isInitialized){
                productsAdapter.notifyDataSetChanged()
                if(relo.count == 0){
                    lay_cart.visibility = View.GONE
                }
            }
        }
    }

    private fun viewDidAppear() {
        lay_cart = view!!.findViewById(R.id.lay_cart)
        rv_search = view!!.findViewById(R.id.rv_search)
        pb_progressBar = view!!.findViewById(R.id.pb_progressBar)
        edt_search = view!!.findViewById(R.id.edt_search)
        ll_bottomBar = activity?.findViewById(R.id.ll_bottomBar) as LinearLayout
        txt_cartcount = view?.findViewById(R.id.txt_cartcount) as TextView

        if (relo.count > 0) {
            // ll_bottomBar.visibility = View.GONE
            lay_cart.visibility = View.VISIBLE
            txt_cartcount.text = relo.count.toString()
        }

        lay_cart.setOnClickListener{
            if(relo.count > 0){
                startActivity(Intent(activity, CartActivity::class.java))
            }
        }

        edt_search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.toString().length > 2) {
                    if (DelhiDarbar.instance!!.commonMethods!!.checkInternetConnect(activity)) {
                        apiDashboard(s.toString())
                    } else {
                        Dialogs.showInformationDialog(context, R.string.Please_try_again_later_)
                    }
                }
            }
        })
    }

    val TAG = SearchFragment::class.java.simpleName
    var actualPriceOfItemsMY = 0.0

    private fun apiDashboard(search: String) {
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.search(search)?.enqueue(object : Callback<Parent_Home> {
            override fun onFailure(call: Call<Parent_Home>, t: Throwable) {
                Log.e(TAG, "78>>$t")
            }

            override fun onResponse(call: Call<Parent_Home>, response: Response<Parent_Home>) {
                if (!response.isSuccessful)
                    return
                if (!response.body()!!.error) {
                    productsAdapter = ProductsAdapter(activity!!, products = response.body()!!.items as ArrayList<Product>, isFirstTime = false)
                    rv_search.adapter = productsAdapter

                    productsAdapter.performItemClick(object : ClickVIewCartInterface {
                        override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {

                            actualPriceOfItemsMY = 0.0
                            for (i in 0 until relo.count) {
                                actualPriceOfItemsMY += relo.all[i].price * relo.all[i].quantity
                            }
                            if (relo.count > 0) {
                               // ll_bottomBar.visibility = View.GONE
                                lay_cart.visibility = View.VISIBLE

                                var s:String = String.format("%.2f", actualPriceOfItemsMY)

                                tv_myActualPrice.text = "" + s + "${resources.getString(R.string.Price_symbol)} - ${relo.count} Item"
                                txt_cartcount.text = relo.count.toString()

                            } else {
                                lay_cart.visibility = View.GONE
                              //  ll_bottomBar.visibility = View.VISIBLE
                            }
                        }
                    })
                    
                    btn_viewCart.setOnClickListener {
                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                            startActivity(Intent(activity, CartActivity::class.java).putExtra("parent", "search"))
                        } else {
                            Dialogs.showNetworkErrorDialog(getContext())
                        }
                    }
                } else {
                    Dialogs.showInformationDialog(context, response.body()!!.message)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),activity)

        if(view!=null){
            edt_search = view!!.findViewById(R.id.edt_search)
            edt_search.hint = resources.getString(R.string.search)

        }

    }

}

