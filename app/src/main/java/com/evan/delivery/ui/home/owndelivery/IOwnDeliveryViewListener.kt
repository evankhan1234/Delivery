package com.evan.delivery.ui.home.owndelivery

import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.db.entities.OwnDelivery

interface IOwnDeliveryViewListener {
    fun onView(ownDelivery: OwnDelivery)
}