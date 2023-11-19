package com.example.saglife.models
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date

/**
 * Modèle de données représentant un événement.
 *
 * @param id Identifiant unique de l'événement.
 * @param name Nom de l'événement.
 * @param dateStart Date de début de l'événement.
 * @param dateEnd Date de fin de l'événement.
 * @param description Description de l'événement.
 * @param photoPath Chemin de l'image associée à l'événement.
 * @param filter Filtre associé à l'événement.
 */
class EventItem (val id:String,val name : String, val dateStart : Date, val dateEnd : Date, val description : String, val photoPath : String, val filter : String){

    /**
     * Obtient le jour du mois et le mois formaté.
     *
     * @return Chaîne représentant le jour du mois et le mois.
     */
    fun getDay(): String{
        return dateStart.date.toString() + " "+ getMonth()
    }

    /**
     * Obtient le mois formaté.
     *
     * @return Chaîne représentant le mois.
     */
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

    /**
     * Obtient l'heure de début et de fin formatée.
     *
     * @return Chaîne représentant l'heure de début et de fin.
     */
    fun getTime(): String{
        return dateStart.hours.toString()+"h"+dateStart.minutes.toString().padStart(2, '0')+" - "+dateEnd.hours.toString()+"h"+dateEnd.minutes.toString().padStart(2, '0')
    }

    /**
     * Convertit l'objet EventItem en une structure de données JSON.
     *
     * @return Map représentant les propriétés de l'événement.
     */
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

    /**
     * Envoie l'événement vers la base de données Firebase.
     */
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