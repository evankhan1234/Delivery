package com.evan.delivery.ui.home.order

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.evan.delivery.data.db.entities.Delivery
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.ui.home.delivery.DeliveryDataSource

class OrdersSourceFactory (private var deliveryListDataSource: OrdersDataSource) :
    DataSource.Factory<Int, Orders>() {

    val mutableLiveData: MutableLiveData<OrdersDataSource> = MutableLiveData()
    override fun create(): DataSource<Int, Orders> {
        mutableLiveData.postValue(deliveryListDataSource)
        return deliveryListDataSource
    }
}