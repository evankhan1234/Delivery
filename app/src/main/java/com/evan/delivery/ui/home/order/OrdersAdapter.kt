package com.evan.delivery.ui.home.order

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Orders
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_order_list.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrdersAdapter (val context: Context, val order: MutableList<Orders>?, val iOrdersUpdateListener: IOrderUpdateListener) : RecyclerView.Adapter<OrdersAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val cellForRow = inflater.inflate(R.layout.layout_order_list, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        Log.e("getList",""+ Gson().toJson(order));

        return order!!.size
    }

    override fun onBindViewHolder(holder:CustomViewHolder, position: Int) {


        holder.itemView.text_view.setOnClickListener {
            iOrdersUpdateListener.onUpdate(order?.get(position)!!)
        }
        Glide.with(context)
            .load(order?.get(position)?.Picture)
            .into(holder.itemView.img_icon!!)
        holder.itemView.text_name.setText(order?.get(position)?.Name)
        holder.itemView.text_phone_number.setText(order?.get(position)?.MobileNumber)
        holder.itemView.text_email.setText(order?.get(position)?.Email)
        var order_address:String=""
        var order_area:String=""

        order_address = "<b> <font color=#15507E>Order Address</font> : </b>"+order?.get(position)?.OrderAddress
        order_area = "<b> <font color=#15507E>Order Area </font> : </b>"+order?.get(position)?.OrderArea
        holder.itemView.tv_order_address.text= Html.fromHtml(order_address)
        holder.itemView.tv_order_area.text= Html.fromHtml(order_area)

        holder.itemView.tv_date.text=getStartDate(order?.get(position)?.Created)


    }
    fun getStartDate(startDate: String?): String? {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("dd,MMMM yyyy")
        val output: String = formatter.format(parser.parse(startDate!!))
        return output
    }
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}