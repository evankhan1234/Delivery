package com.evan.delivery.data.db.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Orders (
    @SerializedName("Id")
    var Id: Int?,
    @SerializedName("CustomerId")
    var CustomerId: Int?,
    @SerializedName("OrderAddress")
    var OrderAddress: String?,
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
    var ShopId: Int?
):Parcelable


