package com.evan.delivery.ui.auth.interfaces

interface ISignUpListener {
    fun onStarted()
    fun onSuccess(message:String)
    fun onFailure(message:String)
    fun onEnd()
    fun onEndError()
}