package com.evan.delivery.ui.home.owndelivery

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.evan.delivery.data.db.entities.OwnDelivery
import com.evan.delivery.data.network.post.LimitPost
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.util.*
import com.google.gson.Gson

class OwnDeliveryDataSource (val context: Context, val alertRepository: UserRepository) :
    PageKeyedDataSource<Int, OwnDelivery>() {

    var networkState: MutableLiveData<NetworkState> = MutableLiveData()
    var limitPost: LimitPost? = null
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, OwnDelivery>
    ) {
        Coroutines.main {
            networkState.postValue(NetworkState.DONE)

            try {
                networkState.postValue(NetworkState.LOADING)

                limitPost = LimitPost(10, 1)
                val response = alertRepository.getOwnDeliveryPagination(SharedPreferenceUtil.getShared(context!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)!!,limitPost!!)
                Log.e("response","response"+ Gson().toJson(response))
                response.success.let { isSuccessful ->
                    if (isSuccessful!!) {
                        networkState.postValue(NetworkState.DONE)
                        val nextPageKey = 2
                        callback.onResult(response.data!!, null, nextPageKey)
                        return@main
                    } else {
                        networkState.postValue(
                            NetworkState(
                                NetworkState.Status.FAILED,
                                response?.message!!
                            )
                        )
                    }
                }
            } catch (e: ApiException) {

                networkState.postValue(NetworkState(NetworkState.Status.FAILED, e.localizedMessage))
            } catch (e: NoInternetException) {
                networkState.postValue(NetworkState(NetworkState.Status.FAILED, e.localizedMessage))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, OwnDelivery>) {
        Coroutines.main {
            try {
                networkState.postValue(NetworkState.LOADING)

                limitPost = LimitPost(10, params.key)
                val response =
                    alertRepository.getOwnDeliveryPagination(SharedPreferenceUtil.getShared(context!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)!!,limitPost!!)
                response.success.let { isSuccessful ->
                    if (isSuccessful!!) {
                        val nextPageKey =
                            params.key + 1
                        networkState.postValue(NetworkState.DONE)
                        callback.onResult(response.data!!, nextPageKey)
                        return@main
                    } else {
                        networkState.postValue(
                            NetworkState(
                                NetworkState.Status.FAILED,
                                response?.message!!
                            )
                        )

                    }
                }
            } catch (e: ApiException) {
                networkState.postValue(NetworkState(NetworkState.Status.FAILED, e.localizedMessage!!))
            } catch (e: NoInternetException) {
                networkState.postValue(NetworkState(NetworkState.Status.FAILED, e.localizedMessage!!))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, OwnDelivery>) {
    }


}