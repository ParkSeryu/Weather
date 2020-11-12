package com.parkseryu.weather


data class RecyclerItem(
    var date: String,
    var min: Int,
    var max: Int,
    var humidity: Int,
    var pop: Long,
    var location: String,
    var main: String,
    var sunrise: String,
    var sunset: String,
    var iconUrl: String
)