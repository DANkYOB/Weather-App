package com.mriganka.weatherapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mriganka.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Guwahati")

        searchCity()

    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "29850b7527d050c02c299cacadeaf5bb", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Min Temp : $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"


                    changeBGAccordingToWeatherCondition(condition)


                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeBGAccordingToWeatherCondition(conditions: String) {
        when (conditions){

            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.ic_sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Party Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.ic_colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Rain","Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.ic_rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Snow","Light Snow", "Moderate Snow","Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.ic_snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.ic_sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }


    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}