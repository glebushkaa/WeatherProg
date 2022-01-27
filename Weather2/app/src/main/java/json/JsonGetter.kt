package json

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt

@DelicateCoroutinesApi
class JsonGetter(language: String, city_ : String, recentCity_ : String) {
    private var openWeatherJson = ""
    private var visualCroasingJson = ""

    private var lang = language

    private val keyAnotherDays = "T7PEM7NY88TBKV729S2VSLMQA"
    private val keyToday = "c44f941499a24513e5c6558130bb9ec8"

    private var infoArray : ArrayList<String> = arrayListOf("","","","","","","","")

    private var city = city_
    private var recentCity = recentCity_

   private fun getTodayWeatherJson() : String{
        try {
            openWeatherJson = URL(
                "https://api.openweathermap.org/data/2.5/weather?q=$city&lang=$lang&units=metric&appid=$keyToday"
            ).readText(Charsets.UTF_8)
            city = JSONObject(openWeatherJson).getString("name")
        } catch (e: Exception) {
            city = recentCity
        }
        return openWeatherJson
    }

    fun getAnotherDaysJson() : String{
        try {
            visualCroasingJson = URL(
                "https://weather.visualcrossing.com/" +
                        "VisualCrossingWebServices/rest/services/timeline/$city?" +
                        "unitGroup=metric&include=days%2Ccurrent&key=$keyAnotherDays&contentType=json"
            ).readText(Charsets.UTF_8)
        } catch (e: Exception) {
        }
        return  visualCroasingJson
    }

    fun setArrayInfo() : ArrayList<String>{
        getTodayWeatherJson()
        getAnotherDaysJson()

        val mainJsonObj = JSONObject(openWeatherJson).getJSONObject("main")
        val currentJsonObj = JSONObject(visualCroasingJson).getJSONObject("currentConditions")
        val daysJsonObj = JSONObject(visualCroasingJson).getJSONArray("days")

        infoArray[0] = JSONObject(openWeatherJson).getString("name")
        infoArray[1] = "${mainJsonObj.getDouble("temp").roundToInt()}°"
        infoArray[2] = "${mainJsonObj.getDouble("feels_like").roundToInt()}° ®"
        infoArray[3] = currentJsonObj.getString("windspeed")
        infoArray[4] = daysJsonObj.getJSONObject(0).getString("precipprob")
        infoArray[5] = mainJsonObj.getDouble("pressure").roundToInt().toString()
        infoArray[6] = mainJsonObj.getString("humidity")
        infoArray[7] = JSONObject(openWeatherJson)
            .getJSONArray("weather").getJSONObject(0)
            .getString("icon")

        return infoArray
    }

   fun setIconsAndTextDays() : Array<Array<String>>{
       val daysDates = Array(4) { Array(2) {""} }
       val anotherJsonObj = JSONObject(getAnotherDaysJson()).getJSONArray("days")
       var num = 1
       GlobalScope.launch(Dispatchers.Main) {
           while(num != 5){
               val date = anotherJsonObj.getJSONObject(num).getString("datetime")
               val icon = anotherJsonObj.getJSONObject(num).getString("icon")
               val dateArray = date.split('-')
               daysDates[num-1][0] = dateArray[2] + "." + dateArray[1]
               daysDates[num-1][1] = icon
               num++
           }
       }
       return daysDates
   }
}
