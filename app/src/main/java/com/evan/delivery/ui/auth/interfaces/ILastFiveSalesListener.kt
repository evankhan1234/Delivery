package com.evan.delivery.ui.auth.interfaces


import com.evan.delivery.data.db.entities.LastFiveSales

interface ILastFiveSalesListener {

    fun onLast(data: MutableList<LastFiveSales>)
}