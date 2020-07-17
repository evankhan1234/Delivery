package com.evan.delivery.ui.home.order

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
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.ui.home.delivery.IDeliveryUpdateListener
import kotlinx.android.synthetic.main.layout_order_list.view.*
import java.text.SimpleDateFormat

class OrderListAdapter(val context: Context, val iOrdersUpdateListener: IOrderUpdateListener) :
    PagedListAdapter<Orders, RecyclerView.ViewHolder>(NewsDiffCallback) {
    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlertViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            (holder as AlertViewHolder).bind(
                context,
                getItem(position),
                position,
                iOrdersUpdateListener
            )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<Orders>() {
            override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
                return oldItem.Id == newItem.Id
            }

            override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(context: Context, orders: Orders?, position: Int, listener: IOrderUpdateListener) {

        if (orders != null) {
            itemView.text_view.setOnClickListener {
                listener.onUpdate(orders)
            }
            Glide.with(context)
                .load(orders?.Picture)
                .into(itemView.img_icon!!)
            itemView.text_name.setText(orders.Name)
            itemView.text_phone_number.setText(orders.MobileNumber)
            itemView.text_email.setText(orders.Email)
            var order_address: String = ""
            var order_area: String = ""

            order_address =
                "<b> <font color=#15507E>Order Address</font> : </b>" + orders.OrderAddress
            order_area = "<b> <font color=#15507E>Order Area </font> : </b>" + orders.OrderArea
            itemView.tv_order_address.text = Html.fromHtml(order_address)
            itemView.tv_order_area.text = Html.fromHtml(order_area)

            itemView.tv_date.text = getStartDate(orders.Created)

            // tv_status
        }
    }

    companion object {
        fun create(parent: ViewGroup): AlertViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_list, parent, false)

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