package com.example.saglife.models
import java.util.Date

class EventItem (val id:String,val name : String, val dateStart : Date, val dateEnd : Date, val description : String, val photoPath : String, val filter : String){

    fun getDay(): String{
        return dateStart.date.toString() + " "+ getMonth()
    }

    private fun getMonth() : String{
        return when(dateStart.month){
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
        return dateStart.hours.toString()+"h"+dateStart.minutes.toString().padStart(2, '0')+" - "+dateEnd.hours.toString()+"h"+dateEnd.minutes.toString().padStart(2, '0')
    }



}