package com.example.weather
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import changers.Changer
import changers.OptionsSetter
import com.example.weather.databinding.ActivityMainBinding
import database.DbManager
import internet.InternetChecker
import json.JsonGetter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val dataModel : DataModel by viewModels()
    private var city = ""

    private val optionsSetter = OptionsSetter()
    private val changer = Changer()
    private val internetChecker = InternetChecker()
    private lateinit var json : JsonGetter

    private val db = DbManager(this)

    private var infoArray = arrayListOf("","","","","","","","")

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
        db.openDb()
        if(db.checkIsDbEmpty()){
            var index = 0
                while (index != 8){
                    infoArray[index] = db.readDb()[index]
                    index++
                }
            city = infoArray[0]
                setInfo(true)
            }
        getLastLocation()
        setOnClickListeners()
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
                GlobalScope.launch (Dispatchers.IO) {
                    setInfo(false)
                }
            }
            setVisibilityKeyboard()
            optionsSetter.setVisibilityView(arrayOf(binding.cityField,binding.dark),View.GONE)
            binding.editCity.text = null
            dataModel.clickabilityButtons.value = true
        }
    }
    private fun getLastLocation() {
        if(internetChecker.isOnline(this) && db.checkIsDbEmpty()){
            GlobalScope.launch(Dispatchers.IO) {
                setInfo(false)
            }
        }
        else if (internetChecker.isOnline(this)) {
            setCityFieldVisible()
        }
        else{
            Toast.makeText(this, getString(R.string.turn_on_internet), Toast.LENGTH_LONG)
                .show()
            GlobalScope.launch(Dispatchers.IO){
                while(true) {
                    if (internetChecker.isOnline(this@MainActivity) && db.checkIsDbEmpty()) {
                        setInfo(false)
                        break
                    } else if (internetChecker.isOnline(this@MainActivity) && !db.checkIsDbEmpty()) {
                        setCityFieldVisible()
                        break
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


    private fun setInfo(isDb : Boolean){
        try{
            if(!isDb){
                json = JsonGetter(getString(R.string.lang),city,infoArray[0])
                infoArray = json.setArrayInfo()
                setDatesAndIconsForAnotherDays()
            }
            setTextAndIconsForMainActivity()

            if(!db.checkIsDbEmpty()) db.insertDb(infoArray)
            else db.updateDb(infoArray)
        }
        catch (e : Exception){}
    }
    @SuppressLint("SetTextI18n")
    private fun setTextAndIconsForMainActivity(){
        binding.apply {
            tvCity.text = infoArray[0]
            tvTemp.text = infoArray[1]
            tvFeelsTemp.text = "${getString(R.string.feelsLike)} ${infoArray[2]}"
            bWindSpeed.text = getString(R.string.windSpeed) + infoArray[3] + getString(R.string.speed)
            bPrecipProbability.text = getString(R.string.precip_probability) + infoArray[4] + "%"
            bPressure.text = getString(R.string.pressure) + infoArray[5] + getString(R.string.mbar)
            bHumidity.text = getString(R.string.humidity) + infoArray[6] + "%"
            changer.setCurrentWeatherIcon(infoArray[7],binding.ivTodayWeather,this@MainActivity)
        }
    }

    private fun setDatesAndIconsForAnotherDays(){
        try{
            json = JsonGetter(getString(R.string.lang),city,infoArray[0])
            daysDates = json.setIconsAndTextDays()
            GlobalScope.launch(Dispatchers.Main) {
                dataModel.daysInfo.value = daysDates
                dataModel.cityName.value = city
                dataModel.recentCity.value = infoArray[0]
            }
        }
        catch (e : Exception){}
    }

    private fun setCityFieldVisible() {
        GlobalScope.launch(Dispatchers.Main) {
            optionsSetter.setVisibilityView(
                arrayOf(
                    binding.dark,
                    binding.cityField
                ), View.VISIBLE
            )
            setVisibilityKeyboard()
        }
    }

    private fun setVisibilityKeyboard() =
        (this.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)!!
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

