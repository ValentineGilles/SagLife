package com.example.saglife.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun getProfilePicFromUid(uid: String, onSuccess: (String) -> Unit) {
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
                val profile_pic = documentSnapshot.getString("profile_pic")
                if ( profile_pic!= null) {
                    println("La photo est est : $profile_pic")
                    onSuccess(profile_pic)
                } else {
                    println("Photo est null pour l'UID: $uid")
                    onSuccess("")
                }
            } else {
                onSuccess("")
            }
        }
        .addOnFailureListener { exception ->
            println("Erreur lors de la récupération de la photo: $exception")
            onSuccess("")
        }
}