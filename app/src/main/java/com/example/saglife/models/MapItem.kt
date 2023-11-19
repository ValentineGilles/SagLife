package com.example.saglife.models

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Modèle de données représentant un élément sur la carte.
 *
 * @param id Identifiant unique de l'élément sur la carte.
 * @param name Nom de l'élément.
 * @param adresse Adresse de l'élément.
 * @param filter Filtre associé à l'élément.
 * @param description Description de l'élément.
 * @param photoPath Chemin de l'image associée à l'élément.
 */
class MapItem (val id:String, val name : String, val adresse : String, val filter : String, val description : String, val photoPath : String){

    /**
     * Convertit l'objet MapItem en une structure de données JSON.
     *
     * @return Map représentant les propriétés de l'élément.
     */
    fun toJson(): Map<String, String> {
        return mapOf(
            "Name" to this.name,
            "Adresse" to this.adresse,
            "Description" to this.description,
            "Filter" to this.filter,
            "Photo" to this.photoPath,
        )
    }

    /**
     * Envoie l'élément vers la base de données Firebase.
     */
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