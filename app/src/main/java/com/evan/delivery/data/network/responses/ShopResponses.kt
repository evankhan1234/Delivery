package com.evan.delivery.data.network.responses

import com.evan.delivery.data.db.entities.Shop
import com.google.gson.annotations.SerializedName

class ShopResponses (
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val data: Shop?
)