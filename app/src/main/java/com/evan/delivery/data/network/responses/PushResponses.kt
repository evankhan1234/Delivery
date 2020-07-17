package com.evan.delivery.data.network.responses

import com.google.gson.annotations.SerializedName

class PushResponses (
    @SerializedName("multicast_id")
    val multicast_id: String?,
    @SerializedName("success")
    val success: String?,
    @SerializedName("failure")
    val failure: String?,
    @SerializedName("canonical_ids")
    val canonical_ids: String?
)