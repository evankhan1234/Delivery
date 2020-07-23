package com.evan.delivery.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.evan.delivery.BuildConfig
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.*
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.ImageUpdateActivity
import com.evan.delivery.ui.auth.interfaces.IProfileListener
import com.evan.delivery.ui.home.customerorder.UpdateOrdersFragment
import com.evan.delivery.ui.home.dashboard.DashboardFragment
import com.evan.delivery.ui.home.delivery.DeliveryFragment
import com.evan.delivery.ui.home.orderdelivery.OrderDeliveryFragment
import com.evan.delivery.ui.home.owndelivery.OwnDeliveryFragment
import com.evan.delivery.ui.home.owndelivery.details.OwnDeliveryDetailsFragment

import com.evan.delivery.ui.home.settings.SettingsFragment
import com.evan.delivery.ui.home.settings.password.ChangePasswordFragment
import com.evan.delivery.ui.home.settings.profile.ProfileUpdateFragment
import com.evan.delivery.util.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.bottom_navigation_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException

private const val PERMISSION_REQUEST = 10
class HomeActivity : AppCompatActivity(), KodeinAware, IProfileListener {

    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    var mFragManager: FragmentManager? = null
    var fragTransaction: FragmentTransaction? = null
    var mCurrentFrag: Fragment? = null
    var CURRENT_PAGE: Int? = 1
    var activity: Activity?=null
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    var auth: FirebaseAuth? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    var progress_bar: ProgressBar? = null
    private var fresh: String? = ""
    private var token: String? = ""
    var deliveryStatus:Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.profileListener=this
        token = SharedPreferenceUtil.getShared(this, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        viewModel.getUserProfile(token!!)

        activity=this
        progress_bar=findViewById(R.id.progress_bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }
        fresh = SharedPreferenceUtil.getShared(this, SharedPreferenceUtil.TYPE_FRESH)
        if (fresh != null && !fresh?.trim().equals("") && !fresh.isNullOrEmpty()) {
            setUpHeader(FRAG_TOP)
            afterClickTabItem(FRAG_TOP, null)
            setUpFooter(FRAG_TOP)
        } else {
            progress_bar?.show()

            Handler().postDelayed(Runnable {
                progress_bar?.visibility = View.GONE
                setUpHeader(FRAG_TOP)
                afterClickTabItem(FRAG_TOP, null)
                setUpFooter(FRAG_TOP)
            }, 10000)
            SharedPreferenceUtil.saveShared(
                this,
                SharedPreferenceUtil.TYPE_FRESH,
                "Fresh"
            )
        }
        img_header_back?.setOnClickListener {
            onBackPressed()
        }
        btn_orders?.setOnClickListener {
            setUpHeader(FRAG_ORDER)
            //afterClickTabItem(FRAG_ORDER, null)
            addFragment(FRAG_ORDER, true, null)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

        val f = getVisibleFragment()
        if (f != null)
        {
            if (f is OrderDeliveryFragment) {
                val storeFragment: OrderDeliveryFragment =
                    mFragManager?.findFragmentByTag(FRAG_STORE.toString()) as OrderDeliveryFragment
                setUpHeader(FRAG_STORE)
            }
            if (f is SettingsFragment) {
                val storeFragment: SettingsFragment =
                    mFragManager?.findFragmentByTag(FRAG_SETTINGS.toString()) as SettingsFragment
                //  storeFragment.removeChild()
                setUpHeader(FRAG_SETTINGS)
            }
            if (f is DashboardFragment) {
                val storeFragment: DashboardFragment =
                    mFragManager?.findFragmentByTag(FRAG_TOP.toString()) as DashboardFragment
                //  storeFragment.removeChild()
                setUpHeader(FRAG_TOP)
            }

            if (f is OwnDeliveryFragment) {
                val storeFragment: OwnDeliveryFragment =
                    mFragManager?.findFragmentByTag(FRAG_ORDER.toString()) as OwnDeliveryFragment
                //  storeFragment.removeChild()
                setUpHeader(FRAG_ORDER)
            }
        }

    }
    fun getVisibleFragment(): Fragment? {
        val fragmentManager = mFragManager
        val fragments = fragmentManager!!.fragments
        fragments.reverse()
        for (fragment in fragments!!) {
            if (fragment != null && fragment.isVisible) {
                return fragment
            }
        }
        return null
    }
    fun btn_home_clicked(view: View) {
        setUpHeader(FRAG_TOP)
        afterClickTabItem(FRAG_TOP, null)
        setUpFooter(FRAG_TOP)

    }

    fun btn_store_clicked(view: View) {
        if (deliveryStatus==1){
            setUpHeader(FRAG_STORE)
            afterClickTabItem(FRAG_STORE, null)
            setUpFooter(FRAG_STORE)
        }
        else{
            Toast.makeText(this,"Your Service is of now",Toast.LENGTH_SHORT).show()
        }

        //ccheckPP()


    }


    fun deliveryStatusFor( value:Int){
        deliveryStatus=value
    }

    fun btn_settings_clicked(view: View) {
        setUpHeader(FRAG_SETTINGS)
        afterClickTabItem(FRAG_SETTINGS, null)
        setUpFooter(FRAG_SETTINGS)
    }
    @Suppress("UNUSED_PARAMETER")
    fun afterClickTabItem(fragId: Int, obj: Any?) {
        addFragment(fragId, false, obj)

    }
    fun finishs(){
        finishAffinity()
    }
    fun goToChangePasswordFragment(users: Users) {
        setUpHeader(FRAG_CHANGE_PASSWORD)
        mFragManager = supportFragmentManager
        var fragId:Int?=0
        fragId=FRAG_CHANGE_PASSWORD
        fragTransaction = mFragManager?.beginTransaction()
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {

        }
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null
        newFrag = ChangePasswordFragment()
        val b= Bundle()
        b.putParcelable(Users::class.java?.getSimpleName(), users)
        newFrag.setArguments(b)
        mCurrentFrag = newFrag
        fragTransaction!!.setCustomAnimations(
            R.anim.view_transition_in_left,
            R.anim.view_transition_out_left,
            R.anim.view_transition_in_right,
            R.anim.view_transition_out_right
        )

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())
        fragTransaction?.addToBackStack(fragId.toString())
        fragTransaction!!.commit()

    }
    fun goToProfileUpdateFragment(users: Users) {
        setUpHeader(FRAG_PROFILE_UPDATE)
        mFragManager = supportFragmentManager
        var fragId:Int?=0
        fragId=FRAG_PROFILE_UPDATE
        fragTransaction = mFragManager?.beginTransaction()
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {

        }
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null
        newFrag = ProfileUpdateFragment()
        val b= Bundle()
        b.putParcelable(Users::class.java?.getSimpleName(), users)
        newFrag.setArguments(b)
        mCurrentFrag = newFrag
        fragTransaction!!.setCustomAnimations(
            R.anim.view_transition_in_left,
            R.anim.view_transition_out_left,
            R.anim.view_transition_in_right,
            R.anim.view_transition_out_right
        )

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())
        fragTransaction?.addToBackStack(fragId.toString())
        fragTransaction!!.commit()

    }
    fun goToViewDeliveryFragment(orders: Orders) {
        setUpHeader(FRAG_VIEW_DELIVERY)
        mFragManager = supportFragmentManager
        // create transaction
        var fragId:Int?=0
        fragId=FRAG_VIEW_DELIVERY
        fragTransaction = mFragManager?.beginTransaction()
        //check if there is any backstack if yes then remove it
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {
            //this will clear the back stack and displays no animation on the screen
            // mFragManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        // check current fragment is wanted fragment
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null

        // identify which fragment will be called

        newFrag = ShopMapsFragment()
        val b= Bundle()
        b.putParcelable(Orders::class.java?.getSimpleName(), orders)

        newFrag.setArguments(b)

        mCurrentFrag = newFrag

        fragTransaction!!.setCustomAnimations(
            R.anim.view_transition_in_left,
            R.anim.view_transition_out_left,
            R.anim.view_transition_in_right,
            R.anim.view_transition_out_right
        )

        // param 1: container id, param 2: new fragment, param 3: fragment id

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())
        // prevent showed when user press back fabReview
        fragTransaction?.addToBackStack(fragId.toString())
        //  fragTransaction?.hide(active).show(guideFragment).commit();
        fragTransaction!!.commit()

    }
    fun goToUpdateDeliveryFragment(delivery: Delivery) {
        setUpHeader(FRAG_DELIVERY_FOR)
        mFragManager = supportFragmentManager
        // create transaction
        var fragId:Int?=0
        fragId=FRAG_DELIVERY_FOR
        fragTransaction = mFragManager?.beginTransaction()
        //check if there is any backstack if yes then remove it
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {
            //this will clear the back stack and displays no animation on the screen
            // mFragManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        // check current fragment is wanted fragment
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null

        // identify which fragment will be called

        newFrag = UpdateOrdersFragment()
        val b= Bundle()
        b.putParcelable(Delivery::class.java?.getSimpleName(), delivery)

        newFrag.setArguments(b)

        mCurrentFrag = newFrag

        fragTransaction!!.setCustomAnimations(
            R.anim.view_transition_in_left,
            R.anim.view_transition_out_left,
            R.anim.view_transition_in_right,
            R.anim.view_transition_out_right
        )

        // param 1: container id, param 2: new fragment, param 3: fragment id

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())
        // prevent showed when user press back fabReview
        fragTransaction?.addToBackStack(fragId.toString())
        //  fragTransaction?.hide(active).show(guideFragment).commit();
        fragTransaction!!.commit()

    }
    fun goToViewOwnDeliveryDetailsFragment(ownDelivery: OwnDelivery) {
        setUpHeader(FRAG_ORDER_DETAILS)
        mFragManager = supportFragmentManager
        // create transaction
        var fragId:Int?=0
        fragId=FRAG_ORDER_DETAILS
        fragTransaction = mFragManager?.beginTransaction()
        //check if there is any backstack if yes then remove it
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {
            //this will clear the back stack and displays no animation on the screen
            // mFragManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        // check current fragment is wanted fragment
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null

        // identify which fragment will be called

        newFrag = OwnDeliveryDetailsFragment()
        val b= Bundle()
        b.putParcelable(OwnDelivery::class.java?.getSimpleName(), ownDelivery)

        newFrag.setArguments(b)

        mCurrentFrag = newFrag

        fragTransaction!!.setCustomAnimations(
            R.anim.view_transition_in_left,
            R.anim.view_transition_out_left,
            R.anim.view_transition_in_right,
            R.anim.view_transition_out_right
        )

        // param 1: container id, param 2: new fragment, param 3: fragment id

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())
        // prevent showed when user press back fabReview
        fragTransaction?.addToBackStack(fragId.toString())
        //  fragTransaction?.hide(active).show(guideFragment).commit();
        fragTransaction!!.commit()

    }
    fun addFragment(fragId: Int, isHasAnimation: Boolean, obj: Any?) {
        // init fragment manager
        mFragManager = supportFragmentManager
        // create transaction
        fragTransaction = mFragManager?.beginTransaction()
        //check if there is any backstack if yes then remove it
        val count = mFragManager?.getBackStackEntryCount()
        if (count != 0) {
            //this will clear the back stack and displays no animation on the screen
            // mFragManager?.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        // check current fragment is wanted fragment
        if (mCurrentFrag != null && mCurrentFrag!!.getTag() != null && mCurrentFrag!!.getTag() == fragId.toString()) {
            return
        }
        var newFrag: Fragment? = null
        // identify which fragment will be called
        when (fragId) {
            FRAG_TOP -> {
                newFrag = DashboardFragment()
            }
            FRAG_STORE -> {
                newFrag = OrderDeliveryFragment()
            }

            FRAG_SETTINGS -> {
                newFrag = SettingsFragment()
            }
            FRAG_ORDER-> {
                newFrag = OwnDeliveryFragment()
            }
        }

        mCurrentFrag = newFrag
        // init argument
        if (obj != null) {
            val args = Bundle()
        }
        // set animation
        if (isHasAnimation) {
            fragTransaction!!.setCustomAnimations(
                R.anim.view_transition_in_left,
                R.anim.view_transition_out_left,
                R.anim.view_transition_in_right,
                R.anim.view_transition_out_right
            )
        }
        // param 1: container id, param 2: new fragment, param 3: fragment id

        fragTransaction?.replace(R.id.main_container, newFrag!!, fragId.toString())

        // prevent showed when user press back fabReview
        fragTransaction?.addToBackStack(fragId.toString())
        //  fragTransaction?.hide(active).show(guideFragment).commit();
        fragTransaction!!.commit()

    }
    fun setUpHeader(type: Int) {
        when (type) {
            FRAG_TOP -> {
                ll_back_header?.visibility = View.GONE
                rlt_header?.visibility = View.VISIBLE
                tv_title.text = resources.getString(R.string.home)
                btn_footer_home.setSelected(true)
            }
            FRAG_STORE -> {
                ll_back_header?.visibility = View.GONE
                rlt_header?.visibility = View.VISIBLE
                tv_title.text = resources.getString(R.string.orders)
                btn_footer_store.setSelected(true)
            }

            FRAG_SETTINGS -> {
                ll_back_header?.visibility = View.GONE
                rlt_header?.visibility = View.VISIBLE
                tv_title.text = resources.getString(R.string.settings)
                btn_footer_settings.setSelected(true)

            }
            FRAG_VIEW_DELIVERY->{
                ll_back_header?.visibility = View.VISIBLE
                rlt_header?.visibility = View.GONE
                tv_details.text = resources.getString(R.string.shop_map)
            }
            FRAG_DELIVERY_FOR->{
                ll_back_header?.visibility = View.VISIBLE
                rlt_header?.visibility = View.GONE
                tv_details.text = resources.getString(R.string.update_order)
            }
            FRAG_ORDER->{
                ll_back_header?.visibility = View.VISIBLE
                rlt_header?.visibility = View.GONE
                tv_details.text = resources.getString(R.string.delivery)
            }
            FRAG_ORDER_DETAILS->{
                ll_back_header?.visibility = View.VISIBLE
                rlt_header?.visibility = View.GONE
                tv_details.text = resources.getString(R.string.details)
            }
            FRAG_PROFILE_UPDATE->{
                ll_back_header?.visibility = View.VISIBLE
                rlt_header?.visibility = View.GONE
                tv_details.text = resources.getString(R.string.update_profile)
            }
            else -> {

            }
        }
    }
    fun setUpFooter(type: Int) {
        setUnselectAllmenu()
        CURRENT_PAGE = type

        when (type) {
            FRAG_TOP -> {
                shadow_line?.visibility = View.VISIBLE
                bottom_navigation?.visibility = View.VISIBLE

                btn_footer_home.setSelected(true)
                tv_home_menu.setSelected(true)
            }
            FRAG_STORE -> {
                shadow_line?.visibility = View.VISIBLE
                bottom_navigation?.visibility = View.VISIBLE

                btn_footer_store.setSelected(true)
                tv_store_menu.setSelected(true)
            }

            FRAG_SETTINGS-> {
                shadow_line?.visibility = View.VISIBLE
                bottom_navigation?.visibility = View.VISIBLE

                btn_footer_settings.setSelected(true)
                tv_settings_menu.setSelected(true)
            }


            else -> {

            }

        }
    }

    private fun setUnselectAllmenu() {
        btn_footer_home.setSelected(false)
        tv_home_menu.setSelected(false)

        btn_footer_store.setSelected(false)
        tv_store_menu.setSelected(false)

        btn_footer_settings.setSelected(false)
        tv_settings_menu.setSelected(false)
    }

    private fun enableView() {
        getLocation()
        // Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps)
            {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE, locationGps!!.latitude.toString())
                            SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE, locationGps!!.longitude.toString())
                            Log.d("Evan", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("Evan", " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE, locationNetwork!!.latitude.toString())
                            SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE, locationNetwork!!.longitude.toString())
                            Log.d("Khan", "Khan1" + locationNetwork!!.latitude)
                            Log.d("Khan", "Khan2" + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE, locationGps!!.latitude.toString())
                    SharedPreferenceUtil.saveShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE, locationGps!!.longitude.toString())
                    Log.d("CodeAndroidLocation", " Khan2" + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " Khan2" + locationNetwork!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST)
        {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }
        else if(requestCode==CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Start your camera handling here

                takePhoto()
            }
        }
        else if(requestCode==REQUEST_EXTERNAL_STORAGE_FROM_CAPTURE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Start your camera handling here
                getImageFromGallery()
            }
        }


    }

    override fun onProfile(user: Users) {
        deliveryStatus=user?.DeliveryStatus
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
                            //update in
                            if ( image_update.equals("profile")){
                                val f = getVisibleFragment()
                                if (f != null) {
                                    if (f is ProfileUpdateFragment) {

                                        f.showImage(updated_image_url)
                                    }

                                }
                            }





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
}
