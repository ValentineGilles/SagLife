package com.example.saglife.models

import java.util.Date

class ForumPostItem(val id:String, val author:String, val date: Date, val icon:String, val title:String, val nb:Int, val filter: String, val description:String, val imageUrls: List<String>) {

    fun getDay(): String{
        return date.date.toString() + " "+ getMonth()
    }

    private fun getMonth() : String{
        return when(date.month){
            0-> "Janv."
            1-> "Févr."
            2-> "Mars"
            3-> "Avr."
            4-> "Mai"
            5-> "Juin"
            6-> "Juil."
            7-> "Août"
            8-> "Sept."
            9-> "Oct."
            10-> "Nov."
            else-> "Déc."
        }
    }

    fun getTime(): String{
        return date.hours.toString()+"h"+date.minutes.toString().padStart(2, '0')
    }
}