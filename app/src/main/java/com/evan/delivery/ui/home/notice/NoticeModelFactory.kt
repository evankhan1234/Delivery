package com.evan.delivery.ui.home.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evan.delivery.data.repositories.UserRepository


class NoticeModelFactory (
    private val repository: UserRepository, private val sourceFactory: NoticeSourceFactory
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoticeViewModel(repository,sourceFactory) as T
    }
}