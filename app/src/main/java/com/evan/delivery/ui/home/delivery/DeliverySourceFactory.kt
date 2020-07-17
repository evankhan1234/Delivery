package com.evan.delivery.ui.home.delivery

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

import com.evan.delivery.data.db.entities.Delivery

class DeliverySourceFactory (private var deliveryListDataSource: DeliveryDataSource) :
    DataSource.Factory<Int, Delivery>() {

    val mutableLiveData: MutableLiveData<DeliveryDataSource> = MutableLiveData()
    override fun create(): DataSource<Int, Delivery> {
        mutableLiveData.postValue(deliveryListDataSource)
        return deliveryListDataSource
    }
}