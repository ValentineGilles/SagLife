package com.example.saglife.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun getDescriptionFromUid(uid: String, onSuccess: (String) -> Unit) {
    // Référence à la collection "users" dans Firestore
    println("getProfilePicFromUid")
    val db = Firebase.firestore
    val usersCollection = db.collection("users")

    // DocumentReference pour l'utilisateur spécifique avec l'UID
    val userDocument = usersCollection.document(uid)

    // Lire les données du document
    userDocument.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Le document existe, récupérer le nom d'utilisateur
                val description = documentSnapshot.getString("description")
                if ( description != null) {
                    println("La description est est : $description")
                    onSuccess(description)
                } else {
                    println("description est null pour l'UID: $uid")
                    onSuccess("")
                }
            } else {
                onSuccess("")
            }
        }
        .addOnFailureListener { exception ->
            println("Erreur lors de la récupération de la description: $exception")
            onSuccess("")
        }
}