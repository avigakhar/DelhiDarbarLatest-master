package com.app.delhidarbar.view

import android.Manifest
import android.annotation.TargetApi
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
import android.os.SystemClock
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.app.delhidarbar.BuildConfig
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.register.ParentRegisterResponse
import com.app.delhidarbar.retrofit.Api
import com.app.delhidarbar.retrofit.RetrofitUtils
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.iv_cropper.CropImage
import com.app.delhidarbar.utils.iv_cropper.CropImageView
import com.app.delhidarbar.view.fragments.LoginFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val mMediaUri: Uri? = null
    private var fileUri: Uri? = null
    private var mediaPath: String? = null
    private var mImageFileLocation = ""
    private var postPath: String? = null
    private var snackbar: Snackbar? = null
    var mLastClickTime: Long = 0;

    private val fragmentManager: FragmentManager? = null
    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        DelhiDarbar.instance?.hideStatusBar(this)

        btn_save.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            onBtnSaveClicked()
        }
        img_back.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            onBackPressed()
        }


        //on image back press
        onimg_cameraClicked()

    }

    /**
     * open gallry and upldoad image to the  server.
     *
     * @// Todo: 09/07/2019  open gallry and upload image to the  server.
     */

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 2501

    @RequiresApi(api = Build.VERSION_CODES.M)

    fun onimg_cameraClicked() {
        img_camera.setOnClickListener {
            if (DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                            REQUEST_ID_MULTIPLE_PERMISSIONS, this@RegisterActivity)) {
                initOptionsMenu(this@RegisterActivity)
            }
        }
    }

    private val CHOOSE_GALLERY = 2
    private val CHOOSE_CAMERA = 3
    fun initOptionsMenu(context: Activity) {
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
            captureImage()
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

    private lateinit var imageFile: File
    fun showPopUp(activity: Activity, picker: View, popupWindow: PopupWindow) {
        val trans = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f)
        popupWindow.showAtLocation(activity.getWindow().getDecorView()
                , Gravity.BOTTOM, 0, 0);
        trans.setDuration(400);
        trans.setInterpolator(AccelerateDecelerateInterpolator());
        picker.startAnimation(trans);
    }

    fun chooseGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
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

    fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            try {
                imageFile = createImageFile(this@RegisterActivity)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (imageFile != null) {
                val uri: Uri
                if (Build.VERSION.SDK_INT >= 23) {
                    val info = this.packageManager.resolveContentProvider(this.packageName + ".photos", PackageManager.GET_META_DATA)
                    val `in` = info.loadXmlMetaData(this.packageManager, this.packageName + ".photos")

                    this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    this.grantUriPermission("com.android.providers.media.MediaProvider", Uri.fromFile(imageFile), Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    uri = FileProvider.getUriForFile(this, this.packageName + ".photos", imageFile!!)
                } else {
                    uri = Uri.fromFile(imageFile)
                }

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(cameraIntent, CHOOSE_CAMERA)
            }
        }

    }

    var isFromSetting = false
    override fun onResume() {
        super.onResume()
        if (isFromSetting && DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                        REQUEST_ID_MULTIPLE_PERMISSIONS, this@RegisterActivity) == false) {
            isFromSetting = false
        }
    }

    val TAG = RegisterActivity::class.java.simpleName
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
                        initOptionsMenu(this@RegisterActivity)
                    } else {
                        Log.e(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            DelhiDarbar.instance!!.commonMethods.showDialogOKWidMsgTitle(
                                    this@RegisterActivity, resources.getString(R.string.access_full_feature_of_app), DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
                                override fun invoke(p1: DialogInterface, p2: Int) {
                                    when (p2) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            p1.dismiss()
                                            DelhiDarbar.instance!!.commonMethods.checkAndRequestPermissions(
                                                    REQUEST_ID_MULTIPLE_PERMISSIONS, this@RegisterActivity)
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
                                    this@RegisterActivity, resources.getString(R.string.access_full_feature_of_app), DialogInterface.OnClickListener(object : DialogInterface.OnClickListener, (DialogInterface, Int) -> Unit {
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

                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }
    }

    var hasImage = false;
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_CAMERA && resultCode == Activity.RESULT_OK) {
            val uri = Uri.fromFile(imageFile)
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
            this.imageFile = imageFile
            val uri = Uri.fromFile(imageFile)
            Log.i("TAG", "path: " + imageFile.absolutePath)

            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageFile = File(resultUri.path)
                    val bitmap: Bitmap
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, resultUri)

                        val tempUri = getImageUri(this@RegisterActivity, bitmap)
                        Log.e(TAG, "328::${File(getRealPathFromURI(tempUri))}")
                        img_user_image!!.setImageBitmap(bitmap)
                        hasImage = true
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

    private fun getRealPathFromURI(imageUri: Uri): String {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = this.managedQuery(imageUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    /**
     * Saving user
     */

    fun onBtnSaveClicked() {
        //Edit text
        val name = et_name!!.text!!.toString().trim { it <= ' ' }
        val email = et_email!!.text!!.toString().trim { it <= ' ' }
        val phone = et_phone!!.text!!.toString().trim { it <= ' ' }
        val password = et_password!!.text!!.toString().trim { it <= ' ' }
        val confirmPassword = et_conform!!.text!!.toString().trim { it <= ' ' }


        if (name.isEmpty()) {
//            et_name!!.error = getString(R.string.Please_enter_the_name)
            showAlert(getString(R.string.Please_enter_the_name),et_name)
        } else if (email.isEmpty()) {
//            et_email!!.error = getString(R.string.Please_enter_the_email)
            showAlert(getString(R.string.Please_enter_the_email),et_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            et_email!!.error = getString(R.string.Please_enter_the_email_valid_email)
            showAlert(getString(R.string.Please_enter_the_email_valid_email),et_email)
        } else if (phone.isEmpty()) {
//            et_phone!!.error = getString(R.string.Please_enter_the_email_the_number)
            showAlert(getString(R.string.Please_enter_the_email_the_number),et_phone)
        } else if(et_phone.text!!.length < 9){
            showAlert(getString(R.string.Please_enter_valid_phone),et_phone)
        }else if (password.isEmpty()) {
//            et_password!!.error = getString(R.string.Please_enter_the_password)
            showAlert(getString(R.string.Please_enter_the_password),et_password)
        } else if (password.length < 6) {
//            et_password!!.error = getString(R.string.Please_enter_valid_password)
            showAlert(getString(R.string.Please_enter_valid_password),et_password)
        } else if (confirmPassword.isEmpty()) {
//            et_conform!!.error = getString(R.string.Please_enter_the_password)
            showAlert(getString(R.string.Please_enter_the_password),et_conform)
        } else if (confirmPassword != password) {
//            et_conform!!.error = getString(R.string.password_dosnt_match)
            showAlert(getString(R.string.password_dosnt_match),et_conform)
        } else {
            DelhiDarbar.instance!!.commonMethods!!.hideKeyboard(this@RegisterActivity)
            submitUserDetails(name, phone, password, email)

        }


    }

fun showAlert(alert:String,requestFocus:EditText){
    DelhiDarbar.instance!!.commonMethods.showDialogOK(this@RegisterActivity,
            alert, { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                requestFocus.requestFocus()
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
            }
        }// proceed with logic by disabling the related features or quit the app.
    }, resources.getString(R.string.ok))
}
    private fun submitUserDetails(name: String, phone: String, password: String, email: String) {
        val map = HashMap<String, RequestBody>()
        if (hasImage) {
            val convert_File_2String = imageFile.toString()
            val filename = convert_File_2String.substring(convert_File_2String.lastIndexOf("/") + 1)
            map.put("image\"; filename=\"" + filename + "\" ", RequestBody.create(MediaType.parse("image/*"), imageFile))
        }
        map.put(ConstantKeys.name, RequestBody.create(MediaType.parse("text/plain"), name))
        map.put(ConstantKeys.email, RequestBody.create(MediaType.parse("text/plain"), email))
        map.put(ConstantKeys.password, RequestBody.create(MediaType.parse("text/plain"), password))
        map.put(ConstantKeys.phone, RequestBody.create(MediaType.parse("text/plain"), phone))
        progress_bar!!.visibility = View.VISIBLE

        RetrofitUtils
                .getRetrofitUtils()!!
                .create(Api::class.java)
                .createUser(map)
                .enqueue(object : Callback<ParentRegisterResponse> {
                    override fun onResponse(call: Call<ParentRegisterResponse>, response: Response<ParentRegisterResponse>) {
                        progress_bar!!.visibility = View.GONE
                       /* if (!response.isSuccessful)
                            return*/
                        if (!response.isSuccessful){
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(this@RegisterActivity,
                                    resources.getString(R.string.User_Already_exist), { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        dialog.dismiss()
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }// proceed with logic by disabling the related features or quit the app.
                            }, resources.getString(R.string.ok))
                        }else if (response!!.body()!!.error == false) {
                            DelhiDarbar.instance!!.commonMethods.showDialogOK(this@RegisterActivity,
                                    response?.body()?.message, { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        setFragmentManager()
                                        dialog.dismiss()
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }// proceed with logic by disabling the related features or quit the app.
                            }, resources.getString(R.string.ok))


                        }
                            progress_bar!!.visibility = View.GONE }

                    override fun onFailure(call: Call<ParentRegisterResponse>, t: Throwable) {
                        progress_bar!!.visibility = View.GONE
                        tab_email!!.error = t.message
                        snackbar = t.message?.let { Snackbar.make(constraintLayout!!, it, Snackbar.LENGTH_LONG) }
                    }
                })

    }

    private fun setFragmentManager() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager!!.beginTransaction()
        val login= LoginFragment()
        transaction.addToBackStack("LoginFragment")

        transaction.replace(R.id.fragment_contianor, login).commit()
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri")
    }


    companion object {
        var isFromBackPressed = false
        val MEDIA_TYPE_IMAGE = 1
        val IMAGE_DIRECTORY_NAME = "Android File Upload"
        private val PICK_FROM_GALLERY = 1
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_PICK_PHOTO = 2
        private val CAMERA_PIC_REQUEST = 1111
        private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100

        private fun getOutputMediaFile(type: Int): File? {

            // External sdcard location
            val mediaStorageDir = File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    IMAGE_DIRECTORY_NAME)

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Image", "Oops! Failed create "
                            + IMAGE_DIRECTORY_NAME + " directory")
                    return null
                }
            }

            // Create a media file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(Date())
            val mediaFile: File
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = File(mediaStorageDir.path + File.separator
                        + "IMG_" + ".jpg")
            } else {
                return null
            }

            return mediaFile
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isFromBackPressed = true
    }

}