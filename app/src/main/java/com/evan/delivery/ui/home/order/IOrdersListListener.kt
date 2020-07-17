package com.evan.delivery.ui.home.order

import com.evan.delivery.data.db.entities.Orders

interface IOrdersListListener {
    fun order(data:MutableList<Orders>?)
    fun onStarted()
    fun onEnd()
}