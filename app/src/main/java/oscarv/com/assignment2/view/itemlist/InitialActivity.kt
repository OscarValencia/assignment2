package oscarv.com.assignment2.view.itemlist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_initial.*
import kotlinx.android.synthetic.main.city_item.view.*
import org.json.JSONArray
import org.json.JSONException
import oscarv.com.assignment2.R
import oscarv.com.assignment2.model.City
import oscarv.com.assignment2.view.itemdetail.ForecastActivity
import java.io.IOException
import java.util.*

class InitialActivity : AppCompatActivity() {

    private lateinit var mCities: ArrayList<City>
    private lateinit var mAdapter: CitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
        mCities = ArrayList<City>()
        mAdapter = CitiesAdapter(mCities)
        generateCities()

        setupRecyclerView()

    }

    private fun readCitiesJsonFile(): String? {
        var cityString: String? = null
        try {
            val inputStream = assets.open("static_cities.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            cityString = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cityString
    }

    private fun setupRecyclerView() {
        city_list.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        city_list.adapter = mAdapter
    }

    private fun generateCities() {
        val citiesString = readCitiesJsonFile()
        try {
            val citiesJson = JSONArray(citiesString)
            for (i in 0 until citiesJson.length()) {
                val cityJson = citiesJson.getJSONObject(i)
                val city = City(
                        cityJson.getString("city"),
                        cityJson.getString("state"),
                        cityJson.getString("country"),
                        cityJson.getInt("woeid"))
                Log.d("_TAG", "generateCities: " + city.toString())
                mCities.add(city)
            }

            mAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private inner class CitiesAdapter internal constructor(
            private val mCities: ArrayList<City>) :
            RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.city_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (city, state, country,woeid) = mCities[position]
            holder.cityLabel.text ="$city"
            holder.cityLabel.setOnClickListener {goToForecastActivity(woeid)  }
        }

        override fun getItemCount(): Int {
            return mCities.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var cityLabel: TextView = itemView.tv_city
        }
    }

    private fun goToForecastActivity(woeid: Int) {
        val intent : Intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("woeid",woeid)
        startActivity(intent)
    }
}
