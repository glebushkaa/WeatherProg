package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.ActivityMoreBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt

@DelicateCoroutinesApi
class MoreInformation : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    private var url = ""
    private var dates = Array(4) { "" }
    private var city = ""
    private var key = "T7PEM7NY88TBKV729S2VSLMQA"
    private lateinit var dateTvArray : Array<TextView>
    private lateinit var windTvArray : Array<TextView>
    private lateinit var minMaxTvArray : Array<TextView>
    private lateinit var feelsTvArray : Array<TextView>
    private lateinit var precipTvArray : Array<TextView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.bMore.setOnClickListener{
            Toast.makeText(this,"Work",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            dateTvArray = arrayOf(firstDate,secondDate,thirdDate,fourthDate)
            windTvArray = arrayOf(wind1,wind2,wind3,wind4)
            minMaxTvArray = arrayOf(min1,min2,min3,min4)
            feelsTvArray = arrayOf(feels1,feels2,feels3,feels4)
            precipTvArray = arrayOf(precip1,precip2,precip3,precip4)
        }
        city = intent.getStringExtra("CITY").toString()
        GlobalScope.launch(Dispatchers.IO) {
            doInBackground()
            setInfo()
        }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return true
    }

    private fun doInBackground(){
        url = try {
            URL(
                "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$city?unitGroup=metric&include=days%2Ccurrent&key=$key&contentType=json"
            ).readText(Charsets.UTF_8)
        } catch (e : Exception){
            "" }
    }
    @SuppressLint("SetTextI18n")
    private fun setInfo(){
        try {
            val daysJsonObj = JSONObject(url).getJSONArray("days")
            GlobalScope.launch(Dispatchers.Main) {
                val delimiter = "-"
                var num = 1
                while(num != 5){
                    val date = daysJsonObj.getJSONObject(num).getString("datetime")
                    val dateArray = date.split(delimiter)
                    dates[num-1] = dateArray[2] + "." + dateArray[1]
                    num++
                }
                for((index, tv) in dateTvArray.withIndex()){
                    tv.text = dates[index]
                }
                for((index, tv) in windTvArray.withIndex()){
                    tv.text = "${getString(R.string.wind)}${
                        daysJsonObj.getJSONObject(index+1).getString("windspeed")
                    }${getString(R.string.speed)}"
                }
                for((index, tv) in precipTvArray.withIndex()){
                    tv.text = "${getString(R.string.precip)} ${
                        daysJsonObj.getJSONObject(index+1).getString("precipprob")
                    }%"
                }
                for((index, tv) in minMaxTvArray.withIndex()){
                    tv.text = "${getString(R.string.minMax)} ${
                        daysJsonObj.getJSONObject(index+1).getDouble("tempmin").roundToInt()
                    } °/${
                        daysJsonObj.getJSONObject(index+1).getDouble("tempmax").roundToInt()
                    }°"
                }
                for((index, tv) in feelsTvArray.withIndex()){
                    tv.text = "${getString(R.string.feels)} ${
                        daysJsonObj.getJSONObject(index+1).getDouble("feelslike").roundToInt()
                    } °"
                }
            }
        } catch (e: Exception) {

        }
    }
}