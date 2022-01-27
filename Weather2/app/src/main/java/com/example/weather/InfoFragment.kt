package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import changers.Changer
import changers.OptionsSetter
import com.example.weather.databinding.FragmentInfoBinding
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class InfoFragment : Fragment() {
    private lateinit var binding : FragmentInfoBinding
    private val dataModel: DataModel by activityViewModels()
    private val optionsSetter = OptionsSetter()
    private val changer = Changer()
    private var city = ""
    private var recentCity = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataModel.cityName.observe(this,{
            city = it
        })
        dataModel.clickabilityButtons.observe(this,{
            binding.apply{
                optionsSetter.setClickable(arrayOf(bMoreInfo),it)
            }
        })
        binding.bMoreInfo.setOnClickListener{
            val intent = Intent(activity,MoreInformation::class.java).apply {
                putExtra("CITY",city)
                putExtra("RECENT_CITY",recentCity)
            }
           startActivity(intent)
        }
        dataModel.daysInfo.observe(this,{
            binding.apply {
                changer.apply {
                bFirstDay.text = it[0][0]
                setFragmentIcon(it[0][1], bFirstDay,activity!!.applicationContext)
                bSecondDay.text = it[1][0]
                setFragmentIcon(it[1][1], bSecondDay,activity!!.applicationContext)
                bThirdDay.text = it[2][0]
                setFragmentIcon(it[2][1], bThirdDay,activity!!.applicationContext)
                bFourthDay.text = it[3][0]
                setFragmentIcon(it[3][1], bFourthDay,activity!!.applicationContext)
                }
            }
        })
    }
    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }
}