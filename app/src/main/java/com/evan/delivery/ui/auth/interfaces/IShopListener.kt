package com.evan.delivery.ui.auth.interfaces

import com.evan.delivery.data.db.entities.Shop

interface IShopListener {
    fun onShow(shop: Shop)
}