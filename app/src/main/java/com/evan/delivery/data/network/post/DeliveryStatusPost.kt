package com.evan.delivery.data.network.post

import com.google.gson.annotations.SerializedName

data class DeliveryStatusPost (
    @SerializedName("DeliveryId")
    var DeliveryId: Int?,
    @SerializedName("Status")
    var Status: Int?,
    @SerializedName("OrderDetails")
    var OrderDetails: String?,
    @SerializedName("DeliveryCharge")
    var DeliveryCharge: Double?
)