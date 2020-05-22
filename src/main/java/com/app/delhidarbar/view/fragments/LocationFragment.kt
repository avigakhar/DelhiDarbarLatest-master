package com.app.delhidarbar.view.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.DelhiDarbar.Companion.instance
import com.app.delhidarbar.model.recent_location.RecentLocation
import com.app.delhidarbar.model.update_user_location.UpdateLocationResponseModel
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickWithText
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.view.adapter.recent_location.AdapterRecentLocation
import kotlinx.android.synthetic.main.fragment_location.*
import kotlinx.android.synthetic.main.fragment_location.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Double

class LocationFragment : Fragment() {
    lateinit var pb_location: ProgressBar
    lateinit var rv_location:RecyclerView

    companion object {
        val TAG = LocationFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb_location = view.findViewById(R.id.pb_location)
        rv_location= view.findViewById(R.id.rv_location)
        onClick(view)

    }

    private fun replcaeFragment() {
        val manager = fragmentManager
        val transaction = manager!!.beginTransaction()
        transaction.replace(R.id.fragment_contianor, Select_restaurent())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onClick(view: View) {
        view.btn_next.setOnClickListener {

            if (et_street!!.text!!.toString().trim().length == 0) {
                et_street!!.error = getString(R.string.Please_enter_street)
            }else if(et_building!!.text.toString().trim().length == 0) {
                et_building!!.error = getString(R.string.Please_enter_building)
            }else if(et_city!!.text.toString().trim().length == 0) {
                et_city!!.error = getString(R.string.Please_enter_city)
            }else if(et_contact!!.text.toString().trim().length == 0) {
                et_contact!!.error = getString(R.string.Please_enter_phone)
            } else {
                var add = et_street!!.text!!.toString() + ", "+ et_building!!.text!!.toString() + ", "+ et_city!!.text!!.toString() + ", "+ et_postalCode!!.text!!.toString();
                sendUserUserLocationToTheServer(address = add, contact = et_contact!!.text!!.toString(), note = et_note!!.text!!.toString(),
                        name = instance!!.mySharedPrefrence.getString("userName")!!, email = instance!!.mySharedPrefrence.getString("email")!!)

                DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.userLocation, add+ ", "+ et_contact!!.text!!.toString());


            }
        }


        view.tvEnterLocation.setOnClickListener {
            Log.i("tvEnterLocation", "onViewClicked: Clicked")

            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)),
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)))
            Log.e(TAG, "onTvClick: 93->$str_currrentAddress")
            tvEnterLocation!!.text = str_currrentAddress
        }

        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
            apiRecentLocation()
        } else {
            DelhiDarbar.instance!!.commonMethods.showDialogOK(activity, getString(R.string.Please_try_again_later_)
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

    private fun apiRecentLocation() {
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.locationApi(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : Callback<RecentLocation> {

            override fun onResponse(call: Call<RecentLocation>, response: Response<RecentLocation>) {
                Log.e(TAG, "78>>${response.body()}")
                if (!response.isSuccessful)
                    return
                val recentLocationAdpter = AdapterRecentLocation(activity!!, response.body()!!.locations)
                rv_location.adapter = recentLocationAdpter
                recentLocationAdpter.performItemClick(object : ClickWithText {
                    override fun itemClick(postion: Int, name: String) {
//                      sendUserUserLocationToTheServer(address=response.body()!!.locations[postion]),latitude="",longitude="")
/*
                        sendUserUserLocationToTheServer(
                                response.body()!!.locations!![postion]!!.locationLatitude.toString(),
                                response.body()!!.locations!![postion]!!.locationLatitude.toString(),
                                response.body()!!.locations!![postion]!!.locationAddress!!
                        )
*/

                        DelhiDarbar.instance!!.mySharedPrefrence.putString(ConstantKeys.userLocation, response.body()!!.locations!![postion]!!.locationAddress!!)

                    }
                })
            }

            override fun onFailure(call: Call<RecentLocation>, t: Throwable) {
                Log.e(TAG, "78>>$t")
            }

        })
    }

    fun sendUserUserLocationToTheServer(address: String, contact:String, note:String, name:String, email:String) {
        pb_location.visibility = View.VISIBLE
        DelhiDarbar.instance!!.commonMethods.disableScreenInteraction(activity!!)
        RetrofitUtils.getRetrofitUtils()
                ?.create(Api::class.java)
                ?.updateUserLocation(
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, address, contact, note, name, email)
                ?.enqueue(object : Callback<UpdateLocationResponseModel> {
                    override fun onFailure(call: Call<UpdateLocationResponseModel>, t: Throwable) {
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity!!)
                        pb_location.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<UpdateLocationResponseModel>, response: Response<UpdateLocationResponseModel>) {
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity!!)
                        pb_location.visibility = View.GONE
                        if (!response.isSuccessful)
                            return
                        replcaeFragment()

                    }

                })

    }
}
