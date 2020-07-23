package com.evan.delivery.ui.home.owndelivery

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.ui.home.delivery.IDeliveryUpdateListener
import com.evan.delivery.ui.home.delivery.IDeliveryViewListener
import kotlinx.android.synthetic.main.layout_own_delivery_list_item.view.*
import java.text.SimpleDateFormat

class OwnDeliveryAdapter (val context: Context, val ownDeliveryViewListener: IOwnDeliveryViewListener) :
    PagedListAdapter<OwnDelivery, RecyclerView.ViewHolder>(NewsDiffCallback) {
    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlertViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            (holder as AlertViewHolder).bind(context, getItem(position), position,ownDeliveryViewListener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<OwnDelivery>() {
            override fun areItemsTheSame(oldItem: OwnDelivery, newItem: OwnDelivery): Boolean {
                return oldItem.Id == newItem.Id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: OwnDelivery, newItem: OwnDelivery): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(context: Context, delivery: OwnDelivery?, position: Int, listener: IOwnDeliveryViewListener) {

        if (delivery != null)
        {

            itemView.text_view.setOnClickListener {
                if(delivery?.Status==0){
                   Toast.makeText(context,"Your Order Already Cancel",Toast.LENGTH_SHORT).show()
                }
                else {
                    listener.onView(delivery!!)
                }

            }

            Glide.with(context)
                .load(delivery?.Picture)
                .into(itemView.img_icon!!)
            itemView.text_name.setText(delivery?.Name)
            itemView.text_phone_number.setText(delivery?.MobileNumber)
            itemView.text_email.setText(delivery?.Email)
            var order_address:String=""
            var order_area:String=""

            order_address = "<b> <font color=#15507E>Invoice Number</font> : </b>"+delivery?.InvoiceNumber
            order_area = "<b> <font color=#15507E>Delivery Charge </font> : </b>"+delivery?.DeliveryCharge+" Tk"
            itemView.tv_order_address.text= Html.fromHtml(order_address)
            itemView.tv_order_area.text= Html.fromHtml(order_area)

            itemView.tv_date.text=getStartDate(delivery?.Created)
            if(delivery?.Status==2){
                itemView.tv_status.setText("Processing")
            }
            else if(delivery?.Status==3){
                itemView.tv_status.setText("Delivered")
            }
            else if(delivery?.Status==0){
                itemView.tv_status.setText("Canceled")
            }
            // tv_status
        }
    }

    companion object {
        fun create(parent: ViewGroup): AlertViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_own_delivery_list_item, parent, false)

            return AlertViewHolder(view)
        }
    }
    fun getStartDate(startDate: String?): String? {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("dd,MMMM yyyy")
        val output: String = formatter.format(parser.parse(startDate!!))
        return output
    }

}