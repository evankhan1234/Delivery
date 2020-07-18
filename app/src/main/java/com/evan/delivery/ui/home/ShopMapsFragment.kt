package com.evan.delivery.ui.home

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.data.db.entities.Shop
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.interfaces.IShopListener
import com.evan.delivery.util.GoogleMapDTO
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class ShopMapsFragment : Fragment() ,KodeinAware,IShopListener{
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var token: String? = ""
    var order: Orders? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_shop_maps, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.shopListener=this

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)

        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(Orders::class.java.getSimpleName()) != null) {
                order = args?.getParcelable(Orders::class.java.getSimpleName())

                Log.e("data", "data" + Gson().toJson(order))

            }
        }
        viewModel.getShopBy(token!!,order?.ShopId!!)
        return root
    }
    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyB8fLUHActNwh_rAqQIbGHUn-t-touoUt8"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    //   path.add(endLatLng)
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLACK)
                lineoption.geodesic(true)
            }
            googleMap.addPolyline(lineoption)
        }
    }
    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
    var latitudes:Double?=0.0
    var longitudes:Double?=0.0
    override fun onShow(shop: Shop) {
        mapFragment.getMapAsync(OnMapReadyCallback
        {
            googleMap = it
            Log.d("GoogleMap", "before isMyLocationEnabled")
//            googleMap.isMyLocationEnabled = true
            val latitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE)?.toDouble()
            val longitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE)?.toDouble()
            val location1 = LatLng(latitude!!,longitude!!)
            googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 12.5f ))

            latitudes=shop?.Latitude!!
            longitudes=shop?.Longitude!!

            //val location3 = LatLng(23.8084641,90.4277429)
            val location3 = LatLng(latitudes!!,longitudes!!)
            googleMap.addMarker(MarkerOptions().position(location3).title("Shop Location").icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE)))

            Log.d("GoogleMap", "before URL")
            val URL = getDirectionURL(location1,location3)
            Log.d("GoogleMap", "URL : $URL")
            GetDirection(URL).execute()

        })
    }


}