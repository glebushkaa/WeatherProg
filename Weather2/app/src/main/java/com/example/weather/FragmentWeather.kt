package com.example.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weather.databinding.FragmentWeatherBinding

class FragmentWeather : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    private val dataModel: DataModel by activityViewModels()
    private lateinit var optionsSetter : OptionsSetter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentWeatherBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        optionsSetter = OptionsSetter()
        binding.apply {
            bToday.setOnClickListener {
            optionsSetter.setAlpha(arrayOf(bAnotherDays,bToday),0.7f,1f)
            dataModel.visibilityInfoTv.value = View.VISIBLE
            dataModel.visibilityInfoFragment.value = View.GONE
        }
            bAnotherDays.setOnClickListener {
            optionsSetter.setAlpha(arrayOf(bAnotherDays,bToday),1f,0.7f)
            dataModel.visibilityInfoTv.value = View.GONE
            dataModel.visibilityInfoFragment.value = View.VISIBLE
            }
        }
        dataModel.clickabilityButtons.observe(this,{
            binding.apply{
                optionsSetter.setClickability(arrayOf(bAnotherDays,bToday),it)
            }
        })

    }

    companion object {
        @JvmStatic fun newInstance() = FragmentWeather()

    }
}