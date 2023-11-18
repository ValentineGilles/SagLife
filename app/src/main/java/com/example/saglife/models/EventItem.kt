package com.example.saglife.models
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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

    fun toJson(): Map<String, Any> {
        return mapOf(
            "Name" to this.name,
            "Date_start" to this.dateStart,
            "Date_stop" to this.dateEnd,
            "Description" to this.description,
            "Filter" to this.filter,
            "Photo" to this.photoPath,
        )
    }
    fun toFirebase() {
        val db = Firebase.firestore

        db.collection("event")
            .add(toJson())
            .addOnSuccessListener { documentReference ->
                println("Evénement ajouté avec l'ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout de l'événement: $e")
            }
    }


}