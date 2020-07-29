package com.evan.delivery.ui.home.owndelivery.details

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.CustomerOrderList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_customer_order_list.view.*
import java.text.SimpleDateFormat

class OwnDeliveryCustomerOrderAdapter (val context: Context, val order: MutableList<CustomerOrderList>?) : RecyclerView.Adapter<OwnDeliveryCustomerOrderAdapter.CustomViewHolder>() {


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
        }
        else if (order?.get(position)?.ReturnProduct==2){
            holder.itemView.btn_delete.setText("Returned")
        }
        else{
            holder.itemView.btn_delete.setText("Done")
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

}