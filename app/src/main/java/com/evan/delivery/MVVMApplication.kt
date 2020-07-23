package com.evan.delivery

import android.app.Application
import com.evan.delivery.data.db.AppDatabase
import com.evan.delivery.data.network.MyApi
import com.evan.delivery.data.network.NetworkConnectionInterceptor
import com.evan.delivery.data.network.PushApi
import com.evan.delivery.data.preferences.PreferenceProvider
import com.evan.delivery.data.repositories.QuotesRepository
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.home.delivery.DeliveryDataSource
import com.evan.delivery.ui.home.delivery.DeliveryModelFactory
import com.evan.delivery.ui.home.delivery.DeliverySourceFactory
import com.evan.delivery.ui.home.delivery.DeliveryViewModel
import com.evan.delivery.ui.home.notice.NoticeDataSource
import com.evan.delivery.ui.home.notice.NoticeModelFactory
import com.evan.delivery.ui.home.notice.NoticeSourceFactory
import com.evan.delivery.ui.home.notice.NoticeViewModel
import com.evan.delivery.ui.home.order.OrdersDataSource
import com.evan.delivery.ui.home.order.OrdersModelFactory
import com.evan.delivery.ui.home.order.OrdersSourceFactory
import com.evan.delivery.ui.home.order.OrdersViewModel
import com.evan.delivery.ui.home.owndelivery.OwnDeliveryDataSource
import com.evan.delivery.ui.home.owndelivery.OwnDeliveryModelFactory
import com.evan.delivery.ui.home.owndelivery.OwnDeliverySourceFactory
import com.evan.delivery.ui.home.owndelivery.OwnDeliveryViewModel
import com.evan.delivery.ui.home.profile.ProfileViewModelFactory
import com.evan.delivery.ui.home.quotes.QuotesViewModelFactory

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MVVMApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { PushApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance(), instance()) }
        bind() from singleton { QuotesRepository(instance(), instance(), instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { ProfileViewModelFactory(instance()) }
        bind() from provider { DeliveryModelFactory(instance(), instance()) }
        bind() from provider { NoticeModelFactory(instance(), instance()) }
        bind() from provider { OwnDeliveryModelFactory(instance(), instance()) }
        bind() from provider { OrdersModelFactory(instance(), instance()) }
        bind() from provider { DeliveryViewModel(instance(), instance()) }
        bind() from provider { OwnDeliveryViewModel(instance(), instance()) }
        bind() from provider { OrdersViewModel(instance(), instance()) }
        bind() from provider { NoticeViewModel(instance(), instance()) }
        bind() from provider { DeliveryDataSource(instance(), instance()) }
        bind() from provider { OrdersDataSource(instance(), instance()) }
        bind() from provider { OwnDeliveryDataSource(instance(), instance()) }
        bind() from provider { NoticeDataSource(instance(), instance()) }
        bind() from provider { DeliverySourceFactory(instance()) }
        bind() from provider { OwnDeliverySourceFactory(instance()) }
        bind() from provider { OrdersSourceFactory(instance()) }
        bind() from provider { NoticeSourceFactory(instance()) }
        bind() from provider{ QuotesViewModelFactory(instance()) }


    }

}