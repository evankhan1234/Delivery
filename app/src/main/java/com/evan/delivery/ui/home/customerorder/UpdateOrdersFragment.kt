package com.evan.delivery.ui.home.customerorder

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.CustomerOrder
import com.evan.delivery.data.db.entities.CustomerOrderList
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListListener
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListener
import com.evan.delivery.ui.custom.CustomDialog
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.gson.Gson
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class UpdateOrdersFragment : Fragment(), KodeinAware,ICustomerOrderListener,ICustomerOrderListListener,ICancelListener,ICancelOrderListener {

    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var ordersAdapter: CustomerOrderAdapter? = null
    var delivery: Delivery? = null
    var token: String? = ""
    var rcv_orders:RecyclerView?=null
    var tv_invoice:TextView?=null
    var tv_discount:TextView?=null
    var tv_total:TextView?=null
    var tv_delivery_charge:TextView?=null
    var tv_total_amount:TextView?=null
    var tv_sub_total:TextView?=null
    var btn_cancel:Button?=null
    var customerOrders: CustomerOrder?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_update_orders, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.customerOrderInformationListener=this
        viewModel.customerOrderListener=this
        viewModel.cancelOrderListener=this
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(Delivery::class.java.getSimpleName()) != null) {
                delivery = args?.getParcelable(Delivery::class.java.getSimpleName())

                Log.e("data", "data" + Gson().toJson(delivery!!))

            }
        }
        btn_cancel=root?.findViewById(R.id.btn_cancel)
        tv_total_amount=root?.findViewById(R.id.tv_total_amount)
        tv_delivery_charge=root?.findViewById(R.id.tv_delivery_charge)
        rcv_orders=root?.findViewById(R.id.rcv_orders)
        tv_discount=root?.findViewById(R.id.tv_discount)
        tv_total=root?.findViewById(R.id.tv_total)
        tv_invoice=root?.findViewById(R.id.tv_invoice)
        tv_sub_total=root?.findViewById(R.id.tv_sub_total)
        viewModel.getCustomerOrders(token!!, delivery?.OrderId!!)
        viewModel.getCustomerOrderInformation(token!!,delivery?.OrderId!!.toInt())

        btn_cancel?.setOnClickListener {
            showDialog()

        }
        return root
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

    override fun order(data: MutableList<CustomerOrderList>?) {
        ordersAdapter = CustomerOrderAdapter(context!!, data!!, this)
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

    override fun onShow(customerOrderList: CustomerOrderList,reason:String) {

       viewModel?.updateReturnOrderStatus(token!!,customerOrderList?.Id!!,1,reason)

        ordersAdapter?.notifyDataSetChanged()
        var sub:Double?
        var total:Double?

        val sub_total=(customerOrders?.Total?.toDouble()!!+customerOrders?.Discount?.toDouble()!!)-(customerOrderList?.Price?.toDouble()!! * customerOrderList?.Quantity!!)
        sub=sub_total.toDouble()


        val number2digits: Double = String.format("%.2f", sub).toDouble()
        total=sub_total-customerOrders?.Discount?.toDouble()!!
        val number2digitsForTotalValue: Double = String.format("%.2f", total).toDouble()
        val total_for=total+customerOrders?.DeliveryCharge?.toDouble()!!
        val number2digitsForTotal: Double = String.format("%.2f", total_for).toDouble()
        viewModel.updateOrderDeliveryStatusAmount(token!!,customerOrderList?.OrderId!!,total)
        if (sub==0.0){
            tv_discount?.text="0 ট"
            tv_total?.text="0 ট"
            tv_total_amount?.text="0 ট"
            tv_delivery_charge?.text="0 ট"
            tv_sub_total?.text="0 ট"
        }
        else{

            tv_discount?.text=customerOrders?.Discount+" ট"
            tv_delivery_charge?.text=customerOrders?.DeliveryCharge+" ট"
            tv_total_amount?.text=number2digitsForTotal.toString()+" ট"
            tv_sub_total?.text=number2digits.toString()+" ট"
            tv_total?.text=number2digitsForTotalValue.toString()+" ট"
        }


    }

    override fun onCancel() {
        if (activity is HomeActivity) {
            (activity as HomeActivity).onBackPressed()
        }
    }
    fun showDialog() {

        val infoDialog = CustomDialog(context!!, R.style.CustomDialogTheme)
        val inflator =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.layout_cancel, null)
        infoDialog.setContentView(v)
        infoDialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnOK = infoDialog.findViewById(R.id.btn_ok) as Button
        val tv_info = infoDialog.findViewById(R.id.tv_info) as TextView
        val btn_success = infoDialog.findViewById(R.id.btn_success) as Button
        val btn_cancel = infoDialog.findViewById(R.id.btn_cancel) as Button

        val et_delivery_details = infoDialog.findViewById(R.id.et_delivery_details) as EditText
        et_delivery_details.visibility=View.GONE
        tv_info.setText("Are you want to cancel order?")
        btnOK.setOnClickListener {
            viewModel.cancelOrderDeliveryStatus(token!!,delivery?.OrderId!!,0)
            viewModel.cancelOrderStatus(token!!,delivery?.OrderId!!,0)
            infoDialog.dismiss()

            //


        }
        btn_success?.setOnClickListener {
            infoDialog.dismiss()
        }
        btn_cancel?.setOnClickListener {
            infoDialog.dismiss()
        }
        infoDialog.show()
    }

}