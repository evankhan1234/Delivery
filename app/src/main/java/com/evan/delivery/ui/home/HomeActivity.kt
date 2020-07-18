package com.evan.delivery.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.data.db.entities.Shop
import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.interfaces.IProfileListener
import com.evan.delivery.ui.home.dashboard.DashboardFragment
import com.evan.delivery.ui.home.orderdelivery.OrderDeliveryFragment

import com.evan.delivery.ui.home.settings.SettingsFragment
import com.evan.delivery.util.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.bottom_navigation_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

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
        if (requestCode == PERMISSION_REQUEST) {
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
    }

    override fun onProfile(user: Users) {
        deliveryStatus=user?.DeliveryStatus
    }
}
