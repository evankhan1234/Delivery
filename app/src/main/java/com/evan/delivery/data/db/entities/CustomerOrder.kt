package com.evan.delivery.data.db.entities

import com.google.gson.annotations.SerializedName

class CustomerOrder (
    @SerializedName("Discount")
    var Discount: String?,
    @SerializedName("Total")
    var Total: String?,
    @SerializedName("InvoiceNumber")
    var InvoiceNumber: String?,
    @SerializedName("DeliveryCharge")
    var DeliveryCharge: String?
)