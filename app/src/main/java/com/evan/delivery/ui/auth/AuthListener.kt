package com.evan.delivery.ui.auth

import com.evan.delivery.data.db.entities.User


interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String)
}