package com.evan.delivery.data.db.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OwnDelivery (
    @SerializedName("Id")
    var Id: Int?,
    @SerializedName("CustomerId")
    var CustomerId: Int?,
    @SerializedName("OrderLatitude")
    var OrderLatitude: String?,
    @SerializedName("Created")
    var Created: String?,
    @SerializedName("OrderLongitude")
    var OrderLongitude: String?,
    @SerializedName("OrderArea")
    var OrderArea: String?,
    @SerializedName("MobileNumber")
    var MobileNumber: String?,
    @SerializedName("Email")
    var Email: String?,
    @SerializedName("Picture")
    var Picture: String?,
    @SerializedName("Name")
    var Name: String?,
    @SerializedName("ShopId")
    var ShopId: Int?,
    @SerializedName("OrderId")
    var OrderId: Int?,
    @SerializedName("Status")
    var Status: Int?,
    @SerializedName("InvoiceNumber")
    var InvoiceNumber: String?,
    @SerializedName("OrderDetails")
    var OrderDetails: String?,
    @SerializedName("DeliveryCharge")
    var DeliveryCharge: String?
): Parcelable
