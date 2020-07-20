package com.evan.delivery.ui.home.delivery

import com.evan.delivery.data.db.entities.Delivery

interface IDeliveryViewListener {
    fun onView(delivery: Delivery)
}