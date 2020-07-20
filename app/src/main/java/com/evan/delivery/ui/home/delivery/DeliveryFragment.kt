package com.evan.delivery.ui.home.delivery

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.network.post.Push
import com.evan.delivery.data.network.post.PushPost
import com.evan.delivery.ui.custom.CustomDialog
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.NetworkState
import com.evan.delivery.util.SharedPreferenceUtil
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class DeliveryFragment : Fragment() , KodeinAware,IDeliveryUpdateListener,IPushListener,IDeliveryViewListener{
    override val kodein by kodein()

    private val factory : DeliveryModelFactory by instance()
    private lateinit var viewModel: DeliveryViewModel
    var token: String? = ""
    var rcv_search: RecyclerView?=null
    var delivery_Adapter: DeliveryAdapter?=null
    var edit_content: EditText?=null
    var rcv_deliveries: RecyclerView?=null
    var progress_bar: ProgressBar?=null
    var pushPost: PushPost?=null
    var push: Push?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_delivery, container, false)
        rcv_deliveries=root?.findViewById(R.id.rcv_deliveries)
        edit_content=root?.findViewById(R.id.edit_content)
        progress_bar=root?.findViewById(R.id.progress_bar)
        viewModel = ViewModelProviders.of(this, factory).get(DeliveryViewModel::class.java)

        viewModel.pushListener=this
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
        delivery_Adapter = DeliveryAdapter(context!!,this,this)
        rcv_deliveries?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rcv_deliveries?.adapter = delivery_Adapter
        startListening()
    }

    private fun startListening() {

        viewModel.listOfAlerts?.observe(this, Observer {
            delivery_Adapter?.submitList(it)
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

    override fun onUpdate(delivery: Delivery) {
        showDialog(context!!,delivery)
    }

    fun showDialog(mContext: Context, delivery: Delivery) {
        description=delivery?.Name+" your Invoice Number: "+delivery?.InvoiceNumber
        viewModel.getToken(token!!,2,delivery?.CustomerId!!.toString())
        val infoDialog = CustomDialog(mContext, R.style.CustomDialogTheme)
        val inflator =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.layout_delivery_update_status, null)
        infoDialog.setContentView(v)
        infoDialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnOK = infoDialog.findViewById(R.id.btn_ok) as Button
        val linear_delivery = infoDialog.findViewById(R.id.linear_delivery) as LinearLayout
        val btn_success = infoDialog.findViewById(R.id.btn_success) as Button
        val btn_cancel = infoDialog.findViewById(R.id.btn_cancel) as Button
        val et_delivery_charge = infoDialog.findViewById(R.id.et_delivery_charge) as EditText
        val et_delivery_details = infoDialog.findViewById(R.id.et_delivery_details) as EditText
        val switch_status = infoDialog.findViewById(R.id.switch_status) as SwitchCompat
        switch_status?.isChecked = delivery?.Status==3
        if (delivery?.Status==3){
            btn_success?.visibility=View.VISIBLE
            linear_delivery?.visibility=View.GONE
        }

        et_delivery_charge.setText(delivery?.DeliveryCharge.toString())
        et_delivery_details.setText(delivery?.OrderDetails)
        btnOK.setOnClickListener {
            var delivery_charge: Double? = 0.0
            var delivery_charges: String? = ""
            var delivery_details: String? = ""
            delivery_details=et_delivery_details?.text.toString()
            delivery_charges=et_delivery_charge?.text.toString()
            if(delivery_details.isNullOrEmpty() && delivery_charges.isNullOrEmpty() ){
                Toast.makeText(mContext,"All fields Empty", Toast.LENGTH_SHORT).show()
            }
            else if(delivery_details.isNullOrEmpty()){
                Toast.makeText(mContext,"Delivery Details fields Empty", Toast.LENGTH_SHORT).show()
            }
            else if(delivery_charges.isNullOrEmpty()){
                Toast.makeText(mContext,"Delivery Charge fields Empty", Toast.LENGTH_SHORT).show()
            }
            else{
                var status: Int? = 0
                if (switch_status?.isChecked!!){
                    status=3
                }
                else{
                    status=2
                }

                try {
                    delivery_charge=et_delivery_charge?.text.toString().toDouble()
                } catch (e: Exception) {
                    delivery_charge=0.0            }
                Log.e("delivery_charge","delivery_charge"+delivery_charge)
                Log.e("delivery_details","delivery_details"+delivery_details)
                Log.e("delivery_id","delivery_id"+delivery?.Id)
                Log.e("status","status"+status)
                viewModel.updateDeliveryStatus(token!!,delivery?.Id!!,status,delivery_details,delivery_charge!!)

                Handler().postDelayed({
                    replace()
                    push= Push("Orders",description+".Your Order is Delivered")
                    pushPost= PushPost(tokenData,push)
                    viewModel.sendPush("key=AAAAdCyJ2hw:APA91bGF6x20oQnuC2ZeAXsJju-OCAZ67dBpQvaLx7h18HSAnhl9CPWupCJaV0552qJvm1qIHL_LAZoOvv5oWA9Iraar_XQkWe3JEUmJ1v7iKq09QYyPB3ZGMeSinzC-GlKwpaJU_IvO",pushPost!!)
                }, 300)
                infoDialog.dismiss()
            }

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
    var tokenData:String?=""
    var description:String?=""
    override fun onLoad(data: String) {
        tokenData=data
    }

    override fun onView(delivery: Delivery) {
        if (activity is HomeActivity) {
            (activity as HomeActivity).goToUpdateDeliveryFragment(delivery)
        }
    }
}
