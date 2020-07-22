package com.evan.delivery.data.db.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Notice (
    @SerializedName("Id")
    var Id: String?,
    @SerializedName("Title")
    var Title: String?,
    @SerializedName("Content")
    var Content: String?,
    @SerializedName("Image")
    var Image: String?,
    @SerializedName("Created")
    var Created: String?,
    @SerializedName("Status")
    var Status: Int?,
    @SerializedName("Types")
    var Types: Int?
): Parcelable