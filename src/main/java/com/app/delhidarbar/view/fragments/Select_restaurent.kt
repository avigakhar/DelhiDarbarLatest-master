package com.app.delhidarbar.view.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.model.restaurent.Restaurant
import com.app.delhidarbar.model.restaurent.SelectRestaurentModel
import com.app.delhidarbar.model.select_restaurent_model.SelectRestaurantResponse
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickWithText
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.view.Dashboard
import com.app.delhidarbar.view.adapter.AdapterSelectRest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Select_restaurent : Fragment() {
    lateinit var progress_bar: ProgressBar
    lateinit var textView6: TextView
    private var selectRestAdapter: AdapterSelectRest? = null
    private val restaurants_list: ArrayList<Restaurant>? = null
    private var recyler_select_restaurent: RecyclerView? = null

    companion object{
       var firstLogin: Boolean = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.select_rest, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyler_select_restaurent = view.findViewById(R.id.rv_restaurent_list)
        progress_bar = view.findViewById(R.id.progress_bar)
        textView6 = view.findViewById(R.id.textView6)

        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(context)) {
            allRestaurantsApi()
        } else {
            Dialogs.showNetworkErrorDialog(context)
        }

    }

    private fun allRestaurantsApi() {
        progress_bar.visibility = View.VISIBLE
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)

        RetrofitUtils.getRetrofitUtils()!!.create(Api::class.java).allRestaurants().enqueue(object : Callback<SelectRestaurentModel> {
            override fun onResponse(call: Call<SelectRestaurentModel>, response: Response<SelectRestaurentModel>) {

                progress_bar.visibility = View.GONE
                DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)

                if (!response.isSuccessful)
                    return
                selectRestAdapter = AdapterSelectRest(context!!, response.body()!!.restaurants)
                recyler_select_restaurent!!.adapter = selectRestAdapter

                textView6.setText(""+resources.getString(R.string.we_have)+ " "+ response!!.body()!!.restaurants.size+ " " + resources.getString(R.string.branches))

                selectRestAdapter!!.performItemClick(object : ClickWithText {
                    override fun itemClick(pos: Int, tesxt: String) {
                        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                            sendRestaurentDataToServer(response!!.body()!!.restaurants[pos].id)

                            DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.restaurantAddress, response!!.body()!!.restaurants[pos].address)
                            DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.restaurantName, response!!.body()!!.restaurants[pos].name)

                        } else {
                            Dialogs.showNetworkErrorDialog(context)
                        }

                    }
                })

            }

            override fun onFailure(call: Call<SelectRestaurentModel>, t: Throwable) {
                progress_bar.visibility = View.GONE
                DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
            }
        })

    }

    private fun sendRestaurentDataToServer(id: Int) {
        progress_bar.visibility  = View.VISIBLE
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
                            activity?.startActivity(Intent(activity, Dashboard::class.java))
                            activity?.finishAffinity()

                        } else {
                            DelhiDarbar?.instance?.commonMethods?.showDialogOK(
                                    activity,
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
                                            TODO("not implemented")
                                            //To change body of created functions use File | Settings | File Templates.
                                        }


                                    }), resources.getString(R.string.ok))
                        }

                    }

                    override fun onFailure(call: Call<SelectRestaurantResponse>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                    }

                })

    }
}

