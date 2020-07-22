package com.evan.delivery.ui.home.customerorder

import com.evan.delivery.data.db.entities.CustomerOrderList

interface ICancelListener {
    fun onShow(customerOrderList: CustomerOrderList,reason:String)
}