package com.example.saglife.screen.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumFilterItem
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import java.util.Calendar
import java.util.UUID

private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ForumCreatePost(navController: NavHostController) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }

    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }
    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val forumfilter = mutableListOf<ForumFilterItem>()
    var filter_chip by remember { mutableStateOf("") }



    val db = Firebase.firestore

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Créer un nouveau post",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, top= 32.dp)
        )


        TextField(
            shape = RoundedCornerShape(8.dp),
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre du post") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale
                .padding(bottom = 16.dp)
        )

        TextField(
            shape = RoundedCornerShape(8.dp),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description du post") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(200.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterList) { filter ->
                FilterCreatePostChip(
                    onClick = { filterName ->
                        filter_chip = filterName
                        println("Filter chip : $filter_chip")
                    },
                    filter_chip,
                    filter
                )
            }
        }


        Button(
            onClick = {
                // Enregistrez le nouveau post dans la base de données ou effectuez d'autres actions nécessaires
                val newPost = auth.currentUser?.uid?.let {
                    ForumPostItem(
                        id = UUID.randomUUID().toString(),
                        author = it,
                        date = Calendar.getInstance().time,
                        icon = "ic_profile",
                        title = title.text,
                        nb = 0,
                        filter = filter_chip,
                        description = description.text
                    )
                }
                if (filter_chip != "" && description.text.length > 20 && title.text.length > 13) {
                // Enregistrez le nouveau post dans la base de données
                    if (newPost != null) {
                        savePostToDatabase(newPost)
                    }
                // Naviguez vers la page du forum après la création du post
                navController.navigate("forum")
            }},
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale du bouton
        ) {
            Text("Créer le post")
        }
    }

}


// Fonction pour enregistrer un post dans la base de données (remplacez cela par votre logique d'enregistrement réelle)
private fun savePostToDatabase(post: ForumPostItem) {
    // Assurez-vous que l'ID du post est null, car un nouvel ID sera généré lors de l'ajout dans Firestore
    val newPost = mapOf(
        "Date" to post.date,
        "Author" to post.author,
        "Icon" to post.icon,
        "Title" to post.title,
        "Nb" to post.nb,
        "Description" to post.description,
        "Filter" to post.filter,
        // Ajoutez d'autres champs si nécessaire
    )

    // Ajoutez le nouveau post à la collection "forum" de votre base de données Firestore
    val db = Firebase.firestore

    db.collection("forum")
        .add(newPost)
        .addOnSuccessListener { documentReference ->
            println("Post ajouté avec l'ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Erreur lors de l'ajout du post: $e")
        }
}

