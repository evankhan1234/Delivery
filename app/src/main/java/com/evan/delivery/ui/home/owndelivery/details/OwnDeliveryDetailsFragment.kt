package com.evan.delivery.ui.home.owndelivery.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.CustomerOrder
import com.evan.delivery.data.db.entities.CustomerOrderList
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListListener
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListener
import com.evan.delivery.ui.home.customerorder.CustomerOrderAdapter
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.gson.Gson
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class OwnDeliveryDetailsFragment : Fragment(),KodeinAware, ICustomerOrderListListener,
    ICustomerOrderListener {
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var ownDelivery:OwnDelivery?=null
    var token: String? = ""
    var ordersAdapter: OwnDeliveryCustomerOrderAdapter? = null

    var rcv_orders: RecyclerView?=null
    var tv_invoice: TextView?=null
    var tv_discount: TextView?=null
    var tv_total: TextView?=null
    var tv_delivery_charge: TextView?=null
    var tv_total_amount: TextView?=null
    var tv_sub_total: TextView?=null
    var customerOrders: CustomerOrder?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_own_delivery_details, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.customerOrderInformationListener=this
        viewModel.customerOrderListener=this
        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(OwnDelivery::class.java.getSimpleName()) != null) {
                ownDelivery = args?.getParcelable(OwnDelivery::class.java.getSimpleName())

                Log.e("data", "data" + Gson().toJson(ownDelivery!!))

            }
        }
        tv_total_amount=root?.findViewById(R.id.tv_total_amount)
        tv_delivery_charge=root?.findViewById(R.id.tv_delivery_charge)
        rcv_orders=root?.findViewById(R.id.rcv_orders)
        tv_discount=root?.findViewById(R.id.tv_discount)
        tv_total=root?.findViewById(R.id.tv_total)
        tv_invoice=root?.findViewById(R.id.tv_invoice)
        tv_sub_total=root?.findViewById(R.id.tv_sub_total)
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        viewModel.getCustomerOrders(token!!, ownDelivery?.OrderId!!)
        viewModel.getCustomerOrderInformation(token!!,ownDelivery?.OrderId!!.toInt())
        return root
    }

    override fun order(data: MutableList<CustomerOrderList>?) {
        ordersAdapter = OwnDeliveryCustomerOrderAdapter(context!!, data!!)
        rcv_orders?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = ordersAdapter
        }
    }

    override fun onStarted() {

    }

    override fun onEnd() {

    }

    override fun onShow(customerOrder: CustomerOrder?) {
        tv_invoice?.text=customerOrder?.InvoiceNumber
        tv_discount?.text=customerOrder?.Discount+" ট"
        tv_total?.text=customerOrder?.Total+" ট"
        tv_delivery_charge?.text=customerOrder?.DeliveryCharge+" ট"

        customerOrders=customerOrder

        var sub:Double?
        var minus:Double?
        val sub_total=customerOrder?.Total?.toDouble()!!+customerOrder?.DeliveryCharge?.toDouble()!!
        minus=customerOrder?.Total?.toDouble()!!+customerOrder?.Discount?.toDouble()!!
        sub=sub_total.toDouble()
        val number2digits: Double = String.format("%.2f", sub).toDouble()
        val number2digitsForSubTotal: Double = String.format("%.2f", minus).toDouble()
        tv_total_amount?.text=number2digits.toString()+" ট"
        tv_sub_total?.text=number2digitsForSubTotal.toString()+" ট"
    }

    override fun onEmpty() {

    }


}