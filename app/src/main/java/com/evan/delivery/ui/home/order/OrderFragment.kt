package com.evan.delivery.ui.home.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.ui.home.delivery.DeliveryAdapter
import com.evan.delivery.util.NetworkState
import com.evan.delivery.util.SharedPreferenceUtil
import com.evan.delivery.util.hide
import com.evan.delivery.util.show


import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class OrderFragment : Fragment(),KodeinAware,IOrderUpdateListener {
    override val kodein by kodein()
    var progress_bar: ProgressBar?=null
    var rcv_orders: RecyclerView?=null
    private val factory : OrdersModelFactory by instance()
    private lateinit var viewModel: OrdersViewModel
    var ordersAdapter:OrderListAdapter?=null
    var token:String?=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_order, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(OrdersViewModel::class.java)
        progress_bar=root?.findViewById(R.id.progress_bar)
        rcv_orders=root?.findViewById(R.id.rcv_orders)
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)

        return root
    }
    fun replace(){
        viewModel.replaceSubscription(this)
        startListening()
    }
    override fun onResume() {
        super.onResume()
        // viewModel.getCategoryType(token!!)
        Log.e("stop","stop")
        initAdapter()
        initState()
    }

    private fun initAdapter() {
        ordersAdapter = OrderListAdapter(context!!,this)
        rcv_orders?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcv_orders?.adapter = ordersAdapter
        startListening()
    }

    private fun startListening() {

        viewModel.listOfAlerts?.observe(this, Observer {
            ordersAdapter?.submitList(it)
        })

    }


    private fun initState() {
        viewModel.getNetworkState().observe(this, Observer { state ->
            when (state.status) {
                NetworkState.Status.LOADIND -> {
                    progress_bar?.visibility=View.VISIBLE
                }
                NetworkState.Status.SUCCESS -> {
                    progress_bar?.visibility=View.GONE
                }
                NetworkState.Status.FAILED -> {
                    progress_bar?.visibility=View.GONE
                }
            }
        })
    }
//    override fun onResume() {
//        super.onResume()
//        viewModel.getOrders(token!!)
//    }
//    override fun order(data: MutableList<Orders>?) {
//        ordersAdapter = OrdersAdapter(context!!,data!!,this)
//        rcv_orders?.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
//            setHasFixedSize(true)
//            adapter = ordersAdapter
//        }
//    }
//
//    override fun onStarted() {
//        progress_bar?.show()
//    }
//
//    override fun onEnd() {
//        progress_bar?.hide()
//    }

    override fun onUpdate(orders: Orders) {
        if (activity is HomeActivity) {
            (activity as HomeActivity).goToViewDeliveryFragment(orders)
        }

    }

}
