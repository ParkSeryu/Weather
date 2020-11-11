package com.parkseryu.weather

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_recycler.view.*

class RecyclerViewAdapter : RecyclerView.Adapter<Holder>(){
    var listData = mutableListOf<MainActivity.ItemRecycler>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = listData.get(position)
        holder.setItem(data)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}

class Holder(itemView : View) : RecyclerView.ViewHolder(itemView){

    fun setItem(data : MainActivity.ItemRecycler){
        itemView.tv_dt.text = data.date
        itemView.tv_temp_min.text = "최저기온 : ${data.min}°C"
        itemView.tv_temp_max.text = "최고기온 : ${data.max}°C"
        itemView.tv_humidity.text = "습도 : ${data.humidity}%"
        itemView.tv_pop.text = "강수확률 : ${data.pop}% "
        itemView.tv_location.text = "위치정보 : ${data.location}"
        itemView.tv_sunrise.text = "일출시간 : ${data.sunrise} / "
        itemView.tv_sunset.text = "일몰시간 : ${data.sunset}"
        itemView.tv_main.text = data.main
        Glide.with(itemView).load(data.iconUrl).into(itemView.imv)
    }
}