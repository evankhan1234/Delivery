package com.evan.delivery.data.network.post

import com.google.gson.annotations.SerializedName

data class SignUpPost (
    @SerializedName("Email")
    val Email: String?,
    @SerializedName("Password")
    val Password: String?,
    @SerializedName("Name")
    val Name: String?,
    @SerializedName("Picture")
    val Picture: String?,
    @SerializedName("Created")
    val Created: String?,
    @SerializedName("NID")
    val Address: String?,
    @SerializedName("MobileNumber")
    val MobileNumber: String?,
    @SerializedName("Status")
    val Status: Int?,
    @SerializedName("Gender")
    val Gender: Int?,
    @SerializedName("DeliveryStatus")
    val DeliveryStatus: Int?
)