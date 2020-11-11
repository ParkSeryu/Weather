package com.parkseryu.weather

import com.google.gson.annotations.SerializedName

data class RecyclerItem(
    @SerializedName("dt") var date: String,
    @SerializedName("min")var min: Int,
    @SerializedName("max")var max: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("pop")var pop: Long,
    @SerializedName("location")var location: String,

    @SerializedName("main")var main: String,
    @SerializedName("sunrise")var sunrise: String,
    @SerializedName("sunset")var sunset: String,
    @SerializedName("icon")var iconUrl: String
)