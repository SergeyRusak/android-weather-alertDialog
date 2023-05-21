package com.sergeyrusak.task8


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import androidx.databinding.DataBindingUtil
import com.sergeyrusak.task8.Dialogs.SetDesignFragmentDialog
import com.sergeyrusak.task8.fragments.FragmentWeatherDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergeyrusak.task8.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var designFragmentDialog: SetDesignFragmentDialog

    lateinit var API_KEY: String

    private var _cities = mutableListOf<String>()
    private var _weather = mutableListOf<Weather>()
    private val weatherDataModel: DataModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        Log.d("myLog", "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        designFragmentDialog = SetDesignFragmentDialog()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_layout, FragmentWeatherDetail.newInstance()).commit()

        this.API_KEY = resources.getString(R.string.API_KEY)

        binding.btnAddCity.setOnClickListener {
            if (addCity(binding.inputCityName.text.toString())) {
                this.getWeatherForCity(binding.inputCityName.text.toString())
                binding.inputCityName.setText("")
                this.weatherDataModel.data.value = _weather
            }
        }

        binding.btnUpdateTemp.setOnClickListener {
            this.updateWeatherForAllCities()
            this.weatherDataModel.data.value = _weather
        }

        binding.btnChangeFragment.setOnClickListener {
            this.designFragmentDialog.show(supportFragmentManager, null)
        }
    }

    private fun addCity(text: String): Boolean {
        return if (text == "") {
            Toast.makeText(this, "Имя города не может быть пустым", Toast.LENGTH_SHORT).show()
            false
        } else {
            this._cities.add(text)
            true
        }
    }

    private fun weatherRequest(nameCity: String): Weather {
        val weatherURL  = "https://api.openweathermap.org/data/2.5/weather?q=${nameCity}&appid=${this.API_KEY}&units=metric";
        var data        = ""
        val weather     = Weather()
        weather.city    = nameCity

        lateinit var stream: InputStream
        try {
            stream = URL(weatherURL).getContent() as InputStream
        } catch (e: IOException) {
            weather.city = "Ошибка интернет соединения!"
            Log.e("myLog", e.localizedMessage)
            return  weather
        }

        try {
            data             = Scanner(stream).nextLine() ?: ""
            val jsonObj      = JSONObject(data)
            weather.temp     = jsonObj.getJSONObject("main").getString("temp").let { "$it°" }
            weather.humidity = jsonObj.getJSONObject("main").getString("humidity").let { "$it%" }
            val iconName     = JSONObject(jsonObj.getJSONArray("weather").getString(0)).getString("icon").let { "_${it.subSequence(1, it.length)}" }
            weather.iconId   = resources.getIdentifier(iconName, "drawable", packageName)
        } catch (e: Exception) {
            weather.city = "Ошибка запроса! (проверьте название города)"
            Log.e("myLog", e.localizedMessage)
            return weather
        }

        return weather
    }

    private fun updateWeatherForAllCities() {
        GlobalScope.launch(Dispatchers.IO) {
            weatherDataModel.data.value?.clear()
            _cities.forEach { it -> getWeatherForCity(it) }
        }
    }

    private fun getWeatherForCity(nameCity: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val weatherResp = weatherRequest(nameCity)
            _weather.add(weatherResp)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("myLog", "onPause")

        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)

        val dataCities = Gson().toJson(this._cities)
        val dataWeatherInfo = Gson().toJson(this.weatherDataModel.data.value)
        sharedPreferences.edit().putString("citiesData", dataCities).apply()
        sharedPreferences.edit().putString("weatherInfoData", dataWeatherInfo).apply()
    }

    override fun onResume() {
        super.onResume()
        Log.d("myLog", "onResume")
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val dataCitiesJson = sharedPreferences.getString("citiesData", "")
        try {
            val type: Type = object : TypeToken<MutableList<String>>() {}.type
            this._cities = Gson().fromJson<MutableList<String>>(dataCitiesJson, type)
        } catch (e: Exception) {
            Log.d("myLog", "Error Load Data about cities!")
        }

        // load list with weather for cities

        val dataWeather = sharedPreferences.getString("weatherInfoData", "")

        try {
            val type: Type = object : TypeToken<MutableList<Weather>>() {}.type
            val dataFromStorage = Gson().fromJson<MutableList<Weather>>(dataWeather, type)

            if (dataFromStorage != null) {
                this.weatherDataModel.data.value = dataFromStorage
                _weather = dataFromStorage
            }
        } catch (e: Exception) {
            Log.d("myLog", "Error Load Data about weather!")
        }
    }
}