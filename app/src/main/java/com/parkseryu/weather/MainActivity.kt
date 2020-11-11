package com.parkseryu.weather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var lat : String
    private lateinit var lon : String
    var exclude = "current,minutely,hourly,alerts"
    var KEY = "c212b40268f3fc879961a0ab9afa2466"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btnRetrieve.setOnClickListener {
            getLocation()
        }
    }

    fun getLocation() {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled : Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled : Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                   ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
                return
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
                return
            }
        }

        when{
            isGPSEnabled -> {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lat = location?.latitude.toString()
                lon = location?.longitude.toString()
            }
            isNetworkEnabled -> {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lat = location?.latitude.toString()
                lon = location?.longitude.toString()
            }
            else ->  Log.d("ttest", "error")
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1F, gpsLocationListener)
        Log.d("ttest", "$lat, $lon")
        getWeather()
    }

    val gpsLocationListener = LocationListener { location ->
        lat = location.latitude.toString()
        lon = location.longitude.toString()
    }

    fun getWeather() {

        val data: MutableList<ItemRecycler> = mutableListOf()
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit()
            .getWeather(lat, lon, exclude, KEY)
        res.enqueue(object : Callback<JsonObject> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObj = JSONObject(response.body().toString())
                val location = jsonObj.getString("timezone")
                Log.d("test", "$jsonObj")
                val jArrayDaily = jsonObj.getJSONArray("daily")
                for (i in 0 until jArrayDaily.length()) {
                    val obj_daily = jArrayDaily.getJSONObject(i)
                    val dt = obj_daily.getLong("dt")
                    val sunrise = obj_daily.getLong("sunrise")
                    val sunset = obj_daily.getLong("sunset")
                    val humidity = obj_daily.getInt("humidity")
                    val pop = round(obj_daily.getDouble("pop") * 100)

                    val temp = obj_daily.getJSONObject("temp")
                    val min = temp.getInt("min") - 273
                    val max = temp.getInt("max") - 273

                    val jArrayWeather = obj_daily.getJSONArray("weather")
                    val obj_weather = jArrayWeather.getJSONObject(0)
                    var main = obj_weather.getString("main")
                    val icon = obj_weather.getString("icon")
                    val iconUrl = "http://openweathermap.org/img/w/" + icon + ".png"


                    // data 바꿔주기
                    var sdf = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
                    val date = sdf.format(dt * 1000L)

                    sdf = SimpleDateFormat("HH 시 mm 분", Locale.ENGLISH)
                    val sdf_sunrise = sdf.format(sunrise * 1000L)
                    val sdf_sunset = sdf.format(sunset * 1000L)

                    when (main) {
                        "Clear" -> main = "맑음"
                        "Clouds" -> main = "흐림"
                        "Rain" -> main = "비"
                        else -> {}
                    }

                    val item =
                        ItemRecycler(
                            date,
                            min,
                            max,
                            humidity,
                            pop,
                            location,
                            main,
                            sdf_sunrise,
                            sdf_sunset,
                            iconUrl
                        )
                    data.add(item)

                    Log.d("testtt", "$i , $date, $min, $max, $sdf_sunset, $icon")
                }

                val adapter = RecyclerViewAdapter()
                adapter.listData = data
                recyclerView.addItemDecoration(DividerItemDecoration(applicationContext,LinearLayout.VERTICAL))
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test fail", "Failure : ${t.message.toString()}")
            }
        })
    }

    data class ItemRecycler(
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
}