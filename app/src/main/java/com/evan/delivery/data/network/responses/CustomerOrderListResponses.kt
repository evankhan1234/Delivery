package com.evan.delivery.data.network.responses

import com.evan.delivery.data.db.entities.CustomerOrderList
import com.google.gson.annotations.SerializedName

class CustomerOrderListResponses (
    @SerializedName("success")
    val success : Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val data: MutableList<CustomerOrderList>?
)