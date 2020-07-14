package com.evan.delivery.data.network.responses

data class ImageResponse (
    val success : Boolean?,
    val status: Int?,
    val message: String?,
    val img_address: String?
    )