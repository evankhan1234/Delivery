package com.evan.delivery.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.evan.delivery.BuildConfig
import com.evan.delivery.R
import com.evan.delivery.ui.auth.interfaces.ISignUpListener
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_create_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateAccountActivity  : AppCompatActivity() , KodeinAware, ISignUpListener {
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var image_address:String=""
    var auth: FirebaseAuth? = null
    var reference: DatabaseReference? = null

    var name: String=""
    var mobile: String=""
    var email: String=""
    var password: String=""
    var address: String=""
    var genderId: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.signUpListener=this
        auth = FirebaseAuth.getInstance()
        et_password?.transformationMethod = MyPasswordTransformationMethod()
        show_pass?.setOnClickListener {
            onPasswordVisibleOrInvisible()
        }
        img_user_add?.setOnClickListener {
            openImageChooser()
        }

        img_header_back?.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        btn_ok?.setOnClickListener {


            if(radio_male?.isChecked!!){
                genderId=1
            }
            else if(radio_female?.isChecked!!){
                genderId=2
            }
            Log.e("genderId","genderId"+genderId)
            name=et_name?.text.toString()
            mobile=et_phone?.text.toString()
            email=et_email?.text.toString()
            password=et_password?.text.toString()
            address=et_address?.text.toString()
            if(name.isNullOrEmpty() && mobile.isNullOrEmpty()&& email.isNullOrEmpty() && password.isNullOrEmpty() && address.isNullOrEmpty()&& image_address.isNullOrEmpty()){
                root_layout?.snackbar("All Field is Empty")
            }
            else if(name.isNullOrEmpty()){
                root_layout?.snackbar("Name is Empty")
            }
            else if(mobile.isNullOrEmpty()){
                root_layout?.snackbar("Mobile is Empty")
            }
            else if(email.isNullOrEmpty()){
                root_layout?.snackbar("Email is Empty")
            }
            else if(password.isNullOrEmpty()){
                root_layout?.snackbar("Password is Empty")
            }
            else if(address.isNullOrEmpty()){
                root_layout?.snackbar("NID is Empty")
            }
            else if(image_address.isNullOrEmpty()){
                root_layout?.snackbar("Image is Empty")
            }
            else{
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val currentDate = sdf.format(Date())
                viewModel.signUp(email,password,name,image_address,currentDate,address,mobile,0,genderId,0)

            }
        }
    }
    fun onPasswordVisibleOrInvisible() {
        val cursorPosition = et_password?.selectionStart

        if (et_password?.transformationMethod == null) {
            et_password?.transformationMethod = MyPasswordTransformationMethod()
            show_pass?.isSelected = false
        } else {

            et_password?.transformationMethod = null
            show_pass?.isSelected = true
        }
        et_password?.setSelection(cursorPosition!!)
    }
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val RESULT_TAKE_PHOTO = 10
    private val RESULT_LOAD_IMG = 101
    private val REQUEST_EXTERNAL_STORAGE_FROM_CAPTURE = 1002
    private val RESULT_UPDATE_IMAGE = 11

    fun openImageChooser() {
        showImagePickerDialog(this, object :
            DialogActionListener {
            override fun onPositiveClick() {
                openCamera()
            }

            override fun onNegativeClick() {
                checkGalleryPermission()
            }
        })
    }
    private fun checkGalleryPermission() {
        if (isCameraePermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_EXTERNAL_STORAGE_FROM_CAPTURE
                    )
                } else {
                    //start your camera

                    getImageFromGallery()
                }
            } else {
                getImageFromGallery()
            }
        } else {
            //required permission

            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE_FROM_CAPTURE
            )
        }
    }
    fun openCamera() {
        if (isCameraePermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED&&checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf<String?>(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                } else {
                    //start your camera

                    takePhoto()
                }
            } else {
                takePhoto()
            }
        } else {
            //required permission

            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_CAMERA,
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start your camera handling here

                    takePhoto()
                }
            }
            REQUEST_EXTERNAL_STORAGE_FROM_CAPTURE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start your camera handling here
                    getImageFromGallery()
                }
            }
        }
    }

    private var mTakeUri: Uri? = null
    private var mFile: File? = null
    private var mCurrentPhotoPath: String? = null
    private fun takePhoto() {
        val intent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            mFile = null
            try {
                mFile = createImageFile(this)
                mCurrentPhotoPath = mFile?.getAbsolutePath()
            } catch (ex: IOException) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created


            if (mFile != null) {
                mTakeUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID, mFile!!
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val packageManager: PackageManager =
                        this.getPackageManager()
                    val activities: List<ResolveInfo> =
                        packageManager.queryIntentActivities(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                    for (resolvedIntentInfo in activities) {
                        val packageName: String? =
                            resolvedIntentInfo.activityInfo.packageName
                        this.grantUriPermission(
                            packageName,
                            mTakeUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakeUri)
                startActivityForResult(intent, RESULT_TAKE_PHOTO)
            }
        }
    }

    private fun getImageFromGallery() {
        val photoPickerIntent =
            Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        Log.e("requestCode", resultCode.toString() + " requestCode" + requestCode)
        Log.e("RESULT_OK", "RESULT_OK" + RESULT_OK)


        when (requestCode) {
            RESULT_LOAD_IMG -> {
                try {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        val file = File(getRealPathFromURI(imageUri, this))
                        goImagePreviewPage(imageUri, file)
                    }
                } catch (e: Exception) {
                    Log.e("exc", "" + e.message)
                    Toast.makeText(this,"Can not found this image", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            RESULT_TAKE_PHOTO -> {
                //return if photopath is null
                if(mCurrentPhotoPath == null)
                    return
                mCurrentPhotoPath = getRightAngleImage(mCurrentPhotoPath!!)
                try {
                    val imgFile = File(mCurrentPhotoPath)
                    if (imgFile.exists()) {
                        goImagePreviewPage(mTakeUri, imgFile)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            RESULT_UPDATE_IMAGE -> {
                if (data != null && data?.hasExtra("updated_image_url")) {
                    val updated_image_url: String? = data?.getStringExtra("updated_image_url")
                    Log.e("updated_image_url", "--$updated_image_url")
                    if (updated_image_url != null) {
                        if (updated_image_url == "") {

                        } else {
                            img_user_add?.visibility= View.GONE
                            image_address="http://192.168.0.105/"+updated_image_url
                            //update in
                            Glide.with(this)
                                .load("http://192.168.0.105/"+updated_image_url)
                                .into(img_user_profile!!)

//                            image_address="http://hathbazzar.com/"+updated_image_url
//                            //update in
//                            Glide.with(this)
//                                .load("http://hathbazzar.com/"+updated_image_url)
//                                .into(img_user_profile!!)

                        }
                    }
                }
            }





        }

    }

    fun goImagePreviewPage(uri: Uri?, imageFile: File) {
        val fileSize = imageFile.length().toInt()
        if (fileSize <= SERVER_SEND_FILE_SIZE_MAX_LIMIT) {
            val i = Intent(
                this,
                ImageUpdateActivity::class.java
            )
            i.putExtra("from", FRAG_CREATE_NEW_DELIVERY)
            temporary_profile_picture = imageFile
            temporary_profile_picture_uri = uri
            startActivityForResult(i, RESULT_UPDATE_IMAGE)
            overridePendingTransition(R.anim.right_to_left, R.anim.stand_by)
        } else {
            showDialogSuccessMessage(
                this,
                resources.getString(R.string.image_size_is_too_large),
                resources.getString(R.string.txt_close),


                null
            )
        }
    }

    override fun onStarted() {
        progress_bar?.visibility= View.VISIBLE
    }

    override fun onSuccess(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()

    }

    override fun onFailure(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun onEnd() {
        progress_bar?.visibility=View.GONE
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onEndError() {
        progress_bar?.visibility= View.GONE
    }



}