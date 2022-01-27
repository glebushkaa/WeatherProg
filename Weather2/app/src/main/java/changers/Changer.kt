package changers

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.example.weather.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class Changer {

    fun setCurrentWeatherIcon(iconName: String, imageView: ImageView, context: Context) {
        when (iconName) {
            "01d", "01n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_1)
            "02d", "02n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_2)
            "03d", "03n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_3)
            "04d", "04n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_4)
            "09d", "09n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_9)
            "10d", "10n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_10)
            "11d", "11n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_11)
            "13d", "13n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_13)
            "50d", "50n" -> imageView.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_50)
        }
    }

    fun setFragmentIcon(iconName: String, button: MaterialButton, context: Context) {
        when (iconName) {
            "rain" -> button.icon = AppCompatResources.getDrawable(context, R.drawable.rain)
            "snow" -> button.icon = AppCompatResources.getDrawable(context, R.drawable.snow)
            "partly-cloudy-day" -> button.icon = AppCompatResources.getDrawable(
                context,
                R.drawable.cloudy
            )
            "cloudy" -> button.icon = AppCompatResources.getDrawable(context, R.drawable.cloudy)
            "clear-day" -> button.icon = AppCompatResources.getDrawable(context, R.drawable.sun)
            "wind" -> button.icon = AppCompatResources.getDrawable(context, R.drawable.windy)
        }
    }

}