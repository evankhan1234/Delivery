package com.evan.delivery.ui.home.owndelivery

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.ui.home.delivery.DeliveryDataSource

class OwnDeliverySourceFactory (private var deliveryListDataSource: OwnDeliveryDataSource) :
    DataSource.Factory<Int, OwnDelivery>() {

    val mutableLiveData: MutableLiveData<OwnDeliveryDataSource> = MutableLiveData()
    override fun create(): DataSource<Int, OwnDelivery> {
        mutableLiveData.postValue(deliveryListDataSource)
        return deliveryListDataSource

    }
}