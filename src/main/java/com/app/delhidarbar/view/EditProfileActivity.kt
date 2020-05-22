package com.app.delhidarbar.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.app.delhidarbar.BuildConfig
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.helper.Dialogs
import com.app.delhidarbar.helper.getContext
import com.app.delhidarbar.model.profile_model.ProfileResponeModel
import com.app.delhidarbar.model.update_user.UpdateUser
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.iv_cropper.CropImage
import com.app.delhidarbar.utils.iv_cropper.CropImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EditProfileActivity : AppCompatActivity() {
    var p_img:Boolean = true
    private val CHOOSE_CAMERA = 3
    private val CHOOSE_GALLERY = 2
    private lateinit var imageFile: File
    private lateinit var bgimageFile: File
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 2501
    var profileResponeModel: ProfileResponeModel? = null
    val tv_done: TextView by lazy { findViewById<TextView>(R.id.tv_done) }
    val img_user_image: ImageView by lazy { findViewById<ImageView>(R.id.img_user_image) }
    val bg_imageView: ImageView by lazy { findViewById<ImageView>(R.id.bg_imageView) }
    val img_camera: ImageView by lazy { findViewById<ImageView>(R.id.img_camera) }
    val img_bg_camera: ImageView by lazy { findViewById<ImageView>(R.id.img_bg_camera) }
    val edt_userNameFnln: EditText by lazy { findViewById<EditText>(R.id.edt_userNameFnln) }
    val edt_email: EditText by lazy { findViewById<EditText>(R.id.edt_email) }
    val edt_phoneNumber: EditText by lazy { findViewById<EditText>(R.id.edt_phoneNumber) }
    val edt_address: EditText by lazy { findViewById<EditText>(R.id.edt_address) }
    val tv_changeLanguage: TextView by lazy { findViewById<TextView>(R.id.tv_changeLanguage) }
    val pb_apiProgressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.pb_apiProgressBar) }
    val pb_progressBarEdtiProfile: ProgressBar by lazy { findViewById<ProgressBar>(R.id.pb_progressBarEdtiProfile) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        DelhiDarbar.instance?.hideStatusBar(this)

        img_back.setOnClickListener { finish() }

        initViews()
    }

    private fun initViews() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            if (bundle.getParcelable<ProfileResponeModel>("profileResponeModel") != null) {
                profileResponeModel = bundle.getParcelable("profileResponeModel")

                if (profileResponeModel!!.user!!.name != null) {
                    edt_userNameFnln.setText(profileResponeModel!!.user!!.name)
                }
                if (!profileResponeModel!!.user!!.email.equals("")) {
                    edt_email.setText(profileResponeModel!!.user!!.email)
                }else
                    edt_email.setText(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.email))


                if (profileResponeModel!!.user!!.phone != null) {
                    edt_phoneNumber.setText(profileResponeModel!!.user!!.phone)
                }
                if (profileResponeModel!!.user!!.location_address != null) {
                    edt_address.setText(profileResponeModel!!.user!!.location_address)
                }

                if (profileResponeModel!!.user!!.image != null) {
                    pb_progressBarEdtiProfile.visibility = View.VISIBLE
                    Picasso.get()
                            .load(profileResponeModel!!.user!!.image)
                            .into(img_user_image, object : Callback {
                                override fun onSuccess() {
                                    pb_progressBarEdtiProfile.visibility = View.GONE
                                }

                                override fun onError(e: Exception?) {
                                    pb_progressBarEdtiProfile.visibility = View.GONE
                                }
                            })
                } else {
                    pb_progressBarEdtiProfile.visibility = View.GONE
                    img_user_image.setImageResource(R.drawable.no_image)
                }

                if (profileResponeModel!!.user!!.background_image != null) {
                    Picasso.get().load(profileResponeModel!!.user!!.background_image).into(bg_imageView)
                } else {
                    pb_progressBarEdtiProfile.visibility = View.GONE
                    bg_imageView.setImageResource(R.drawable.profile_bg)
                }

                //Todo api hiting
                tv_done.setOnClickListener {
                    if (DelhiDarbar!!.instance!!.commonMethods!!.checkInternetConnect(this@EditProfileActivity)) {
                        if (edt_userNameFnln.text.isEmpty()) {
                            edt_userNameFnln!!.error = getString(R.string.Please_enter_the_name)

                        } else if (edt_email.text.isEmpty()) {
                            edt_email!!.error = getString(R.string.Please_enter_the_email)

                        } else if (!Patterns.EMAIL_ADDRESS.matcher(edt_email.text.toString()).matches()) {
                            edt_email!!.error = getString(R.string.Please_enter_the_email_valid_email)

                        } else if (edt_phoneNumber.text.isEmpty()) {
                            edt_phoneNumber!!.error = getString(R.string.Please_enter_the_email_the_number)

                        } else if(edt_phoneNumber.text.length < 9){
                            edt_phoneNumber!!.error = getString(R.string.Please_enter_valid_phone)

                        }else if (edt_address.text.isEmpty()) {
                            edt_address!!.error = getString(R.string.Please_enter_the_password)
                        } else {
                            DelhiDarbar.instance!!.commonMethods!!.hideKeyboard(this@EditProfileActivity)
                            editProfileApi(profileResponeModel!!)
                        }

                    } else {
                        Dialogs.showNetworkErrorDialog(getContext())
                    }
                }
                img_camera.setOnClickListener {
                    if (DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                                    REQUEST_ID_MULTIPLE_PERMISSIONS, this@EditProfileActivity)) {
                        p_img = true
                        initOptionsMenu(this@EditProfileActivity, p_img)
                    }
                }

                img_bg_camera.setOnClickListener {
                    if (DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                                    REQUEST_ID_MULTIPLE_PERMISSIONS, this@EditProfileActivity)) {
                        p_img = false
                        initOptionsMenu(this@EditProfileActivity, p_img)
                    }
                }

                tv_changeLanguage.setOnClickListener{
                    startActivity(Intent(this, ChangeLanguageActivity::class.java))
                }
            }
        }
    }

    private fun editProfileApi(profileResponeModel: ProfileResponeModel) {
        pb_apiProgressBar.visibility = View.VISIBLE
        DelhiDarbar!!.instance!!.commonMethods!!.disableScreenInteraction(this@EditProfileActivity)
        val map = java.util.HashMap<String, RequestBody>()
        if (::imageFile.isInitialized) {
            val convert_File_2String = imageFile.toString()
            val filename = convert_File_2String.substring(convert_File_2String.lastIndexOf("/") + 1)
            map.put("image\"; filename=\"" + filename + "\" ", RequestBody.create(MediaType.parse("image/*"), imageFile))
        }

        if (::bgimageFile.isInitialized) {
            val convert_File_2String = bgimageFile.toString()
            val filename = convert_File_2String.substring(convert_File_2String.lastIndexOf("/") + 1)
            map.put("background_image\"; filename=\"" + filename + "\" ", RequestBody.create(MediaType.parse("image/*"), bgimageFile))
        }

        map.put(ConstantKeys.language, RequestBody.create(MediaType.parse("text/plain"), DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language)!!))
        map.put(ConstantKeys.name, RequestBody.create(MediaType.parse("text/plain"), edt_userNameFnln.text.toString()))
        map.put(ConstantKeys.email, RequestBody.create(MediaType.parse("text/plain"), edt_email.text.toString()))
        map.put(ConstantKeys.address, RequestBody.create(MediaType.parse("text/plain"), edt_address.text.toString()))
        map.put(ConstantKeys.phone, RequestBody.create(MediaType.parse("text/plain"), edt_phoneNumber.text.toString()))
        RetrofitUtils.getRetrofitUtils()?.create(Api::class.java)?.updateUser(
                map, DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.LoginUserIdKey)!!)?.enqueue(object : retrofit2.Callback<UpdateUser> {
            override fun onFailure(call: Call<UpdateUser>, t: Throwable) {
                DelhiDarbar!!.instance!!.commonMethods!!.enableScreenInteraction(this@EditProfileActivity)
                pb_apiProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<UpdateUser>, response: Response<UpdateUser>) {
                DelhiDarbar!!.instance!!.commonMethods!!.enableScreenInteraction(this@EditProfileActivity)
                pb_apiProgressBar.visibility = View.GONE
                Log.e("Edit profiel activity ", "onSuuces" + response.body())
                if (!response.isSuccessful)
                    return
                if (response.body()!!.error == false) {
                    if (!response.body()!!.user!!.name!!.equals("")) {
                        profileResponeModel.user!!.name = response.body()!!.user!!.name!!
                    }
                    if (!response.body()!!.user!!.phone!!.equals("")) {
                        profileResponeModel.user!!.phone = response.body()!!.user!!.phone!!
                    }

                    DelhiDarbar.instance!!.mySharedPrefrence.putString("userPhone", response.body()!!.user!!.phone!!)

                    isFromUpdateDatabase = true
                    updateprofile = true
                    finish()

                } else {
                    DelhiDarbar.instance!!.commonMethods.showDialogOK(this@EditProfileActivity,
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


    companion object {
        var isFromUpdateDatabase = false
        var updateprofile = false
        val TAG = EditProfileActivity::class.java.simpleName
    }

    fun initOptionsMenu(context: Activity, p_image: Boolean) {
        val layoutInflater: LayoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.custom_popup, null)
        val popupWindow = PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        val pickerContainerV: View = popupView.findViewById(R.id.container_picker)
        val camtxt: TextView = popupView.findViewById(R.id.camtxt)
        val galltxt: TextView = popupView.findViewById(R.id.galltxt)
        val tv_cancel: TextView = popupView.findViewById(R.id.tv_cancel)
        camtxt.text = resources.getString(R.string.Take_Photo_Camera)
        galltxt.text = resources.getString(R.string.Take_Photo_Gallery)
        tv_cancel.text = resources.getString(R.string.Cancel)
        camtxt.setOnClickListener {
        popupWindow.dismiss()
            if(p_image)
                captureImage()
            else
                captureImageBg()
        }
        galltxt.setOnClickListener {
            popupWindow.dismiss()
            chooseGallery()
        }
        tv_cancel.setOnClickListener {
            popupWindow.dismiss()
        }

        showPopUp(context, pickerContainerV, popupWindow);
    }

    fun showPopUp(activity: Activity, picker: View, popupWindow: PopupWindow) {
        val trans = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f)
        popupWindow.showAtLocation(activity.getWindow().getDecorView()
                , Gravity.BOTTOM, 0, 0);
        trans.setDuration(400)
        trans.setInterpolator(AccelerateDecelerateInterpolator());
        picker.startAnimation(trans)
    }

    fun chooseGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, CHOOSE_GALLERY)
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        var storageDir = Environment.getExternalStorageDirectory().toString() + "/picupload"
        if (Build.VERSION.SDK_INT >= 23) {
            storageDir = context.filesDir.absolutePath + "/picupload"
        }
        val dir = File(storageDir)
        if (!dir.exists())
            dir.mkdir()

        return File("$storageDir/$imageFileName.jpg")
    }

    fun captureImageBg() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    bgimageFile = createImageFile(this@EditProfileActivity)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (::bgimageFile.isInitialized && bgimageFile != null) {
                    val uri: Uri
                    if (Build.VERSION.SDK_INT >= 23) {
                        val info = this.packageManager.resolveContentProvider(this.packageName + ".photos", PackageManager.GET_META_DATA)
                        val `in` = info.loadXmlMetaData(this.packageManager, this.packageName + ".photos")

                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(bgimageFile), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(bgimageFile), Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(bgimageFile), Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        uri = FileProvider.getUriForFile(this, this.packageName + ".photos", bgimageFile!!)
                    } else {
                        uri = Uri.fromFile(bgimageFile)
                    }

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(cameraIntent, CHOOSE_CAMERA)
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun captureImage() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    imageFile = createImageFile(this@EditProfileActivity)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (::imageFile.isInitialized && imageFile != null) {
                    val uri: Uri
                    if (Build.VERSION.SDK_INT >= 23) {
                        val info = this.getPackageManager().resolveContentProvider(this.getPackageName() + ".photos", PackageManager.GET_META_DATA)
                        val `in` = info.loadXmlMetaData(this.getPackageManager(), this.getPackageName() + ".photos")

                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        uri = FileProvider.getUriForFile(this, this.getPackageName() + ".photos", imageFile)
                    } else {
                        uri = Uri.fromFile(imageFile)
                    }
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(cameraIntent, CHOOSE_CAMERA)
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_CAMERA && resultCode == Activity.RESULT_OK) {
            var uri:Uri
            if(p_img)
                uri = Uri.fromFile(imageFile)
            else{
                uri = Uri.fromFile(bgimageFile)
            }

            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)

        } else if (requestCode == CHOOSE_GALLERY && resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val imgDecodableString = cursor.getString(columnIndex)
            cursor.close()
            val imageFile = File("" + imgDecodableString)
            if(p_img)
                this.imageFile = imageFile
            else
                this.bgimageFile = imageFile

            val uri = Uri.fromFile(imageFile)
            Log.i("TAG", "path: " + imageFile.absolutePath)

           // CropImage.activity(uri).setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    if(p_img)
                        imageFile = File(resultUri.path)
                    else
                        bgimageFile = File(resultUri.path)

                    val bitmap: Bitmap
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, resultUri)

                        val tempUri = getImageUri(this@EditProfileActivity, bitmap)
                        // pimage = new File(getRealPathFromURI(tempUri));


                        if(p_img)
                            img_user_image!!.setImageBitmap(bitmap)
                        else
                            bg_imageView!!.setImageBitmap(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    var isFromSetting = false
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.e(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms: HashMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED)
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in 0 until permissions.size)
                        perms.put(permissions[i], grantResults[i])
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "sms & location services permission granted")
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        initOptionsMenu(this@EditProfileActivity, p_img)
                    } else {
                        Log.e(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            DelhiDarbar.instance!!.commonMethods.showDialogOKWidMsgTitle(
                                    this@EditProfileActivity, resources.getString(R.string.access_full_feature_of_app), DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            p1.dismiss()
                                            DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                                                    REQUEST_ID_MULTIPLE_PERMISSIONS, this@EditProfileActivity)
                                        }
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                            p1.dismiss()
                                        }
                                    }
                                }

                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                }
                            }), resources.getString(R.string.permission_required))
                        } else {
                            DelhiDarbar.instance!!.commonMethods.showDialogOKWidMsgTitle(
                                    this@EditProfileActivity, resources.getString(R.string.access_full_feature_of_app), DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            p1.dismiss()
                                            isFromSetting = true
                                            startActivity(
                                                    Intent(
                                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
                                        }
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                            p1.dismiss()
                                        }
                                    }
                                }

                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                }
                            }), resources.getString(R.string.permission_required))

                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),this)

        if (isFromSetting && DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(REQUEST_ID_MULTIPLE_PERMISSIONS, this@EditProfileActivity) == false) {
            isFromSetting = false
        }
    }
}