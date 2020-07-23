package com.evan.delivery.ui.home.settings.profile



interface IProfileUpdateListener {
    fun onStarted()
    fun onEnd()
    fun onUser(message: String)
    fun onFailure(message: String)
}