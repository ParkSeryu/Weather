package com.parkseryu.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("timezone") val timezone: String,
    @SerializedName("daily") val daily: ArrayList<Daily>
)

data class Daily(
    @SerializedName("dt") val dt: Long,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pop") val pop: Double,

    @SerializedName("temp") val temp: Temp,
    @SerializedName("weather") val weather: ArrayList<Weather>
)

data class Temp(
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double
)

data class Weather(
    @SerializedName("icon") val icon: String,
    @SerializedName("main") val main: String,

    )

