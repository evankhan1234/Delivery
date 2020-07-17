package com.evan.delivery.ui.home.order

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.evan.delivery.data.db.entities.Orders
import com.evan.delivery.data.network.post.DeliveryStatusPost
import com.evan.delivery.data.network.post.PushPost
import com.evan.delivery.data.network.post.TokenPost
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.home.delivery.DeliveryDataSource
import com.evan.delivery.ui.home.delivery.DeliverySourceFactory
import com.evan.delivery.ui.home.delivery.IPushListener
import com.evan.delivery.util.ApiException
import com.evan.delivery.util.Coroutines
import com.evan.delivery.util.NetworkState
import com.evan.delivery.util.NoInternetException
import com.google.gson.Gson

class OrdersViewModel (
    val repository: UserRepository,
    val alertListSourceFactory: OrdersSourceFactory
) : ViewModel() {

    var listOfAlerts: LiveData<PagedList<Orders>>? = null

    private val pageSize = 7

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(true)
            .build()
        listOfAlerts = LivePagedListBuilder<Int, Orders>(alertListSourceFactory, config).build()
    }
    fun replaceSubscription(lifecycleOwner: LifecycleOwner) {
        listOfAlerts?.removeObservers(lifecycleOwner)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        listOfAlerts =
            LivePagedListBuilder<Int, Orders>(alertListSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> =
        Transformations.switchMap<OrdersDataSource, NetworkState>(
            alertListSourceFactory.mutableLiveData,
            OrdersDataSource::networkState
        )



}