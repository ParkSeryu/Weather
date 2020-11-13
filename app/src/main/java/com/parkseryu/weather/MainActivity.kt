package com.parkseryu.weather

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var lat: String
    private lateinit var lon: String
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
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    100
                )
                return
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    100
                )
                return
            }
        }

        when {
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
            else -> Log.d("ttest", "error")
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            1F,
            gpsLocationListener
        )
        Log.d("ttest", "$lat, $lon")
        val call: Call<WeatherResponse> =
            RetrofitClient.getInstance().buildRetrofit().getWeather(lat, lon, exclude, KEY)
        val asynchronous = Asynchronous()
        asynchronous.execute(call)

    }

    val gpsLocationListener = LocationListener { location ->
        lat = location.latitude.toString()
        lon = location.longitude.toString()
    }


    inner class Asynchronous : AsyncTask<Call<WeatherResponse>, Void, Boolean>() {
        val data = mutableListOf<RecyclerItem>()

        val progressDialog = ProgressDialog(this@MainActivity)

        override fun onPreExecute() {
            progressDialog.setMessage("Loading...")
            progressDialog.setCancelable(true)
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Call<WeatherResponse>?): Boolean {
            val call = params[0]
            val weatherResponse = call?.execute()?.body()
            try { Log.d("what", "${weatherResponse!!.daily.toString().length}")
                for (i in 0..7) {
                    val dt = weatherResponse!!.daily[i].dt
                    val location = weatherResponse.timezone
                    val sunrise_o = weatherResponse.daily[i].sunrise
                    val sunset_o = weatherResponse.daily[i].sunset
                    val humidity = weatherResponse.daily[i].humidity
                    val pop = round(weatherResponse.daily[i].pop * 100)
                    val min = (weatherResponse.daily[i].temp.min - 273.15).roundToInt()
                    val max = (weatherResponse.daily[i].temp.max - 273.15).roundToInt()
                    val icon = weatherResponse.daily[i].weather[0].icon
                    var main = weatherResponse.daily[i].weather[0].main
                    val iconUrl = "http://openweathermap.org/img/w/" + icon + ".png"
                    val sdf = SimpleDateFormat("yyyy/MM/dd")

                    val date = sdf.format(dt * 1000L)

                    val sdf2 = SimpleDateFormat("HH시 mm분 ")
                    val sunrise = sdf2.format(sunrise_o * 1000L)
                    val sunset = sdf2.format(sunset_o * 1000L)


                    when (main) {
                        "Clear" -> main = "맑음"
                        "Clouds" -> main = "흐림"
                        "Rain" -> main = "비"
                        else -> {
                        }
                    }

                    val item = RecyclerItem(
                        date,
                        min,
                        max,
                        humidity,
                        pop,
                        location,
                        main,
                        sunrise,
                        sunset,
                        iconUrl
                    )
                    data.add(item)

                    Log.d("test", "$date, $min, $main")
                }
                return true
            } catch (e: Exception) {
                return false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result == true) {
                val adapter = RecyclerViewAdapter()
                adapter.listData = data

                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.adapter = adapter
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        applicationContext,
                        R.drawable.line_divider,
                        0,
                        0
                    )
                )
            } else {
                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_LONG).show()
            }


        }
    }

}


///비동기식
/*  call.enqueue(object : Callback<WeatherResponse> {
      override fun onResponse(
          call: Call<WeatherResponse>,
          response: Response<WeatherResponse>
      ) {
          val weatherResponse = response.body()
          for (i in 0..7) {
              val dt = weatherResponse!!.daily[i].dt
              val location = weatherResponse.timezone
              val sunrise_o = weatherResponse.daily[i].sunrise
              val sunset_o = weatherResponse.daily[i].sunset
              val humidity = weatherResponse.daily[i].humidity
              val pop = round(weatherResponse.daily[i].pop * 100)
              val min = (weatherResponse.daily[i].temp.min - 273.15).roundToInt()
              val max = (weatherResponse.daily[i].temp.max - 273.15).roundToInt()
              val icon = weatherResponse.daily[i].weather[0].icon
              var main = weatherResponse.daily[i].weather[0].main
              val iconUrl = "http://openweathermap.org/img/w/" + icon + ".png"

              var sdf = SimpleDateFormat("yyyy/MM/dd ", Locale.ENGLISH)
              val date = sdf.format(dt * 1000L)

              sdf = SimpleDateFormat("HH 시 mm 분", Locale.ENGLISH)
              val sunrise = sdf.format(sunrise_o * 1000L)
              val sunset = sdf.format(sunset_o * 1000L)

              when (main) {
                  "Clear" -> main = "맑음"
                  "Clouds" -> main = "흐림"
                  "Rain" -> main = "비"
                  else -> {
                  }
              }

              val item = RecyclerItem(
                  date,
                  min,
                  max,
                  humidity,
                  pop,
                  location,
                  main,
                  sunrise,
                  sunset,
                  iconUrl
              )
              data.add(item)

              Log.d("test", "$date, $min, $main")
          }

          val adapter = RecyclerViewAdapter()
          adapter.listData = data


          recyclerView.layoutManager = LinearLayoutManager(applicationContext)
          recyclerView.adapter = adapter
          recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, R.drawable.line_divider ,0, 0))
      }

      override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
          Log.d("test", "${t.message.toString()}")
      }
  })*/

/// 직렬화없이
/*
val jsonObj = JSONObject(response.body().toString())
val location = jsonObj.getString("timezone")
Log.d("test", "$jsonObj")
val jArrayDaily = jsonObj.getJSONArray("daily")
for (i in 0 until jArrayDaily.length()) {
    val obj_daily = jArrayDaily.getJSONObject(i)
    val dt = obj_daily.getLong("dt")
    val sunrise = obj_daily.getLong("sunset")
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
    val date = "1"

    sdf = SimpleDateFormat("HH 시 mm 분", Locale.ENGLISH)
    val sdf_sunrise = sdf.format(sunrise * 1000L)
    val sdf_sunset = sdf.format(sunset * 1000L)

    when (main) {
        "Clear" -> main = "맑음"
        "Clouds" -> main = "흐림"
        "Rain" -> main = "비"
        else -> {
        }
    }

    val item = RecyclerItem(
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
    Log.d("testtt", "$i , $date, $min, $max, $sdf_sunset, $icon") * /
}*/
