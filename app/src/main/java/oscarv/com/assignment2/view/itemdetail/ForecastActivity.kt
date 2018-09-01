package oscarv.com.assignment2.view.itemdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import oscarv.com.assignment2.R
import oscarv.com.assignment2.R.layout.weather_item
import oscarv.com.assignment2.model.data.WeatherAPI
import oscarv.com.assignment2.model.response.WeatherItem
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ForecastActivity : AppCompatActivity() {

    private lateinit var citiesAdapter: CitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //assign WOEID from intent extra
        val CITY_WOEID = intent.getIntExtra("woeid",0)
        //get default calendar instance, "now" by default
        val today = Calendar.getInstance()
        //add next day to calendar
        today.add(Calendar.DAY_OF_YEAR,1)

        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        citiesAdapter = CitiesAdapter()
        city_list.adapter = citiesAdapter

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://www.metaweather.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val weatherAPI = retrofit.create(WeatherAPI::class.java)
        weatherAPI.getCityWeatherByDay(CITY_WOEID,year,month,day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
//                            Log.i("_TAG",it[0].weatherStateName)
                            citiesAdapter.setCities(it)
                        },
                        {Toast.makeText(applicationContext,it.message,Toast.LENGTH_LONG).show()
                            Log.i("_TAG",it.message)
                })
    }

    inner class CitiesAdapter : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>(){

        private val weatherItems : MutableList<WeatherItem> = mutableListOf()

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            holder.bindModel(weatherItems[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            return CityViewHolder(layoutInflater.inflate(weather_item,parent,false))
        }
        override fun getItemCount(): Int {
            return weatherItems.size
        }

        fun setCities(it: List<WeatherItem>?) {
            if (it != null) {
                weatherItems.addAll(it)
            }
            notifyDataSetChanged()
        }

        inner class CityViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

            val cityName : TextView = itemView.findViewById(R.id.tv_creation)
            val cityTemp : TextView = itemView.findViewById(R.id.tv_temp)
            val weatherHumidity : TextView = itemView.findViewById(R.id.tv_humidity)
            val cityMaxTemp : TextView = itemView.findViewById(R.id.tv_max_temp)
            val cityMinTemp : TextView = itemView.findViewById(R.id.tv_min_temp)
            val weatherState : TextView = itemView.findViewById(R.id.tv_weather_state)
            val weatherPic : ImageView = itemView.findViewById(R.id.iv_weather)

            fun bindModel(weatherResponse: WeatherItem) {
                cityName.text = "Created: ${weatherResponse.created}"
                cityTemp.text = weatherResponse.theTemp.toString()
                weatherHumidity.text = "Humidity: ${weatherResponse.humidity}"
                cityMaxTemp.text = "Max Temp: ${weatherResponse.maxTemp}"
                cityMinTemp.text = "Min Temp: ${weatherResponse.minTemp}"
                weatherState.text = weatherResponse.weatherStateName
                Picasso.get().load(
                        "https://www.metaweather.com/static/img/weather/png/64/${weatherResponse.weatherStateAbbr}.png")
                        .into(weatherPic)

            }

        }
    }


}