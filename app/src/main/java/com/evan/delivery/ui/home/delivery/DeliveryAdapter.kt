package com.evan.delivery.ui.home.delivery

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Delivery

import kotlinx.android.synthetic.main.layout_delivery_item_list.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DeliveryAdapter (val context: Context, val iDeliveryUpdateListener: IDeliveryUpdateListener) :
    PagedListAdapter<Delivery, RecyclerView.ViewHolder>(NewsDiffCallback) {
    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlertViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            (holder as AlertViewHolder).bind(context, getItem(position), position,iDeliveryUpdateListener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<Delivery>() {
            override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem.Id == newItem.Id
            }

            override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(context: Context, delivery: Delivery?, position: Int, listener: IDeliveryUpdateListener) {

        if (delivery != null)
        {


            itemView.text_view.setOnClickListener {
                listener.onUpdate(delivery!!)
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
           // tv_status
        }
    }

    companion object {
        fun create(parent: ViewGroup): AlertViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_delivery_item_list, parent, false)

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