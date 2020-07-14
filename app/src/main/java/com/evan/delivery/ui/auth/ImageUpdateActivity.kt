package com.evan.delivery.ui.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.evan.delivery.R
import com.evan.delivery.ui.auth.interfaces.Listener
import com.evan.delivery.util.*



import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageUpdateActivity : AppCompatActivity(),
    Listener, KodeinAware {
    override val kodein by kodein()
    internal var imagefile: File? = null
    internal var image_uri: Uri? = null
    internal var profile_image: ImageView? = null
    internal var btn_update: Button? = null
    internal var btn_cancel: Button? = null
    internal var progress_bar: ProgressBar? = null
    internal var empty_image: RelativeLayout? = null
    internal var from = 0
    internal var mActivity: Activity? = null
    private val factory : AuthViewModelFactory by instance()
    private lateinit var authViewmodel: AuthViewModel
    //FRAG_CREATE_NEW_DELIVERY
    var STORED_DATA: String? = "stored_data"
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.e("Save", "onSaveInstanceState")
        savedInstanceState.putInt(STORED_DATA, from)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_update)
        val viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        authViewmodel = viewModel
        authViewmodel.AddListener=this
        btn_update = findViewById(R.id.btn_confirm_ok)
        progress_bar = findViewById(R.id.progress_bar)
        btn_cancel = findViewById(R.id.btn_cancel)
        profile_image = findViewById(R.id.profile_image)
        //  empty_image = findViewById(R.id.empty_image)

        imagefile = temporary_profile_picture
        image_uri = temporary_profile_picture_uri
        mActivity = this
        if (imagefile != null && image_uri != null) {
            handleImageFile(image_uri!!, imagefile!!)
        }
        if (intent != null) {
            from = intent.getIntExtra("from", 0)
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STORED_DATA)) {
                from = savedInstanceState.getInt(STORED_DATA)
            }

        }

    }

    fun onCancelPressed(view: View?) {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        intent.putExtra("updated_image_url", "")
        finish()
        overridePendingTransition(R.anim.stand_by, R.anim.left_to_right)
    }

    fun afterClickUpdate(view: View?) {


        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), imagefile!!)

        val description: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "image-type")


        val part =
            MultipartBody.Part.createFormData("uploaded_file", imagefile?.name, requestBody)

          authViewmodel.uploadImage(part,description)

    }

    private fun handleImageFile(uri: Uri, file: File) {
        var uri: Uri? = uri
        val fileSize = file.length().toInt()
        Log.e("file before", fileSize.toString() + "")
        var myBitmap: Bitmap? = null
        if (file.exists()) {
            myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            try {
                myBitmap = rotateImageIfRequired(applicationContext, myBitmap, uri!!)
                profile_image?.setImageBitmap(myBitmap)
                // empty_image?.visibility=View.INVISIBLE
            } catch (e: Exception) {
            }
        }

        try {
            if (myBitmap != null) uri = getImageUri(mActivity as Activity, myBitmap!!)
        } catch (e: java.lang.Exception) {
        }


        if (fileSize > SERVER_SEND_FILE_SIZE_LIMIT) {
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val compressFile: File
                        try {
                            compressFile = createImageFile(this@ImageUpdateActivity)
                            val outputStream: FileOutputStream
                            outputStream = FileOutputStream(compressFile)
                            resource.compress(
                                Bitmap.CompressFormat.JPEG,
                                COMPRESS_QUALITY,
                                outputStream
                            )
                            outputStream.close()
                            Log.e("file after", compressFile.length().toString() + "")
                            imagefile = compressFile
                            // profile_image.setImageBitmap(resource);
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })

        }


    }


    override fun Success(result: String) {
        Log.e("result", "" + result)

        runOnUiThread(Runnable {
            try {
                Log.e("result", "" + result)
                val intent = Intent()
                intent.putExtra("updated_image_url", result)
                setResult(RESULT_OK, intent)
                finish()
            }
            catch ( e:Exception)
            {

            }


        })
    }

    override fun Failure(result: String) {

    }

//    override fun couponData(coupon: MutableList<Coupon>) {
//    }
//
//    override fun noInternetConnectionFound() {
//    }
//
//    override fun showProgress() {
//        progress_bar?.visibility=View.VISIBLE
//    }

//    override fun hiddenProgress() {
//        progress_bar?.visibility=View.GONE
//    }
//
//    override fun onFailure(message: String?) {
//        progress_bar?.visibility=View.GONE
//Log.e("onFailure",""+message)
//    }
}
