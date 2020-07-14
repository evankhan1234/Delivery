package com.evan.delivery.ui.auth


import com.evan.delivery.data.db.entities.Users


interface AuthListener {
    fun onStarted()
    fun onSuccess(user: Users)
    fun onFailure(message: String)
}