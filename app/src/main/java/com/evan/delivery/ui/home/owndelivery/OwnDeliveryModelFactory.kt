package com.evan.delivery.ui.home.owndelivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.home.delivery.DeliverySourceFactory
import com.evan.delivery.ui.home.delivery.DeliveryViewModel

class OwnDeliveryModelFactory (
    private val repository: UserRepository, private val sourceFactory: OwnDeliverySourceFactory
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OwnDeliveryViewModel(repository,sourceFactory) as T
    }
}