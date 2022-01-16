package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weather.databinding.FragmentInfoBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class InfoFragment : Fragment() {
    private lateinit var binding : FragmentInfoBinding
    private val dataModel: DataModel by activityViewModels()
    private lateinit var optionsSetter : OptionsSetter
    private var city = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        optionsSetter = OptionsSetter()
        dataModel.cityName.observe(this,{
            city = it
        })
        binding.more.setOnClickListener{
            val intent = Intent(activity,MoreInformation::class.java).apply {
                putExtra("CITY",city)
            }
           startActivity(intent)
        }
        dataModel.daysInfo.observe(this,{
            binding.bFirstDay.text = it[0][0]
            setIcon(it[0][1],binding.bFirstDay)
            binding.bSecondDay.text = it[1][0]
            setIcon(it[1][1],binding.bSecondDay)
            binding.bThirdDay.text = it[2][0]
            setIcon(it[2][1],binding.bThirdDay)
            binding.bFourthDay.text = it[3][0]
            setIcon(it[3][1],binding.bFourthDay)
        })
    }

    private fun setIcon(iconName : String, button : MaterialButton){
        when(iconName){
            "rain" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.rain)
            "snow" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.snow)
            "partly-cloudy-day" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.cloudy)
            "cloudy" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.cloudy)
            "clear-day" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.sun)
            "wind" -> button.icon = AppCompatResources.getDrawable(activity!!.applicationContext,R.drawable.windy)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }
}