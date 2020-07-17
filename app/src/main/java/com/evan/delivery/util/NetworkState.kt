package com.evan.delivery.util

class NetworkState(val status: Status, val msg: String) {

    enum class Status {
        LOADIND,
        SUCCESS,
        FAILED,
        TOTAL


    }

    companion object {
        val LOADING: NetworkState
        val DONE: NetworkState
        val ERROR: NetworkState


        init {
            LOADING = NetworkState(Status.LOADIND, "Loading")
            DONE = NetworkState(Status.SUCCESS, "Done")
            ERROR = NetworkState(Status.FAILED, "Error")

        }
    }
}