package com.example.saglife.models

import java.util.Date

class MapComment (val author: String, val comment : String, val date : Date, val note : Int) {

    fun getDay(): String{
        return date.date.toString() + " "+ getMonth()
    }

    private fun getMonth() : String{
        return when(date.month){
            1-> "Janv."
            2-> "Févr."
            3-> "Mars"
            4-> "Avr."
            5-> "Mai"
            6-> "Juin"
            7-> "Juil."
            8-> "Août"
            9-> "Sept."
            10-> "Oct."
            11-> "Nov."
            else-> "Déc."
        }
    }
}