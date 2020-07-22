package com.evan.delivery.data.network.post

import com.google.gson.annotations.SerializedName

class UserUpdatePost  (
    @SerializedName("Name")
    val Name: String?,
    @SerializedName("NID")
    val NID: String?,
    @SerializedName("Picture")
    val Picture: String?,
    @SerializedName("Gender")
    val Gender: Int?
)