package com.evan.delivery.ui.auth.interfaces

import com.evan.delivery.data.db.entities.CustomerOrderCount


interface ICustomerOrderCountListener {
    fun onCount(customerOrderCount: CustomerOrderCount)
}