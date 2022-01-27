package com.example.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel : ViewModel() {
    val visibilityInfoTv : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val visibilityInfoFragment : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val clickabilityButtons : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val daysInfo : MutableLiveData<Array<Array<String>>> by lazy{
        MutableLiveData<Array<Array<String>>>()
    }

    val cityName : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    val recentCity : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}

