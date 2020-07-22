package com.evan.delivery.ui.home.owndelivery

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.data.network.post.CustomerOrderPost
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListListener
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderListener
import com.evan.delivery.ui.home.delivery.DeliveryDataSource
import com.evan.delivery.ui.home.delivery.DeliverySourceFactory
import com.evan.delivery.util.ApiException
import com.evan.delivery.util.Coroutines
import com.evan.delivery.util.NetworkState
import com.evan.delivery.util.NoInternetException
import com.google.gson.Gson

class OwnDeliveryViewModel (
    val repository: UserRepository,
    val alertListSourceFactory: OwnDeliverySourceFactory
) : ViewModel() {
    var customerOrderInformationListener: ICustomerOrderListener? = null
    var listOfAlerts: LiveData<PagedList<OwnDelivery>>? = null
    var customerOrderListener: ICustomerOrderListListener? = null
    var customerOrderPost: CustomerOrderPost? = null
    private val pageSize = 7

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(true)
            .build()
        listOfAlerts = LivePagedListBuilder<Int, OwnDelivery>(alertListSourceFactory, config).build()
    }
    fun replaceSubscription(lifecycleOwner: LifecycleOwner) {
        listOfAlerts?.removeObservers(lifecycleOwner)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        listOfAlerts =
            LivePagedListBuilder<Int, OwnDelivery>(alertListSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> =
        Transformations.switchMap<OwnDeliveryDataSource, NetworkState>(
            alertListSourceFactory.mutableLiveData,
            OwnDeliveryDataSource::networkState
        )

    fun getCustomerOrders(token:String,id: Int) {
        customerOrderListener?.onStarted()
        Coroutines.main {
            try {
                customerOrderPost= CustomerOrderPost(id)
                val authResponse = repository.getCustomerOrders(token,customerOrderPost!!)
                customerOrderListener?.order(authResponse?.data!!)
                Log.e("getCustomerOrders", "getCustomerOrders" + Gson().toJson(authResponse))
                customerOrderListener?.onEnd()
            } catch (e: ApiException) {
                customerOrderListener?.onEnd()
            } catch (e: NoInternetException) {
                customerOrderListener?.onEnd()
            }
        }

    }

    fun getCustomerOrderInformation(header: String, orderId: Int) {

        Coroutines.main {
            try {
                customerOrderPost = CustomerOrderPost(orderId)
                Log.e("Search", "Search" + Gson().toJson(customerOrderPost))
                val response = repository.getCustomerOrderInformation(header, customerOrderPost!!)
                Log.e("OrderInformation", "OrderInformation" + Gson().toJson(response))
                if (response.data != null) {
                    customerOrderInformationListener?.onShow(response?.data!!)
                } else {
                    customerOrderInformationListener?.onEmpty()
                }
                //   customerOrderListener?.onShow(response?.data!!)
                Log.e("Search", "Search" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }


}