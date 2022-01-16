package com.example.weather

import android.view.View
import android.widget.Button

class OptionsSetter {
    fun setVisibilityView(array: Array<View>,visibility : Int){
        for(n in array){
            n.visibility = visibility
        }
    }

    fun setClickability(array: Array<Button>,clickability : Boolean){
        for(b in array){
            b.isClickable = clickability
        }
    }

    fun setAlpha(array: Array<Button>,firstAlpha : Float,lastAlpha : Float){
        array[0].alpha = firstAlpha
        array[1].alpha = lastAlpha
    }
}