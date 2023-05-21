package com.sergeyrusak.task8.recycleCustomAdapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrusak.task8.Weather
import android.view.LayoutInflater
import com.sergeyrusak.task8.R

class WeatherRecycleAdapterShort(private var weatherList: ArrayList<Weather>) :  RecyclerView.Adapter<WeatherRecycleAdapterShort.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var city = view.findViewById(R.id.cityName) as TextView
        var icon = view.findViewById(R.id.icon) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_short, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.city.text = this.weatherList[position].city
        holder.icon.setImageResource(this.weatherList[position].iconId)
    }

    override fun getItemCount(): Int {
        return this.weatherList.size
    }

    fun updateData(newData: ArrayList<Weather>){
        this.weatherList.clear()
        this.weatherList.addAll(newData)
        notifyDataSetChanged()
    }
}