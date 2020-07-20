package com.evan.delivery.ui.auth.interfaces


import com.evan.delivery.data.db.entities.CustomerOrder
import com.evan.delivery.data.db.entities.CustomerOrderList

interface ICustomerOrderListListener {
    fun order(data:MutableList<CustomerOrderList>?)
    fun onStarted()
    fun onEnd()
}