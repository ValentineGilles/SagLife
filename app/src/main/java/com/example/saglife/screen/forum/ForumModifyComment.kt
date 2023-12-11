package com.example.saglife.screen.forum

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumModifyComment(navController: NavHostController, post_id: String?, comment_id: String?) {
    // État pour gérer le commentaire modifié
    var comment by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    val db = Firebase.firestore

    // Effect pour charger les données du document correspondant à l'ID du commentaire
    if (post_id != null && comment_id != null) {
        LaunchedEffect(post_id) {
            val documentRef = db.collection("forum").document(post_id).collection("comments")
                .document(comment_id ?: "")

            documentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Remplissez le champ de commentaire avec la valeur du document
                    comment = TextFieldValue(documentSnapshot.getString("Comment") ?: "")
                }
            }.addOnFailureListener { exception ->
                println("Erreur lors de la récupération des données du post : $exception")
                Toast.makeText(context, "Erreur de chargement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Champ de texte pour modifier le commentaire
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Commentaire") },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp)
                .padding(bottom = 16.dp)
                .height(200.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent)
        )

        // Bouton pour mettre à jour le commentaire dans la base de données
        Button(
            onClick = {
                if (post_id != null && comment_id != null) {
                    updateCommentInDatabase(post_id, comment_id, comment.text, context)
                }
                navController.navigateUp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale du bouton
        ) {
            Text("Mettre à jour")
        }
    }
}

// Fonction pour mettre à jour le commentaire dans la base de données Firestore
private fun updateCommentInDatabase(postId: String, commentId: String, updatedComment: String, context : Context) {
    val updatedData = mapOf(
        "Comment" to updatedComment,
    )

    val db = Firebase.firestore


    // Utilisez la référence au document avec l'ID du post pour mettre à jour les champs spécifiques du commentaire
    db.collection("forum")
        .document(postId)
        .collection("comments")
        .document(commentId)
        .update(updatedData)
        .addOnSuccessListener {
            println("Champs du post mis à jour avec succès")
            Toast.makeText(context, "Post mis à jour", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            println("Erreur lors de la mise à jour des champs du post: $e")
            Toast.makeText(context, "Erreur lors de la mise à jour du post", Toast.LENGTH_SHORT).show()
        }
}
