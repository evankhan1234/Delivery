package com.evan.delivery.ui.home.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.evan.delivery.R
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DashboardFragment : Fragment() {

    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap

    var tv_store:TextView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_dashboard, container, false)
        tv_store=root?.findViewById(R.id.tv_store)
        tv_store?.isSelected=true

        mapFragment = childFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            Log.d("GoogleMap", "before isMyLocationEnabled")
//            googleMap.isMyLocationEnabled = true
            val latitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE)?.toDouble()
            val longitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE)?.toDouble()
            val location1 = LatLng(latitude!!, longitude!!)
            googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 16.0f))
            val zoomLevel = 16.0f //This goes up to 21

//            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, zoomLevel))
//            Log.d("GoogleMap", "before location2")
//            val location2 = LatLng(23.7926304, 90.3569598)
//            googleMap.addMarker(MarkerOptions().position(location2).title("Madurai"))
//
//            Log.d("GoogleMap", "before location3")
//
//            val location3 = LatLng(23.7926304, 90.3569598)
//            googleMap.addMarker(MarkerOptions().position(location3).title("Bangalore"))

        })
        return root
    }



}