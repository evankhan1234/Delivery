package com.evan.delivery.ui.home.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.CustomerOrderCount
import com.evan.delivery.data.db.entities.LastFiveSales
import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderCountListener
import com.evan.delivery.ui.auth.interfaces.ILastFiveSalesListener
import com.evan.delivery.ui.auth.interfaces.IProfileListener
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.SharedPreferenceUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class DashboardFragment : Fragment() , KodeinAware,ILastFiveSalesListener,
    ICustomerOrderCountListener,IProfileListener {
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap
    var chart: BarChart?=null
    var token: String? = ""
    var pushToken: String? = ""
    private val valueSet1: ArrayList<BarEntry>?=arrayListOf()
    var tv_store:TextView?=null
    var tv_delivered:TextView?=null
    var tv_processing:TextView?=null
    var tv_pending:TextView?=null
    var switch_status:SwitchCompat?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_dashboard, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.lastFiveSalesListener=this
        viewModel.customerOrderCountListener=this
        viewModel.profileListener=this
        switch_status=root?.findViewById(R.id.switch_status)
        tv_store=root?.findViewById(R.id.tv_store)
        tv_delivered=root?.findViewById(R.id.tv_delivered)
        tv_processing=root?.findViewById(R.id.tv_processing)
        tv_pending=root?.findViewById(R.id.tv_pending)
        tv_store?.isSelected=true
        chart=root?.findViewById(R.id.chart1)
        chart!!.setDescription("")
        pushToken = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_PUSH_TOKEN)
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        mapFragment = childFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            val latitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LATITUDE)?.toDouble()
            val longitude = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_LONGITUDE)?.toDouble()
            val location1 = LatLng(latitude!!, longitude!!)
            googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 16.0f))

        })
        viewModel.getLasFive(token!!)
        viewModel.getCustomerOrderCount(token!!)
        viewModel.getUserProfile(token!!)
        viewModel.createToken(token!!,3,pushToken!!)

        switch_status?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.updateDeliveryService(token!!,1)
                (activity as HomeActivity).deliveryStatusFor(1)
            } else {
                viewModel.updateDeliveryService(token!!,0)
                (activity as HomeActivity).deliveryStatusFor(0)
            }
        }
        return root
    }

    override fun onLast(data: MutableList<LastFiveSales>) {

        var first:Float?=0F
        var second:Float?=0F
        var third:Float?=0F
        var fourth:Float?=0F
        var five:Float?=0F
        if (data.size==5){
            first=data.get(0).Total.toFloat()
            second=data.get(1).Total.toFloat()
            third=data.get(2).Total.toFloat()
            fourth=data.get(3).Total.toFloat()
            five=data.get(4).Total.toFloat()
        }
        else if (data.size==4){
            first=data.get(0).Total.toFloat()
            second=data.get(1).Total.toFloat()
            third=data.get(2).Total.toFloat()
            fourth=data.get(3).Total.toFloat()
            five=0F
        }
        else if (data.size==3){
            first=data.get(0).Total.toFloat()
            second=data.get(1).Total.toFloat()
            third=data.get(2).Total.toFloat()
            fourth=0F
            five=0F
        }
        else if (data.size==2){
            first=data.get(0).Total.toFloat()
            second=data.get(1).Total.toFloat()
            third=0F
            fourth=0F
            five=0F
        }
        else if (data.size==1){
            first=data.get(0).Total.toFloat()
            second=0F
            third=0F
            fourth=0F
            five=0F
        }
        else{
            first=0F
            second=0F
            third=0F
            fourth=0F
            five=0F
        }
        var data: BarData? = null
        valueSet1?.clear()
        try {

            data = BarData(
                getXAxisValues(),
                getDataSet(
                    first!!,
                    second!!,
                    third!!,
                    fourth!!,
                    five!!
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        chart!!.data = data
        chart!!.animateXY(1000, 1000)
        chart!!.invalidate()
    }
    private fun getXAxisValues(): java.util.ArrayList<String>? {
        val xAxis = java.util.ArrayList<String>()
        xAxis.add("First")
        xAxis.add("Second")
        xAxis.add("Third")
        xAxis.add("Fourth")
        xAxis.add("Five")
        return xAxis
    }

    private fun getDataSet(
        Total: Float,
        Present: Float,
        Absent: Float,
        Late: Float,
        Leave: Float
    ): java.util.ArrayList<BarDataSet?>? {
        var dataSets: ArrayList<BarDataSet?>? = null
        val v2e1 = BarEntry(Total, 0) // Feb
        valueSet1!!.add(v2e1)
        val v2e2 = BarEntry(Present, 1) // Feb
        valueSet1.add(v2e2)
        val v2e3 = BarEntry(Absent, 2) // Mar
        valueSet1.add(v2e3)
        val v2e4 = BarEntry(Late, 3) // Apr
        valueSet1.add(v2e4)
        val v2e5 = BarEntry(Leave, 4) // May
        valueSet1.add(v2e5)
        val colors = java.util.ArrayList<Int>()
        colors.add(Color.parseColor("#eab259"))
        colors.add(Color.parseColor("#21a839"))
        colors.add(Color.RED)
        colors.add(Color.BLUE)
        colors.add(Color.parseColor("#1daf89"))
        val barDataSet2 = BarDataSet(valueSet1, " Last Five Sales Statistics")
        barDataSet2.colors = colors
        dataSets = java.util.ArrayList()
        dataSets.add(barDataSet2)
        return dataSets
    }

    override fun onCount(customerOrderCount: CustomerOrderCount) {
        tv_pending?.text=customerOrderCount?.Pending?.toString()
        tv_processing?.text=customerOrderCount?.Processing?.toString()
        tv_delivered?.text=customerOrderCount?.Delivered?.toString()
    }

    override fun onProfile(user: Users) {
        switch_status?.isChecked = user?.DeliveryStatus==1
    }

}