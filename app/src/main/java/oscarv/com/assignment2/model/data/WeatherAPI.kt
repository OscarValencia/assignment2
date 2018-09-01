package oscarv.com.assignment2.model.data

import io.reactivex.Observable
import oscarv.com.assignment2.model.response.WeatherItem
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherAPI {

    //API documentation: https://www.metaweather.com/api/
    //https://www.metaweather.com/api/location/{woeid}/{year}/{month}/{day}/
    @GET("api/location/{woeid}/{year}/{month}/{day}/")
    fun getCityWeatherByDay(
            @Path("woeid") woeid: Int,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int) : Observable<List<WeatherItem>>
}
