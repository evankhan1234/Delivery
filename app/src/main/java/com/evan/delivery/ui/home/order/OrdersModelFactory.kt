package com.evan.delivery.ui.home.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.home.delivery.DeliverySourceFactory
import com.evan.delivery.ui.home.delivery.DeliveryViewModel

class OrdersModelFactory (
    private val repository: UserRepository, private val sourceFactory: OrdersSourceFactory
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OrdersViewModel(repository,sourceFactory) as T
    }
}