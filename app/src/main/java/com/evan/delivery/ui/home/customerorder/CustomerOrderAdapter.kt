package com.evan.delivery.ui.home.customerorder

import android.content.Context
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.CustomerOrderList
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.network.post.Push
import com.evan.delivery.data.network.post.PushPost
import com.evan.delivery.ui.custom.CustomDialog

import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_customer_order_list.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomerOrderAdapter (val context: Context, val order: MutableList<CustomerOrderList>?,  val cancelListener:ICancelListener) : RecyclerView.Adapter<CustomerOrderAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val cellForRow = inflater.inflate(R.layout.layout_customer_order_list, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        Log.e("getList",""+ Gson().toJson(order));

        return order!!.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {



        holder.itemView.btn_delete.setOnClickListener {
          //  cancelListener.onId(order?.get(position)!!.Id!!,order?.get(position)!!.Price,position,order?.get(position)!!.Quantity)
            showDialog(context, holder.itemView.btn_delete,cancelListener,order?.get(position)!!)
        }
        Glide.with(context)
            .load(order?.get(position)?.Picture)
            .into(holder.itemView.img_image!!)
       holder.itemView.tv_price.setText("Price: "+order?.get(position)?.Price.toString()+" Tk")
        holder.itemView.tv_product_name.setText(order?.get(position)?.Name)
       holder.itemView.text_quantity.setText(order?.get(position)?.Quantity?.toString())

        holder.itemView.tv_date.text=getStartDate(order?.get(position)?.Created)

        if (order?.get(position)?.ReturnProduct==1){
            holder.itemView.btn_delete.setText("Canceled")
            holder.itemView.btn_delete.setBackgroundColor(context?.resources?.getColor(R.color.black_opacity_40)!!)
            holder.itemView.btn_delete.isClickable=false
            holder.itemView.btn_delete.isEnabled=false
            Toast.makeText(context,"Already Removed",Toast.LENGTH_SHORT).show()
        }


    }
    fun getStartDate(startDate: String?): String? {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("dd,MMMM yyyy")
        val output: String = formatter.format(parser.parse(startDate!!))
        return output
    }
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
    fun showDialog(mContext: Context,remove: Button,cancelListener:ICancelListener,customerOrderList: CustomerOrderList) {

        val infoDialog = CustomDialog(mContext, R.style.CustomDialogTheme)
        val inflator =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflator.inflate(R.layout.layout_cancel, null)
        infoDialog.setContentView(v)
        infoDialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnOK = infoDialog.findViewById(R.id.btn_ok) as Button
        val btn_success = infoDialog.findViewById(R.id.btn_success) as Button
        val btn_cancel = infoDialog.findViewById(R.id.btn_cancel) as Button

        val et_delivery_details = infoDialog.findViewById(R.id.et_delivery_details) as EditText

        btnOK.setOnClickListener {

            var delivery_details: String? = ""
            delivery_details=et_delivery_details?.text.toString()

           if(delivery_details.isNullOrEmpty()){
                Toast.makeText(mContext,"Delivery Details fields Empty", Toast.LENGTH_SHORT).show()
            }

            else{
               remove.setText("Canceled")
               remove.setBackgroundColor(context?.resources?.getColor(R.color.black_opacity_40)!!)
               remove.isClickable=false
               remove.isEnabled=false
               cancelListener.onShow(customerOrderList,et_delivery_details.text.toString())

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
}