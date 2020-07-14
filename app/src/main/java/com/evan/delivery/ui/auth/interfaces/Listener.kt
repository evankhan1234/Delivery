package com.evan.delivery.ui.auth.interfaces

interface Listener {
    fun Success(result: String)
    fun Failure(result: String)
}