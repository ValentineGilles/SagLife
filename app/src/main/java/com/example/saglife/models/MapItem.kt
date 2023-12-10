package com.example.saglife.models

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore

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
class MapItem(val id:String, val author_id:String, val name: String, val adresseName: String, val adresseLocation: GeoPoint, val filter: String, val description: String, val photoPath: String, var note: Double, var distance: Float){

    /**
     * Convertit l'objet MapItem en une structure de données JSON.
     *
     * @return Map représentant les propriétés de l'élément.
     */
    fun toJson(): Map<String, Any> {
        return mapOf(
            "Author_id" to this.author_id,
            "Name" to this.name,
            "AdresseName" to this.adresseName,
            "AdresseLocation" to this.adresseLocation,
            "Description" to this.description,
            "Filter" to this.filter,
            "Photo" to this.photoPath,
        )
    }

    /**
     * Envoie l'élément vers la base de données Firebase.
     */
    fun toFirebase(context : Context) {
        val db = Firebase.firestore

        db.collection("map")
            .add(this.toJson())
            .addOnSuccessListener { documentReference ->
                println("Etablissement ajouté avec l'ID: ${documentReference.id}")
                Toast.makeText(context, "Etablissement ajouté", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'ajout du post: $e")
                Toast.makeText(context, "Erreur lors de l'ajout de l'établissement", Toast.LENGTH_SHORT).show()
            }
    }


}