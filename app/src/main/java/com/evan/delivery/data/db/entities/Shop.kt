package com.evan.delivery.data.db.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Shop (
    @SerializedName("Id")
    var Id: Int?,
    @SerializedName("Name")
    var Name: String?,
    @SerializedName("Address")
    var Address: String?,
    @SerializedName("LicenseNumber")
    var LicenseNumber: String?,
    @SerializedName("Status")
    var Status: Int?,
    @SerializedName("ShopUserId")
    var ShopUserId: Int?,
    @SerializedName("Latitude")
    var Latitude: Double?,
    @SerializedName("Longitude")
    var Longitude: Double?
):Parcelable