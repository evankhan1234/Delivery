package com.evan.delivery.data.db.entities

import com.google.gson.annotations.SerializedName

class CustomerOrderCount  (
    @SerializedName("Pending")
    var Pending: Int?,
    @SerializedName("Processing")
    var Processing: Int?,
    @SerializedName("Delivered")
    var Delivered: Int?
)