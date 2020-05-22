package com.app.delhidarbar.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.model.my_static_view_cart1.PrepareCartResponse
import com.app.delhidarbar.model.profile_model.ProfileResponeModel
import com.app.delhidarbar.model.select_restaurent_model.SelectRestaurantResponse
import com.app.delhidarbar.model.update_user.UpdateUser
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ClickWithText
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.view.*
import com.app.delhidarbar.view.adapter.AdapterForReviews
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_all_review.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Double

class ProfileFragment : Fragment() {
    lateinit var tv_orders: TextView
    lateinit var tv_reviews: TextView
    lateinit var tv_address: TextView
    lateinit var rv_all_review: RecyclerView
    lateinit var pb_progressBar: ProgressBar
    lateinit var pb_userImageLoading: ProgressBar
    var profileResponeModel: ProfileResponeModel? = null
    var type: String = ""
    lateinit var tv_changeLanguage: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_all_review, container, false)

        pb_progressBar = view.findViewById(R.id.pb_progressBar)

        return view
    }

    private fun initViews(view: View) {
        tv_reviews = view.findViewById(R.id.tv_reviews)
        rv_all_review = view.findViewById(R.id.rv_all_review)
        tv_orders = view.findViewById(R.id.tv_orders)
        tv_address = view.findViewById(R.id.tv_address)
        pb_userImageLoading = view.findViewById(R.id.pb_userImageLoading)
        pb_progressBar = view.findViewById(R.id.pb_progressBar)
        tv_changeLanguage = view.findViewById(R.id.tv_changeLanguage)

        tv_changeLanguage.setOnClickListener{
            startActivity(Intent(activity, ChangeLanguageActivity::class.java))
        }

        tv_editProfile.setOnClickListener {
            if (profileResponeModel != null) {
                val bundle = Bundle()
                bundle.putParcelable("profileResponeModel", profileResponeModel)
                startActivity(Intent(activity, EditProfileActivity::class.java).putExtras(bundle))
            }
        }

        tv_logout.setOnClickListener {
            DelhiDarbar.instance!!.mySharedPrefrence.putString("LoginUserId", "")
            DelhiDarbar.instance!!.mySharedPrefrence.putString("userName", "")
            DelhiDarbar.instance!!.mySharedPrefrence.putString("email", "")
            DelhiDarbar.instance!!.mySharedPrefrence.putString("userPhone", "")


            startActivity(Intent(activity, BaseActivity::class.java))
            activity!!.finishAffinity()
        }
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
//            if (profileResponeModel != null)
//                return
            getProfileDataApi(true, "")
        } else {
            Dialogs.showNetworkErrorDialog(context)
        }
    }

    fun getProfileDataApi(iWant2ShowProgress: Boolean, fromWhere: String) {
        if (iWant2ShowProgress) {
            pb_progressBar.visibility = View.VISIBLE
        }
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)?.getProfileDataApi(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : Callback<ProfileResponeModel> {
                    override fun onFailure(call: Call<ProfileResponeModel>, t: Throwable) {
                        if (iWant2ShowProgress) {
                            pb_progressBar.visibility = View.GONE
                        }

                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)

                        Dialogs.showInformationDialog(context, R.string.request_time_out_error)
                    }

                    override fun onResponse(call: Call<ProfileResponeModel>, response: Response<ProfileResponeModel>) {
                        if (iWant2ShowProgress) {
                            pb_progressBar.visibility = View.GONE
                        }
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                        if (!response.isSuccessful)
                            return
                        profileResponeModel = response.body()

                        Log.e("OnProfilResponse127", "aResponse" + profileResponeModel!!.user!!.image)

                        tv__userName.text = profileResponeModel!!.user!!.name
                        if (profileResponeModel!!.user!!.phone != null)
                            tv_phone_email.text = "${profileResponeModel!!.user!!.phone}, ${profileResponeModel!!.user!!.name}"
                        else
                            tv_phone_email.text = DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.email)


                        if (profileResponeModel!!.user!!.image != null && profileResponeModel!!.user!!.image!!.length > 0) {
                            pb_userImageLoading.visibility = View.VISIBLE
                            Picasso.get()
                                    .load(profileResponeModel!!.user!!.image)
                                    .resize(600, 600)
                                    .centerCrop()
                                    .placeholder(R.drawable.no_profile_image)
                                    .into(id_imageUser, object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            pb_userImageLoading.visibility = View.GONE
                                        }

                                        override fun onError(e: Exception?) {
                                            pb_userImageLoading.visibility = View.GONE
                                        }
                                    })
                        } else {
                            id_imageUser.setImageResource(R.drawable.no_profile_image)
                        }

                        tv_reviews.setOnClickListener {
                            val adapterForReviews = AdapterForReviews(activity!!, R.layout.child_item_review, profileResponeModel!!.reviews, emptyList(), emptyList())
                            rv_all_review.adapter = adapterForReviews
                            adapterForReviews.notifyDataSetChanged()

                            cv_cardView.visibility = View.GONE
                            tv_reviews.setBackgroundResource(R.drawable.button_less_round_shape_light_grey)
                            // tv_reviews.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.message_box_background))
                            tv_reviews.setTextColor(ContextCompat.getColor(activity!!, R.color.un_select_dashboard))

                            tv_address.setBackgroundResource(0)
                            tv_address.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
                            tv_orders.setBackgroundResource(0)
                            tv_orders.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
                        }

                        tv_address.setOnClickListener {
                            if (profileResponeModel!!.addresses.size >= 3) {
                                cv_cardView.visibility = View.GONE
                            } else
                                cv_cardView.visibility = View.VISIBLE

                            val adapterForReviews = AdapterForReviews(activity!!, R.layout.item_addresses, emptyList(), profileResponeModel!!.addresses, emptyList())
                            rv_all_review.adapter = adapterForReviews
                            adapterForReviews.notifyDataSetChanged()
                            adapterForReviews.performItemClick(object : ClickWithText {
                                override fun itemClick(postion: Int, name: String) {
                                    when (name) {
                                        "delete" -> {
                                            if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
                                                val dialogBuilder = AlertDialog.Builder(this@ProfileFragment.activity!!)
                                                dialogBuilder.setMessage(resources.getString(R.string.sure_to_delete_addres))
                                                        .setIcon(R.mipmap.delhi_darbar)
                                                        .setCancelable(false)
                                                        .setPositiveButton(resources.getString(R.string.ok), DialogInterface.OnClickListener { dialog, id ->
                                                            dialog.cancel()
                                                            deleteAddressApi(profileResponeModel!!.addresses[postion].id)
                                                        }).setNegativeButton(resources.getString(R.string.cancel), DialogInterface.OnClickListener { dialog, id ->
                                                            dialog.cancel()
                                                        })

                                                val alert = dialogBuilder.create()
                                                alert.setTitle(resources.getString(R.string.app_name))
                                                if (!alert.isShowing) {
                                                    alert.show()
                                                }
                                            } else {
                                                Dialogs.showNetworkErrorDialog(getContext())
                                            }
                                        }
                                        "edit" -> {
                                            DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                                                    activity, resources.getString(R.string.do_u_want_to_change_address)
                                                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                                override fun invoke(p1: DialogInterface, p2: Int) {
                                                    when (p2) {
                                                        DialogInterface.BUTTON_POSITIVE -> {
                                                            p1.dismiss()
                                                            updateAddressDialog(profileResponeModel!!.addresses[postion])
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
                            })

                            cv_cardView.setOnClickListener {
                                DelhiDarbar.instance!!.commonMethods.showDialogWithPoNE(
                                        activity,
                                        resources.getString(R.string.do_u_want_to_add_address)
                                        , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                    override fun invoke(p1: DialogInterface, p2: Int) {
                                        when (p2) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                Log.e("111", "ASDASD")
                                                p1.dismiss()
                                                addAddressDialog()
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

                            tv_address.setBackgroundResource(R.drawable.button_less_round_shape_light_grey)
                            //tv_address.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.message_box_background))
                            tv_address.setTextColor(ContextCompat.getColor(activity!!, R.color.un_select_dashboard))

                            tv_reviews.setBackgroundResource(0)
                            tv_reviews.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
                            tv_orders.setBackgroundResource(0)
                            tv_orders.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
                        }

                        tv_orders.text = "${response.body()!!.total_orders}\n${resources.getString(R.string.orders)}"
                        tv_reviews.text = "${response.body()!!.total_reviews}\n${resources.getString(R.string.reviews)}"
                        tv_address.text = "${response.body()!!.total_addresses}\n${resources.getString(R.string.addresses)}"
                        tv_orders.setOnClickListener {
                            val adapterForReviews = AdapterForReviews(activity!!, R.layout.item_orders, emptyList(), emptyList(), profileResponeModel!!.orders)
                            rv_all_review.adapter = adapterForReviews
                            adapterForReviews.notifyDataSetChanged()
                            adapterForReviews.performItemClick(object : ClickWithText {
                                override fun itemClick(postion: Int, name: String) {
                                    when (name) {
                                        "reorder" -> {
                                            //reOrderAPi(profileResponeModel!!.orders[postion].id!!)
                                            var testing = profileResponeModel!!.orders[postion].items

                                            startActivity(
                                                    Intent(activity, CartActivity::class.java)
                                                    .putExtra("order_id", profileResponeModel!!.orders[postion].id!!)
                                                    .putParcelableArrayListExtra("items", testing)
                                                    .putExtra("amount", profileResponeModel!!.orders[postion].total_amount!!.toString())
                                            /*
                                                     startActivity(Intent(activity, PaymentOptionActivity::class.java)
                                                    .putExtra("order_id",profileResponeModel!!.orders[postion].id!!)
                                                    .putParcelableArrayListExtra("items", testing)
                                                    .putExtra("amount",profileResponeModel!!.orders[postion].total_amount!!.toString())
                                            */
                                            )

                                        }

                                        "review" -> {
                                            reviewDialog(profileResponeModel!!.orders[postion].id!!)
                                        }
                                    }

                                }

                            })

                            cv_cardView.visibility = View.GONE
                            tv_orders.setBackgroundResource(R.drawable.button_less_round_shape_light_grey)
                            tv_orders.setTextColor(ContextCompat.getColor(activity!!, R.color.un_select_dashboard))

                            //tv_orders.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.message_box_background))

                            tv_address.setBackgroundResource(0)
                            tv_address.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
                            tv_reviews.setBackgroundResource(0)
                            tv_reviews.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))

                        }
                        if (fromWhere.equals("isFromUpdate")) {
                            tv_address.performClick()
                        } else if (fromWhere.equals("isFromDelete")) {
                            tv_address.performClick()
                        } else {
                            tv_orders.performClick()
                        }
                    }

                })

    }

    private fun reviewDialog(order_id: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_review)
        dialog.setCancelable(false)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        var btn_done: Button = dialog.findViewById(R.id.btn_done)
        var btn_cancel: Button = dialog.findViewById(R.id.btn_cancel)

        var ed_description: EditText = dialog.findViewById(R.id.edt_specialInstructions)

        var ratingBar: RatingBar = dialog.findViewById(R.id.ratingBar)

        btn_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        btn_done.setOnClickListener(View.OnClickListener {
            if (ratingBar.rating <= 0)
                Toast.makeText(context, "Please select rating", Toast.LENGTH_SHORT).show()
            else if (ed_description.text.toString().trim().equals(""))
                Toast.makeText(context, "Please write few words about order", Toast.LENGTH_SHORT).show()
            else
                addReview(dialog, ed_description.text.toString().trim(), ratingBar.rating.toString(), order_id)
        })

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, keyCode: Int, p2: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    p0!!.dismiss()
                }
                return true
            }
        })
    }

    private fun addReview(dialog: Dialog, description: String, rating: String, order_id: String) {
        pb_progressBar.visibility = View.VISIBLE
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)
                ?.addReview(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, order_id, rating, description)
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
                                    response.body()!!.message
                                    , DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            dialog.dismiss()
                                            p1.dismiss()
                                            getProfileDataApi(true, "")
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


                        }
                    }
                })

    }

    private fun deleteAddressApi(id: String?) {
        pb_progressBar.visibility = View.VISIBLE
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.deleteAddress(id.toString())?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                pb_progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                pb_progressBar.visibility = View.GONE
                if (!response.isSuccessful)
                    return
                getProfileDataApi(true, "isFromDelete")
            }
        })
    }


    private fun reOrderAPi(order_id: String) {
        pb_progressBar.visibility = View.VISIBLE
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)

        RetrofitUtils
                .getRetrofitUtils()!!
                .create(Api::class.java)
                .reOrder(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!, order_id)
                .enqueue(object : Callback<PrepareCartResponse> {

                    override fun onFailure(call: Call<PrepareCartResponse>, t: Throwable) {
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                        if (t is Timeout) {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(
                                    activity,
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
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                        if (response.isSuccessful) {
                            Log.e("191", "Login :" + response.body())
                            val bundle = Bundle()
                            bundle.putParcelable("PrepareCartResponse", response.body())
                            bundle.putString(ConstantKeys.QUANTITY, "")
                            startActivity(Intent(activity, CartActivity::class.java).putExtras(bundle))
                        } else {
                            return
                        }
                    }
                })
    }

    private fun updateAddressDialog(profileModel: com.app.delhidarbar.model.profile_model.Addresse) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_update_address)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }
        val office by lazy { dialog.findViewById<EditText>(R.id.office) as RadioButton }
        val home by lazy { dialog.findViewById<EditText>(R.id.home) as RadioButton }
        val edt_EnterLocation_edit_text by lazy { dialog.findViewById<EditText>(R.id.edt_EnterLocation_edit_text) as EditText }
        val tv_userCurrentLocation by lazy { dialog.findViewById<EditText>(R.id.tv_userCurrentLocation) as TextView }
        val pb_progressBar by lazy { dialog.findViewById<ProgressBar>(R.id.pb_progressBar) as ProgressBar }
        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }

        edt_EnterLocation_edit_text.setText(profileModel.address.toString())
        if (profileModel.name.equals("home", true)) {
            home.isChecked = true
        } else if (profileModel.name.equals("office", true))
            office.isChecked = true

        dialog.findViewById<Button>(R.id.btn_next).setOnClickListener {
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)),
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)))

            if (edt_EnterLocation_edit_text.text!!.toString().trim().length == 0 && tv_userCurrentLocation.text.toString().trim().length == 0) {
                tv_userCurrentLocation.error = getString(R.string.Please_enter_location)
                edt_EnterLocation_edit_text!!.error = getString(R.string.Please_enter_location)

            } else if (!office.isChecked && !home.isChecked) {
                Toast.makeText(activity, resources.getString(R.string.select_address_type), Toast.LENGTH_LONG).show()
            } else {
                if (office.isChecked)
                    type = "Office"
                else
                    type = "Home"

                if (edt_EnterLocation_edit_text!!.text!!.toString().trim().length > 0) {
                    updateAddress(profileModel.id!!, type, edt_EnterLocation_edit_text!!.text!!.toString(), dialog)

                } else if (tv_userCurrentLocation!!.text.toString().trim().length > 0) {
                    updateAddress(profileModel.id!!, type, tv_userCurrentLocation!!.text!!.toString(), dialog)
                }
            }
        }
        tv_userCurrentLocation.setOnClickListener {
            Log.i("tvEnterLocation", "onViewClicked: Clicked")
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)),
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)))
            Log.e(LocationFragment.TAG, "onTvClick: 93->$str_currrentAddress")
            tv_userCurrentLocation.text = str_currrentAddress
        }
    }

    private fun addAddressDialog() {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_update_address)
        dialog.setCancelable(false)
        if (!dialog.isShowing) {
            dialog.show()
        }
        val office by lazy { dialog.findViewById<EditText>(R.id.office) as RadioButton }
        val home by lazy { dialog.findViewById<EditText>(R.id.home) as RadioButton }
        val edt_EnterLocation_edit_text by lazy { dialog.findViewById<EditText>(R.id.edt_EnterLocation_edit_text) as EditText }
        val tv_userCurrentLocation by lazy { dialog.findViewById<EditText>(R.id.tv_userCurrentLocation) as TextView }
        val pb_progressBar by lazy { dialog.findViewById<ProgressBar>(R.id.pb_progressBar) as ProgressBar }
        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btn_next).setOnClickListener {
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)),
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)))

            if (edt_EnterLocation_edit_text.text!!.toString().trim().length == 0 && tv_userCurrentLocation.text.toString().trim().length == 0) {
                tv_userCurrentLocation.error = getString(R.string.Please_enter_location)
                edt_EnterLocation_edit_text!!.error = getString(R.string.Please_enter_location)

            } else if (!office.isChecked && !home.isChecked) {
                Toast.makeText(activity, resources.getString(R.string.select_address_type), Toast.LENGTH_LONG).show()
            } else {
                if (office.isChecked)
                    type = "Office"
                else
                    type = "Home"

                if (tv_userCurrentLocation!!.text.toString().trim().length > 0) {
                    addAddressApi(dialog, tv_userCurrentLocation!!.text!!.toString(), type)
                } else if (edt_EnterLocation_edit_text!!.text!!.toString().trim().length > 0) {
                    addAddressApi(dialog, edt_EnterLocation_edit_text!!.text!!.toString(), type)
                }
            }
        }

        tv_userCurrentLocation.setOnClickListener {
            Log.i("tvEnterLocation", "onViewClicked: Clicked")
            val str_currrentAddress = DelhiDarbar.instance?.commonMethods?.getCompleteAddressString(
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LATITUDE)),
                    Double.parseDouble(DelhiDarbar.instance?.mySharedPrefrence?.getString(ConstantKeys.LONGITUDE)))
            Log.e(LocationFragment.TAG, "onTvClick: 93->$str_currrentAddress")
            tv_userCurrentLocation.text = str_currrentAddress
        }
    }

    fun addAddressApi(dialog: Dialog, name: String, type: String) {
        if (DelhiDarbar.instance!!.commonMethods.checkInternetConnect(activity)) {
            pb_progressBar.visibility = View.VISIBLE
            DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)
            RetrofitUtils
                    .getRetrofitUtils()!!
                    .create(Api::class.java)
                    .getAddAdressApi(
                            DelhiDarbar.instance!!.mySharedPrefrence!!.getString(ConstantKeys.LoginUserIdKey)!!,
                            type,
                            name).enqueue(object : Callback<ProfileResponeModel> {
                        override fun onFailure(call: Call<ProfileResponeModel>, t: Throwable) {
                            dialog.dismiss()
                            pb_progressBar.visibility = View.GONE
                            DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                            DelhiDarbar.instance?.commonMethods?.showDialogOK(
                                    activity,
                                    t.localizedMessage,
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

                        override fun onResponse(call: Call<ProfileResponeModel>, response: Response<ProfileResponeModel>) {
                            dialog.dismiss()
                            pb_progressBar.visibility = View.GONE
                            DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                            if (!response.isSuccessful)
                                return
                            if (response!!.body()!!.error != false) {
                                DelhiDarbar.instance?.commonMethods?.showDialogOK(
                                        activity,
                                        response!!.body()!!.message,
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
                            } else {
                                getProfileDataApi(false, "isFromUpdate")
                            }

                        }

                    })
        } else {
            Dialogs.showNetworkErrorDialog(context)
        }
    }

    fun updateAddress(address_id: String, name: String, address: String, dialog: Dialog) {
        pb_progressBar.visibility = View.VISIBLE
        DelhiDarbar.instance?.commonMethods?.disableScreenInteraction(activity)
        RetrofitUtils
                .getRetrofitUtils()
                ?.create(Api::class.java)?.updateAddress(
                        DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!
                        , address_id, name, address)
                ?.enqueue(object : Callback<ProfileResponeModel> {
                    override fun onFailure(call: Call<ProfileResponeModel>, t: Throwable) {
                        dialog.dismiss()
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                    }

                    override fun onResponse(call: Call<ProfileResponeModel>, response: Response<ProfileResponeModel>) {
                        dialog.dismiss()
                        pb_progressBar.visibility = View.GONE
                        DelhiDarbar.instance?.commonMethods?.enableScreenInteraction(activity)
                        if (!response.isSuccessful)
                            return
                        getProfileDataApi(false, "isFromUpdate")

                    }

                })

    }

    private var _hasLoadedOnce = false; // your boolean field
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
        initViews(this.view!!)
    }

    override fun onResume() {
        super.onResume()
        if (EditProfileActivity.isFromUpdateDatabase == true) {
            getProfileDataApi(true, "")
            DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),activity)
            EditProfileActivity.isFromUpdateDatabase = false

            tv_editProfile.text = resources.getString(R.string.bt_edit)
            tv_logout.text = resources.getString(R.string.sign_out)
            Dashboard.tv_bookATable1.text = resources.getString(R.string.book_a_table)
            Log.e("tyyfvygb", DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language))

        }else{
            DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),activity)

            tv_editProfile.text = resources.getString(R.string.bt_edit)
            tv_logout.text = resources.getString(R.string.sign_out)
            Dashboard.tv_bookATable1.text = resources.getString(R.string.book_a_table)
            Log.e("tyyfvygb", DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // refresh your views here
        super.onConfigurationChanged(newConfig)
        tv_editProfile.text = resources.getString(R.string.bt_edit)
        tv_logout.text = resources.getString(R.string.sign_out)
    }

}
