package com.evan.delivery.data.network.responses


import com.evan.delivery.data.db.entities.Users


data class AuthResponse(
    val isSuccessful : Boolean?,
    val message: String?,
    val user: Users?
)