package com.evan.delivery.data.db.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Delivery (
    @SerializedName("Id")
    var Id: Int?,
    @SerializedName("Email")
    var Email: String?,
    @SerializedName("Name")
    var Name: String?,
    @SerializedName("MobileNumber")
    var MobileNumber: String?,
    @SerializedName("Picture")
    var Picture: String?,
    @SerializedName("OrderLatitude")
    var OrderLatitude: Double?,
    @SerializedName("OrderLongitude")
    var OrderLongitude: Double?,
    @SerializedName("InvoiceNumber")
    var InvoiceNumber: String?,
    @SerializedName("DeliveryCharge")
    var DeliveryCharge: String?,
    @SerializedName("Status")
    var Status: Int?,
    @SerializedName("Created")
    var Created: String?,
    @SerializedName("OrderDetails")
    var OrderDetails: String?,
    @SerializedName("CustomerId")
    var CustomerId: String?,
    @SerializedName("ShopId")
    var ShopId: Int?

):Parcelable