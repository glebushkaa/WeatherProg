package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.ActivityMoreBinding
import json.JsonGetter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.roundToInt


@DelicateCoroutinesApi
class MoreInformation : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    private var url = ""
    private lateinit var json : JsonGetter
    private var dates = Array(4) { "" }
    private var city = ""
    private var recentCity = ""
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://www.instagram.com/gle.bushkaa/"))
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        binding.apply {
            dateTvArray = arrayOf(tvDate1,tvDate2,tvDate3,tvDate4)
            windTvArray = arrayOf(tvWind1,tvWind2,tvWind3,tvWind4)
            minMaxTvArray = arrayOf(tvMin1,tvMin2,tvMin3,tvMin4)
            feelsTvArray = arrayOf(tvFeels1,tvFeels2,tvFeels3,tvFeels4)
            precipTvArray = arrayOf(tvPrecip1,tvPrecip2,tvPrecip3,tvPrecip4)
        }
        city = intent.getStringExtra("CITY").toString()
        recentCity = intent.getStringExtra("RECENT_CITY").toString()

        GlobalScope.launch(Dispatchers.IO) {
            setInfo()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun setInfo(){
        try{
            json = JsonGetter(getString(R.string.lang),city,recentCity)
            url = json.getAnotherDaysJson()
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
                setInfoForTv()
        }
        }
        catch(e : Exception){}
    }

    @SuppressLint("SetTextI18n")
    private fun setInfoForTv(){
        val daysJsonObj = JSONObject(url).getJSONArray("days")

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
}