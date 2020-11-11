package com.parkseryu.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(@SerializedName("timezone") val timezone: String,
@SerializedName("daily") val daily: ArrayList<Daily>
)

data class Daily(
    @SerializedName("dt") val dt : Long
)

