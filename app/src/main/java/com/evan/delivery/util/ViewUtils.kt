package com.evan.delivery.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import com.evan.delivery.ui.custom.CustomDialog
import com.evan.delivery.R
import com.evan.delivery.ui.auth.interfaces.ClickInterface
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*



fun hideKeyboard(activity: Activity) {
    try {
        val view = activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
    catch (ee: java.lang.Exception) {
    }

}
fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG ).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}
fun rotateImage(degree: Int, imagePath: String): String {
    if (degree <= 0) {
        return imagePath
    }
    try {
        var b: Bitmap = BitmapFactory.decodeFile(imagePath)
        val matrix = Matrix()
        if (b.width > b.height) {
            matrix.setRotate(degree.toFloat())
            b = Bitmap.createBitmap(
                b, 0, 0, b.width, b.height,
                matrix, true
            )
        }
        val fOut = FileOutputStream(imagePath)
        val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
        val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)
        val out = FileOutputStream(imagePath)
        if (imageType.equals("png", ignoreCase = true)) {
            b.compress(Bitmap.CompressFormat.PNG, 100, out)
        } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                "jpg",
                ignoreCase = true
            )
        ) {
            b.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        fOut.flush()
        fOut.close()
        b.recycle()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return imagePath
}
@Throws(IOException::class)
fun rotateImageIfRequired(
    context: Context,
    img: Bitmap,
    selectedImage: Uri
): Bitmap {
    val input = context.contentResolver.openInputStream(selectedImage)
    val ei: ExifInterface
    ei = if (Build.VERSION.SDK_INT > 23) ExifInterface(input) else ExifInterface(selectedImage.path)
    val orientation = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
            img,
            90
        )
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
            img,
            180
        )
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
            img,
            270
        )
        else -> img
    }
}

fun rotateImage(
    img: Bitmap,
    degree: Int
): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    val rotatedImg: Bitmap = Bitmap.createBitmap(
        img,
        0,
        0,
        img.width,
        img.height,
        matrix,
        true
    )
    img.recycle()
    return rotatedImg
}
fun getImageUri(
    inContext: Context,
    inImage: Bitmap
): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String? = MediaStore.Images.Media.insertImage(
        inContext.contentResolver,
        inImage,
        "Title",
        null
    )
    return Uri.parse(path)
}
@Throws(IOException::class)
fun createImageFile(context: Context): File {
    // Create an image file name

    val imageFileName = createFileImageName()
    val storageDir: File?
    storageDir = if (context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) != null) {
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    } else {
        context.filesDir
    }
    storageDir!!.mkdirs()

    // Save a file: path for use with ACTION_VIEW intents
    //mCurrentPhotoPath = image.getAbsolutePath();


    return File.createTempFile(
        imageFileName,   /* prefix */
        ".jpg",          /* suffix */
        storageDir      /* directory */
    )
}

