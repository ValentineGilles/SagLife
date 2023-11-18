package com.example.saglife.models

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.Date

class MapItem (val id:String, val name : String, val adresse : String, val filter : String, val description : String, val photoPath : String){

    fun toJson(): Map<String, String> {
        return mapOf(
            "Name" to this.name,
            "Adresse" to this.adresse,
            "Description" to this.description,
            "Filter" to this.filter,
            "Photo" to this.photoPath,
        )
    }
    fun toFirebase() {
        val db = Firebase.firestore

        db.collection("map")
            .add(this.toJson())
            .addOnSuccessListener { documentReference ->
                println("Etablissement ajouté avec l'ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout du post: $e")
            }
    }


}