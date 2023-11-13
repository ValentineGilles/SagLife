package com.example.saglife.models

import java.util.Date

class ForumPostItem(val id:String, val author:String, val date: Date, val icon:String, val title:String, val nb:Int, val filter: String, val description:String) {

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

    fun getTime(): String{
        return date.hours.toString()+"h"+date.minutes.toString().padStart(2, '0')
    }
}