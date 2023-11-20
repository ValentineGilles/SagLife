package com.example.saglife.screen.forum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumFilterItem
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumModifyPost(navController: NavHostController, id: String?) {
    // États pour gérer le titre, la description et le filtre du post modifié
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var filter by remember { mutableStateOf(TextFieldValue()) }

    // État pour le chargement des données du post
    var postLoaded by remember { mutableStateOf(false) }
    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val forumfilter = mutableListOf<ForumFilterItem>()
    var filter_chip by remember { mutableStateOf("") }

    val db = Firebase.firestore

    // Effect pour charger les données du document correspondant à l'ID du post
    LaunchedEffect(id) {
        if (id != null) {
            val documentRef = db.collection("forum").document(id)

            documentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Remplissez les champs de titre, de description et de filtre avec les valeurs du document
                    title = TextFieldValue(documentSnapshot.getString("Title") ?: "")
                    description = TextFieldValue(documentSnapshot.getString("Description") ?: "")
                    filter = TextFieldValue(documentSnapshot.getString("Filter") ?: "")
                } else {
                    // Document non trouvé
                }
            }.addOnFailureListener { exception ->
                println("Erreur lors de la récupération des données du post : $exception")
            }
        }
    }

    // Effect pour charger les données des filtres de post depuis Firestore
    LaunchedEffect(postLoaded) {
        if (!postLoaded) {
            db.collection("filter_forum").get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.get("Name").toString()
                    forumfilter.add(ForumFilterItem(name))
                }
                filterList = forumfilter
                postLoaded = true
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données des filtres de post : $e")
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Champ de texte pour modifier le titre du post
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre du post") },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp)
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent)
        )

        // Champ de texte pour modifier la description du post
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description du post") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent)
        )

        // Liste déroulante de filtres pour le post
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterList) { filter ->
                FilterCreatePostChip(
                    onClick = { filterName ->
                        filter_chip = filterName
                    },
                    filter_chip,
                    filter
                )
            }
        }

        // Bouton pour mettre à jour le post dans la base de données
        Button(
            onClick = {
                if (id != null) {
                    updatePostInDatabase(id, title.text, description.text, filter_chip)
                }
                navController.navigate("forum/$id")
            },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale du bouton
        ) {
            Text("Mettre à jour")
        }
    }
}

// Fonction pour mettre à jour le post dans la base de données Firestore
private fun updatePostInDatabase(postId: String, updatedTitle: String, updatedDescription: String, updatedFilter: String) {
    val updatedData = mapOf(
        "Title" to updatedTitle,
        "Description" to updatedDescription,
        "Filter" to updatedFilter
    )

    val db = Firebase.firestore

    // Utilisez la référence au document avec l'ID du post pour mettre à jour les champs spécifiques du post
    db.collection("forum")
        .document(postId)
        .update(updatedData)
        .addOnSuccessListener {
            println("Champs du post mis à jour avec succès")

        }
        .addOnFailureListener { e ->
            println("Erreur lors de la mise à jour des champs du post: $e")
        }
}
