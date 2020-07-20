package com.evan.delivery.ui.auth.interfaces

import com.evan.delivery.data.db.entities.CustomerOrder


interface ICustomerOrderListener {
    fun onShow(customerOrder: CustomerOrder?)
    fun onEmpty()
}