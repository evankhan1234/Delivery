package com.evan.delivery.ui.home.notice

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.evan.delivery.data.db.entities.Notice
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.util.NetworkState


class NoticeViewModel (
    val repository: UserRepository,
    val alertListSourceFactory: NoticeSourceFactory
) : ViewModel() {

    var listOfAlerts: LiveData<PagedList<Notice>>? = null
    private val pageSize = 7

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(true)
            .build()
        listOfAlerts = LivePagedListBuilder<Int, Notice>(alertListSourceFactory, config).build()
    }

    fun replaceSubscription(lifecycleOwner: LifecycleOwner) {
        listOfAlerts?.removeObservers(lifecycleOwner)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        listOfAlerts =
            LivePagedListBuilder<Int, Notice>(alertListSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> =
        Transformations.switchMap<NoticeDataSource, NetworkState>(
            alertListSourceFactory.mutableLiveData,
            NoticeDataSource::networkState
        )

}