fun createFileImageName(): String {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    return "JPEG_" + timeStamp + "_"
}
fun getRealPathFromURI(uri: Uri?, activity: Activity): String? {
    val projection =
        arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        uri?.let { activity.getContentResolver().query(it, projection, null, null, null) }!!
    val column_index = cursor
        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    return cursor.getString(column_index)
}
fun getRightAngleImage(photoPath: String): String {
    try {
        val ei = ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        var degree = 0
        degree = when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> 0
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_UNDEFINED -> 0
            else -> 90
        }
        return rotateImage(degree, photoPath)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return photoPath
}
var PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE

)
var PERMISSIONS_CAMERA = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
fun isCameraePermissionGranted(activity: Activity?): Boolean {
    // Check if we have write permission


    val permission_camera: Int =
        ActivityCompat.checkSelfPermission(activity as Activity, Manifest.permission.CAMERA)
    val permission_storage: Int =
        ActivityCompat.checkSelfPermission(activity as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    return if (permission_camera != PackageManager.PERMISSION_GRANTED||permission_storage != PackageManager.PERMISSION_GRANTED) {
        false
    } else true
}
fun showImagePickerDialog(mContext: Context, dialogCallback: DialogActionListener) {
    val infoDialog = CustomDialog(mContext, R.style.CustomDialogTheme)
    val inflator =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v: View = inflator.inflate(R.layout.layout_image_chooser, null)
    infoDialog.setContentView(v)
    infoDialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    val main_root =
        infoDialog.findViewById(R.id.main_root) as RelativeLayout
    val btn_camera =
        infoDialog.findViewById(R.id.btn_camera) as LinearLayout
    val btn_gallery =
        infoDialog.findViewById(R.id.btn_gallery) as LinearLayout
    btn_camera.setOnClickListener {
        dialogCallback?.onPositiveClick()
        infoDialog.dismiss()
    }
    main_root?.setOnClickListener {
        infoDialog.dismiss()
    }
    btn_gallery.setOnClickListener {
        dialogCallback?.onNegativeClick()
        infoDialog.dismiss()
    }
    infoDialog.show()
}
fun showDialogSuccessMessage(
    mContext: Context, body: String?, actionString: String?, dialogCallback: ClickInterface?
) {
    val infoDialog = CustomDialog(mContext, R.style.CustomDialogTheme)
    val inflator =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v: View = inflator.inflate(R.layout.layout_dialog_reservation_popup_done, null)
    infoDialog.setContentView(v)
    infoDialog.setCancelable(false)
    infoDialog.setCanceledOnTouchOutside(false)
    infoDialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    val main_root = infoDialog.findViewById(R.id.main_root) as RelativeLayout
    val tv_info = infoDialog.findViewById(R.id.tv_info) as TextView
    val btn_ok = infoDialog.findViewById(R.id.btn_ok) as Button
    if (body == null) {
        tv_info.visibility = View.GONE
    } else {
        tv_info.text = body
    }

    if(actionString != null&&!actionString.isNullOrEmpty()){
        btn_ok.text = actionString
    }else
    {
        btn_ok.text = "Ok"

    }

    btn_ok.setOnClickListener {
        //your business logic

        if (dialogCallback != null) {
            dialogCallback.onItemClick(0)
        }
        infoDialog.dismiss()
    }
    infoDialog.show()
}
fun showDialogSuccessfull(
    mContext: Context,
    body: String?,
    positiveAction: String?,
    negetiveAction: String?,
    dialogCallback: DialogActionListener
) {
    val infoDialog = CustomDialog(mContext, R.style.CustomDialogTheme)
    val inflator =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v: View = inflator.inflate(R.layout.layout_successfull_pop_up, null)
    infoDialog.setContentView(v)
    infoDialog.getWindow()?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    val main_root =
        infoDialog.findViewById(R.id.main_root) as RelativeLayout
    val btnOK = infoDialog.findViewById(R.id.btn_ok) as Button
    val btnCancel =
        infoDialog.findViewById(R.id.btn_cancel) as Button
    val tv_info =
        infoDialog.findViewById(R.id.tv_info) as TextView
    if (body == null) {
        tv_info.visibility = View.GONE
    } else {
        tv_info.visibility = View.VISIBLE
        tv_info.text = body
    }
    if (positiveAction != null) {
        btnOK.text = positiveAction
    }
    if (negetiveAction != null) {
        btnCancel.text = negetiveAction
    }
    btnOK.setOnClickListener {
        //your business logic

        dialogCallback?.onPositiveClick()
        infoDialog.dismiss()
    }
    btnCancel.setOnClickListener {
        //your business logic

        dialogCallback.onNegativeClick()
        infoDialog.dismiss()
    }
    infoDialog.show()
}
fun getHashPassWordMD5(password: String): String? {
    var md: MessageDigest? = null
    try {
        md = MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    val hashInBytes =
        md!!.digest(password.toByteArray(StandardCharsets.UTF_8))
    val sb = StringBuilder()
    for (b in hashInBytes) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}
