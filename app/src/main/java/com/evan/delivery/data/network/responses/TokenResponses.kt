package com.evan.delivery.data.network.responses

import com.google.gson.annotations.SerializedName

class TokenResponses (
    @SerializedName("success")
    val success : Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val data: String?
)