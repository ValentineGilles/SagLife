package com.example.saglife.screen.forum

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.saglife.R
import com.example.saglife.database.getUsernameFromUid
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private val auth = Firebase.auth
@Composable
fun ForumPostCard(navController: NavHostController, data: ForumPostItem) {
    // État pour afficher ou masquer la description complète
    var showFullDescription by remember { mutableStateOf(false) }

    // État pour stocker le nom de l'auteur
    var author by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }

    // Récupération des données du post
    val icon = data.icon
    val title = data.title
    val author_id = data.author
    val date = data.getDay()
    val hour = data.getTime()
    val description = data.description
    val imageUrls = data.imageUrls

    println("data imageUrls : $imageUrls")

    // Récupération du nom de l'auteur en fonction de son ID
    if (author_id != "") {
        getUsernameFromUid(author_id) { username ->
            author = username
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            // Utilisez Box pour permettre à l'utilisateur de fermer la boîte de dialogue en cliquant en dehors de l'image
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showDialog = false },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }


    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône du profil de l'auteur
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "Forum",
                modifier = Modifier.size(20.dp)
            )
            // Nom de l'auteur
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall
            )
            // Date et heure du post
            Text(
                text = "$date à $hour",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            // Titre du post
            Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
        ) {
            // Affiche la description complète si elle est inférieure à 500 caractères ou si l'utilisateur a choisi de la voir
            if (showFullDescription || description.length < 500) {
                Text(
                    text = description,
                    textAlign = TextAlign.Justify
                )
            } else {
                // Limite la description à 500 caractères si elle est trop longue
                Text(
                    text = description.substring(0, 500),
                    textAlign = TextAlign.Justify
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 8.dp)
        ) {
            // Affiche le bouton "Voir plus" ou "Voir moins" si la description est supérieure à 500 caractères
            if (data.description.length > 500) {
                if (showFullDescription) {
                    Text(
                        text = "Voir moins",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { showFullDescription = false }
                            .fillMaxWidth(),
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                } else {
                    Text(
                        text = "Voir plus",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { showFullDescription = true }
                            .fillMaxWidth(),
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
        }

        LazyRow(modifier = Modifier.padding(16.dp)) {
            items(imageUrls) { imageUrl ->
                val cleanedImageUrl = imageUrl.trim().removePrefix("[").removeSuffix("]")
                println("imageUrl : $cleanedImageUrl")
                Image(
                    painter = rememberAsyncImagePainter(cleanedImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                        .clickable {
                            selectedImageUrl = cleanedImageUrl
                            showDialog = true
                        }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
        ) {
            // Affiche le bouton "Modifier" si l'utilisateur actuel est l'auteur du post
            if (author_id == auth.currentUser?.uid) {
                Text(
                    text = "Modifier",
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .clickable {
                            println("id = ${data.id}")
                            navController.navigate("forum/modifypost/${data.id}")
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}
