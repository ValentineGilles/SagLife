package com.example.saglife.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun getUsernameFromUid(uid: String, onSuccess: (String) -> Unit) {
    // Référence à la collection "users" dans Firestore
    val db = Firebase.firestore
    val usersCollection = db.collection("users")

    // DocumentReference pour l'utilisateur spécifique avec l'UID
    val userDocument = usersCollection.document(uid)

    // Lire les données du document
    userDocument.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Le document existe, récupérer le nom d'utilisateur
                val username = documentSnapshot.getString("username")
                if (username != null) {
                    println("Le nom d'utilisateur est : $username")
                    onSuccess(username)
                } else {
                    println("Le nom d'utilisateur est null pour l'UID: $uid")
                    onSuccess("Utilisateur supprimé")
                }
            } else {
                onSuccess("Utilisateur supprimé")
            }
        }
        .addOnFailureListener { exception ->
            println("Erreur lors de la récupération du nom d'utilisateur: $exception")
            onSuccess("Utilisateur supprimé")
        }
}
