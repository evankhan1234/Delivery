package com.evan.delivery.ui.auth.interfaces

import com.evan.delivery.data.db.entities.Users

interface IProfileListener {
    fun onProfile(user: Users)
}