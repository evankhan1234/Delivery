package com.evan.delivery.ui.home.owndelivery

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
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.ui.home.order.OrderListAdapter
import com.evan.delivery.ui.home.order.OrdersModelFactory
import com.evan.delivery.ui.home.order.OrdersViewModel
import com.evan.delivery.util.NetworkState
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.gson.Gson
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class OwnDeliveryFragment : Fragment(),KodeinAware,IOwnDeliveryViewListener {

    override val kodein by kodein()
    var progress_bar: ProgressBar?=null
    var rcv_orders: RecyclerView?=null
    private val factory : OwnDeliveryModelFactory by instance()
    private lateinit var viewModel: OwnDeliveryViewModel
    var ownDeliveryAdapter: OwnDeliveryAdapter?=null
    var token:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_own_delivery, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(OwnDeliveryViewModel::class.java)
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
        ownDeliveryAdapter = OwnDeliveryAdapter(context!!,this)
        rcv_orders?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcv_orders?.adapter = ownDeliveryAdapter
        startListening()
    }

    private fun startListening() {

        viewModel.listOfAlerts?.observe(this, Observer {
            ownDeliveryAdapter?.submitList(it)
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
    override fun onView(ownDelivery: OwnDelivery) {
        if (activity is HomeActivity) {
            (activity as HomeActivity).goToViewOwnDeliveryDetailsFragment(ownDelivery)
        }
    }


}