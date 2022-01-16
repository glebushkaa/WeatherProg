package com.example.weather
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weather.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.*
import java.util.*
import kotlin.math.roundToInt


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val dataModel : DataModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var optionsSetter: OptionsSetter

    private var key = "T7PEM7NY88TBKV729S2VSLMQA"
    private var city = ""
    private var url : String = ""

    private var daysDates = Array(4) { Array(2) {""} }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = null
        addFragment(FragmentWeather.newInstance(),R.id.dayFragment)
        addFragment(InfoFragment.newInstance(),R.id.info_fragment)
        dataModel.visibilityInfoTv.observe(this,{
            binding.infoTv.visibility = it
        })
        dataModel.visibilityInfoFragment.observe(this,{
            binding.infoFragment.visibility = it
        })
        optionsSetter = OptionsSetter()
        setOnClickListeners()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!checkPermissions()) {
            startLocationPermissionRequest()
        }
        else {
            getLastLocation()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.new_city){
            when(binding.cityField.visibility){
                View.VISIBLE->{
                    optionsSetter.setVisibilityView(arrayOf(binding.cityField,binding.dark),View.GONE)
                    dataModel.clickabilityButtons.value = true
                }
                else ->{
                    optionsSetter.setVisibilityView(arrayOf(binding.cityField,binding.dark),View.VISIBLE)
                    dataModel.clickabilityButtons.value = false
                }
            }
            setVisibilityKeyboard()
        }
        return true
    }

    @DelicateCoroutinesApi
    private fun setOnClickListeners(){
        binding.bCity.setOnClickListener{
            if(binding.editCity.text != null){
                city = binding.editCity.text.toString()
                GlobalScope.launch (Dispatchers.IO){
                    doInBackground()
                    setInfo()
                }
            }
            setVisibilityKeyboard()
            optionsSetter.setVisibilityView(arrayOf(binding.cityField,binding.dark),View.GONE)
            binding.editCity.text = null
            dataModel.clickabilityButtons.value = true
        }
    }

    private fun setVisibilityKeyboard(){
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    getLastLocation()
                }
                else -> {
                    View.OnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
            }
        }
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun getCityByLocation(context: Context, longtitude : Double, latitude : Double){
        GlobalScope.launch (Dispatchers.IO){
        val geocoder = Geocoder(context,Locale.ENGLISH)
        val addresses = geocoder.getFromLocation(latitude,longtitude,1)
        if (addresses.size > 0) {
            city = addresses[0].locality
                doInBackground()
                setInfo()
        }
        }
    }

    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    getCityByLocation(
                        this,
                        task.result.longitude,
                        task.result.latitude
                    )
                }
                else {
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(1000L)
                        GlobalScope.launch(Dispatchers.Main) {
                            optionsSetter.setVisibilityView(arrayOf(binding.dark, binding.cityField), View.VISIBLE)
                            setVisibilityKeyboard()
                        }
                    }
                }
            }
    }

    private fun addFragment(fragment : Fragment, id : Int){
        supportFragmentManager
            .beginTransaction()
            .replace(id , fragment)
            .commit()
    }

    companion object {
        private const val TAG = "LocationProvider"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private fun setBackground(iconName : String, layout : ConstraintLayout){
        when(iconName){
            "rain" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.cloudy_back)
            "snow" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.sunny_back)
            "partly-cloudy-day" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.foggy_back)
            "cloudy" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.cloudy_back)
            "clear-day" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.sunny_back)
            "wind" -> layout.background = AppCompatResources.getDrawable(this,R.drawable.foggy_back)
        }
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
            val currentJsonObj = JSONObject(url).getJSONObject("currentConditions")
            val mainJsonObj = JSONObject(url).getJSONArray("days").getJSONObject(0)
            val anotherJsonObj = JSONObject(url).getJSONArray("days")
            GlobalScope.launch(Dispatchers.Main) {
                setBackground(currentJsonObj.getString("icon"),binding.background)
            }
            binding.apply {
                mainText.text = "\t\t\t${currentJsonObj.getDouble("temp").roundToInt()}°"
                bHumidity.text = getString(R.string.humidity) + currentJsonObj.getString("humidity") + "%"
                bWindSpeed.text = getString(R.string.windSpeed) + currentJsonObj.getString("windspeed") + getString(R.string.speed)
                bPrecipProb.text = getString(R.string.precip_probability) + mainJsonObj.getString("precipprob") + "%"
                bPressure.text = getString(R.string.pressure) + + currentJsonObj.getDouble("pressure").roundToInt() + getString(R.string.mbar)
                textView.text = JSONObject(url).getString("address")
                additionalInfo.text = "${getString(R.string.feelsLike)} ${currentJsonObj.getString("feelslike")}° ®"
            }
            GlobalScope.launch(Dispatchers.Main) {
                val delimiter = "-"
                var num = 1
                while(num != 5){
                    val date = anotherJsonObj.getJSONObject(num).getString("datetime")
                    val icon = anotherJsonObj.getJSONObject(num).getString("icon")
                    val dateArray = date.split(delimiter)
                   daysDates[num-1][0] = dateArray[2] + "." + dateArray[1]
                    daysDates[num-1][1] = icon
                    num++
                }
               dataModel.daysInfo.value = daysDates
               dataModel.cityName.value = city
            }
        } catch (e: Exception) {

        }
    }


}

