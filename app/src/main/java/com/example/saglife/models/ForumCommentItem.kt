package com.example.saglife.models
import java.util.Date
class ForumCommentItem (val comment_id : String, val author_id: String, val comment: String, val date: Date)
{
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
