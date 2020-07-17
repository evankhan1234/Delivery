package com.evan.delivery.ui.home.order


import com.evan.delivery.data.db.entities.Orders

interface IOrderUpdateListener {
    fun onUpdate(orders: Orders)
}