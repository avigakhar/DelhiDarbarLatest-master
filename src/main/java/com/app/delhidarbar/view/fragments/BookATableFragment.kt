package com.app.delhidarbar.view.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.DelhiDarbar.Companion.instance
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.helper.RegexPatterns
import com.app.delhidarbar.model.get_all_restaurant.AllRestaurantResponse
import com.app.delhidarbar.model.select_restaurent_model.SelectRestaurantResponse
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickWithText
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.CustomTimePickerDialog
import com.app.delhidarbar.view.Dashboard
import com.app.delhidarbar.view.adapter.AdapterBookATable
import com.app.delhidarbar.view.adapter.AdapterSelectRest1
import kotlinx.android.synthetic.main.fragment_book_a_table.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class BookATableFragment : Fragment() {
    lateinit var pb_progressBar: ProgressBar
    lateinit var tv_selectRestaurant: TextView
    lateinit var rl_selectRestaurant: RelativeLayout
    lateinit var rl_selectRestaurantParty: RelativeLayout
    lateinit var tv_party: TextView
    lateinit var tv_book: TextView
    private var _hasLoadedOnce = false
    lateinit var tv_name :EditText
    lateinit var tv_email :EditText
    lateinit var tv_phone :EditText
    lateinit var tv_desc :EditText
    lateinit var language: String
    private val SAFETY_NET_API_SITE_KEY = "6LfKceAUAAAAAE6gvMgvo50_Sbjzx3lsVbs8lBlW"
    private val URL_VERIFY_ON_SERVER = "https://api.androidhive.info/google-recaptcha-verfication.php"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_book_a_table, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),activity)

        if(view!=null){
            try{
                viewDidAppear()

                tv1.text = resources.getString(R.string.book_a_table)
                tv_openDatePicker.text = resources.getString(R.string.date)
                tv_openTimePicker.text = resources.getString(R.string.time)
                tv_party.text = resources.getString(R.string.number_of_persons)
                tv2.text = resources.getString(R.string.contact_details)
                tv_selectRestaurant.text = resources.getString(R.string.select_restaurent)

                tv_name.hint = resources.getString(R.string.hint_name)
                tv_email.hint = resources.getString(R.string.email)
                tv_phone.hint = resources.getString(R.string.phone)
                tv_book.text = resources.getString(R.string.book)
                tv_desc.hint = resources.getString(R.string.add_special_instructions)

            }catch (e :Exception){
            }

        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (this.isVisible) {
            Dashboard.isactive = false

            if (isVisibleToUser && !_hasLoadedOnce) {
                viewDidAppear()
                _hasLoadedOnce = true
            }
        }
    }

    private fun viewDidAppear() {
        tv_name = view!!.findViewById(R.id.tv_name)
        tv_email = view!!.findViewById(R.id.tv_email)
        tv_phone = view!!.findViewById(R.id.tv_phone)
        tv_desc = view!!.findViewById(R.id.edt_specialInstructions)

        view!!.findViewById<RelativeLayout>(R.id.rl_selectRestaurantDate).setOnClickListener {
            openDatePicker()
        }

        view!!.findViewById<RelativeLayout>(R.id.rl_selectRestaurantime).setOnClickListener {
            if(tv_selectRestaurant.text.toString().trim().equals("") || tv_selectRestaurant.text.toString().trim().equals(resources.getString(R.string.select_restaurent))){
                Toast.makeText(activity, resources.getString(R.string.select_rest_first), Toast.LENGTH_SHORT).show()
            }else
                getTime()
        }

        tv_selectRestaurant = view!!.findViewById(R.id.tv_selectRestaurant)
        rl_selectRestaurant = view!!.findViewById(R.id.rl_selectRestaurant)
        rl_selectRestaurantParty = view!!.findViewById(R.id.rl_selectRestaurantParty)
        tv_party = view!!.findViewById(R.id.tv_party)
        tv_book = view!!.findViewById(R.id.tv_book)

        rl_selectRestaurant.setOnClickListener {
            if (::allRestaurantResponse.isInitialized) {
                dialogRestaurant(1, allRestaurantResponse)
            }
        }

        rl_selectRestaurantParty.setOnClickListener {
            if (::allRestaurantResponse.isInitialized) {
                openFullScreenDialogForAllCoupon(2, allRestaurantResponse)
            }
        }

        tv_book.setOnClickListener {
            if (tv_selectRestaurant.text.isEmpty()|| tv_selectRestaurant.text.equals(resources.getString(R.string.select_restaurent))) {
                Toast.makeText(activity, R.string.Please_select_restaurant, Toast.LENGTH_LONG).show()

            } else if (tv_openDatePicker.text.isEmpty()|| tv_openDatePicker.text.equals(resources.getString(R.string.date))) {
                Toast.makeText(activity, R.string.Please_enter_date, Toast.LENGTH_LONG).show()
            } else if (tv_openTimePicker.text.isEmpty()|| tv_openTimePicker.text.equals(resources.getString(R.string.time))) {
                Toast.makeText(activity, R.string.Please_enter_time, Toast.LENGTH_LONG).show()
            }else if (tv_party.text.isEmpty()|| tv_party.text.equals(resources.getString(R.string.number_of_persons))) {
                Toast.makeText(activity, R.string.Please_enter_the_email_the_number, Toast.LENGTH_LONG).show()
            } else if (tv_name.text.isEmpty()) {
                Toast.makeText(activity, R.string.Please_enter_the_name, Toast.LENGTH_LONG).show()
            } else if (tv_email.text.isEmpty()) {
                Toast.makeText(activity, R.string.Please_enter_the_email, Toast.LENGTH_LONG).show()

            }  else if (tv_phone.text.isEmpty()) {
                Toast.makeText(activity, R.string.Please_enter_phone, Toast.LENGTH_LONG).show()
            } else if(tv_phone.text.length < 9){
                Toast.makeText(activity, R.string.Please_enter_valid_phone, Toast.LENGTH_LONG).show()
            }
            else {
                val matcher = Pattern.compile(RegexPatterns.EMAIL).matcher(tv_email.text)
                if (!matcher.matches()) {
                    Toast.makeText(activity, R.string.Please_enter_the_email_valid_email, Toast.LENGTH_LONG).show()
                }

                else {
                    DelhiDarbar.instance!!.commonMethods!!.hideKeyboard(activity)
                    if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                        submitBookAnTableApi()
                    } else {
                        Dialogs.showNetworkErrorDialog(context)
                    }
                }
            }
        }

        pb_progressBar = view!!.findViewById(R.id.pb_progressBar)
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
            getAllRestaurant()
        } else {
            Dialogs.showNetworkErrorDialog(context)
        }
    }

    private fun submitBookAnTableApi() {
        pb_progressBar.visibility = View.VISIBLE
        DelhiDarbar.instance!!.commonMethods.disableScreenInteraction(activity)
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)
                ?.bookAnTable(DelhiDarbar.instance!!.mySharedPrefrence!!.getString(ConstantKeys.LoginUserIdKey)!!, restaurant_id, party_id, date, time, tv_name.text.toString().trim(),
                        tv_phone.text.toString().trim(), tv_email.text.toString().trim(), tv_desc.text.toString().trim(), DelhiDarbar.instance!!.mySharedPrefrence.getString("language")!!)
                ?.enqueue(object : Callback<SelectRestaurantResponse> {
                    override fun onFailure(call: Call<SelectRestaurantResponse>, t: Throwable) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity)
                    }

                    override fun onResponse(call: Call<SelectRestaurantResponse>, response: Response<SelectRestaurantResponse>) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity)
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error == false) {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(activity,
                                    response.body()!!.message, DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            resetFields()
                                          // replaceFragment()
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
                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(activity,
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

    private fun resetFields() {
        tv_selectRestaurant.text = ""
        tv_selectRestaurant.hint = "${resources.getString(R.string.select_restaurent)}"
        tv_openTimePicker.text = ""
        tv_openTimePicker.hint = "${resources.getString(R.string.time)}"
        tv_openDatePicker.text = ""
        tv_openDatePicker.hint = "${resources.getString(R.string.date)}"
        tv_party.text = ""
        tv_party.hint = "${resources.getString(R.string.party)}"

        tv_name.text.clear()
        tv_email.text.clear()
        tv_desc.text.clear()
        tv_phone.text.clear()
    }

    lateinit var allRestaurantResponse: AllRestaurantResponse
    private fun getAllRestaurant() {
        pb_progressBar.visibility = View.VISIBLE
        DelhiDarbar.instance!!.commonMethods.disableScreenInteraction(activity)
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)
                ?.allParties()
                ?.enqueue(object : Callback<AllRestaurantResponse> {
                    override fun onFailure(call: Call<AllRestaurantResponse>, t: Throwable) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity)
                    }

                    override fun onResponse(call: Call<AllRestaurantResponse>, response: Response<AllRestaurantResponse>) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance!!.commonMethods.enableScreenInteraction(activity)
                        if (!response.isSuccessful)
                            return
                        if (response.body()!!.error == false) {
                            allRestaurantResponse = response.body()!!

                            //   replcaeFragment()
                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(activity,
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

    private fun openDatePicker() {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_date_picker)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }
        val dp_PickupDate by lazy { dialog.findViewById<DatePicker>(R.id.dp_PickupDate) as DatePicker }
        dp_PickupDate.minDate = System.currentTimeMillis() - 1000

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_next).setOnClickListener {
            tv_openDatePicker.text = "${dp_PickupDate.year}-${dp_PickupDate.month + 1}-${dp_PickupDate.dayOfMonth}"
            date = "${dp_PickupDate.year}-${dp_PickupDate.month + 1}-${dp_PickupDate.dayOfMonth}"
            dialog.dismiss()
        }

    }

    private fun setTimePickerInterval(timePicker: TimePicker) {
        try {
            val minutePicker = timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android")) as NumberPicker
            minutePicker.minValue = 0
            minutePicker.maxValue = 60 / TIME_PICKER_INTERVAL - 1
            val displayedValues = ArrayList<String>()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            minutePicker.displayedValues = displayedValues.toTypedArray()
        } catch (e: Exception) {
            Log.e("TAG", "Exception: $e")
        }

    }

    fun getTime(){
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR, hour)
            cal.set(Calendar.MINUTE, minute)

            if(tv_selectRestaurant.text.toString().trim()!!.contains("Costa")){
                if (hour >= 13 && hour <=23) {
                    tv_openTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)
                }else{
                    Toast.makeText(activity, resources.getString(R.string.res_closed), Toast.LENGTH_SHORT).show()
                }

            }else{
                if (hour >= 12  && hour <=22) {
                    if(hour == 12 && minute >= 30)
                        tv_openTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)
                    else if(hour == 22 && minute <=30)
                        tv_openTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)
                     else if(hour >= 13 && hour <=21)
                        tv_openTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)
                    else
                        Toast.makeText(activity, resources.getString(R.string.res_closed), Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(activity, resources.getString(R.string.res_closed), Toast.LENGTH_SHORT).show()
                }
            }

            time = "${hour}:${minute}"

        }

        rl_selectRestaurantime.setOnClickListener {
          //  CustomTimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            if(tv_selectRestaurant.text.toString().trim()!!.contains("Costa"))
                CustomTimePickerDialog(context, timeSetListener, 13, 0, true).show()
            else
                CustomTimePickerDialog(context, timeSetListener, 12, 30, true).show()
        }

    }

    private fun openTimePicker() {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_time_picker)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)

        var dp_PickupTime = dialog.findViewById(R.id.dp_PickupTime) as TimePicker

        dp_PickupTime.setIs24HourView(true)

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btn_next).setOnClickListener {
            var hour = dp_PickupTime.currentHour
            var min = dp_PickupTime.currentMinute
//            showTime(hour, min)
            tv_openTimePicker.text = "${hour}:${min}"
            time = "${hour}:${min}"
            dialog.dismiss()
        }

    }

    lateinit var minutePicker:NumberPicker
    var displayedValues: ArrayList<String>? = null
    private val TIME_PICKER_INTERVAL = 15

    var format = ""
    fun showTime(hour: Int, min: Int) {
        var hour = hour
        if (hour == 0) {
            hour += 12
            format = "AM"
        } else if (hour == 12) {
            format = "PM"
        } else if (hour > 12) {
            hour -= 12
            format = "PM"
        } else {
            format = "AM"
        }
        tv_openTimePicker.text = "${hour}:${min} ${format}"
        /* time.setText(StringBuilder().append(hour).append(" : ").append(min)
                 .append(" ").append(format))*/
    }

    var restaurant_id = ""
    var party_id = ""
    var date = ""
    var time = ""

    private fun dialogRestaurant(fromWhere: Int, allRestaurantResponse: AllRestaurantResponse) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.select_rest1)
        dialog.setCancelable(true)
       // dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val rv_haveAnCoupon by lazy { dialog.findViewById<RecyclerView>(R.id.rv_restaurent_list) as RecyclerView }

        val valAdapterBookATable = AdapterSelectRest1(this.activity!!, allRestaurantResponse.restaurants)
        rv_haveAnCoupon.adapter = valAdapterBookATable
        valAdapterBookATable.performItemClick(object : ClickWithText {
            override fun itemClick(postion: Int, name: String) {
                tv_selectRestaurant.text = allRestaurantResponse.restaurants!![postion]!!.name
                restaurant_id = allRestaurantResponse.restaurants!![postion]!!.id.toString()
                dialog.dismiss()
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

    private fun openFullScreenDialogForAllCoupon(fromWhere: Int, allRestaurantResponse: AllRestaurantResponse) {
        val dialog = Dialog(activity, R.style.full_screen_dialog)
        dialog.setContentView(R.layout.dialog_have_a_coupon)
        dialog.setCancelable(false)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val rv_haveAnCoupon by lazy { dialog.findViewById<RecyclerView>(R.id.rv_haveAnCoupon) as RecyclerView }
        val iv_BackButtonToolbar by lazy { dialog.findViewById<ImageView>(R.id.iv_BackButtonToolbar) as ImageView }
        iv_BackButtonToolbar.setOnClickListener { dialog.dismiss() }
        val tv_myToolbarTxt by lazy { dialog.findViewById<TextView>(R.id.tv_myToolbarTxt) as TextView }

        when (fromWhere) {
            1 -> {
                tv_myToolbarTxt.text = resources.getString(R.string.select_restaurent)
                val valAdapterBookATable = AdapterBookATable(this.activity!!, 1, allRestaurantResponse.restaurants, emptyList())
                rv_haveAnCoupon.adapter = valAdapterBookATable
                valAdapterBookATable.performItemClick(object : ClickWithText {
                    override fun itemClick(postion: Int, name: String) {
                        tv_selectRestaurant.text = allRestaurantResponse.restaurants!![postion]!!.name
                        restaurant_id = allRestaurantResponse.restaurants!![postion]!!.id.toString()
                        dialog.dismiss()
                    }
                })
            }
            2 -> {
                tv_myToolbarTxt.text = resources.getString(R.string.party)
                val valAdapterBookATable = AdapterBookATable(this.activity!!, 2, emptyList(), allRestaurantResponse.parties)
                rv_haveAnCoupon.adapter = valAdapterBookATable

                valAdapterBookATable.performItemClick(object : ClickWithText {
                    override fun itemClick(postion: Int, name: String) {
                        tv_party.text = allRestaurantResponse.parties!![postion].toString()
                        party_id = allRestaurantResponse.parties!![postion].toString()
                      /*tv_party.text = allRestaurantResponse.parties!![postion]!!.name
                        party_id = allRestaurantResponse.parties!![postion]!!.id.toString()*/
                        dialog.dismiss()
                    }
                })
            }
        }
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


    private fun replcaeFragment() {
        val manager = fragmentManager
        val transaction = manager!!.beginTransaction()
        transaction.replace(R.id.fragment_contianor, HomeFragment())
        transaction.addToBackStack("homefragment")
        transaction.commit()
    }

}
