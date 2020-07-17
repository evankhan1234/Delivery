package com.evan.delivery.ui.home.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.evan.delivery.data.repositories.UserRepository

class DeliveryModelFactory (
    private val repository: UserRepository, private val sourceFactory: DeliverySourceFactory
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeliveryViewModel(repository,sourceFactory) as T
    }
}