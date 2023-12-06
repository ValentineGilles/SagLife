package com.example.saglife.screen.forum

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.saglife.models.ForumFilterItem
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Calendar
import java.util.UUID

private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumCreatePost(navController: NavHostController) {
    // État pour gérer le titre et la description du post
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }

    // État pour le chargement des données des filtres de post
    var postLoaded by remember { mutableStateOf(false) }
    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val forumfilter = mutableListOf<ForumFilterItem>()
    var filter_chip by remember { mutableStateOf("") }

    var selectedImageUris by remember { mutableStateOf<MutableList<Uri>>(mutableListOf()) }


    val db = Firebase.firestore

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        uris?.let {
            selectedImageUris = it.toMutableList()
        }
    }

    // Utilisation de LaunchedEffect pour récupérer les données des filtres de post une seule fois
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
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
                Text(
                    text = "Créer un nouveau post",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
                )
            }
            item {
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre du post") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 300.dp) // Ajuster la largeur maximale
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description du post") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Text(
                    text = "Ajouter un filtre",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterList) { filter ->
                        FilterCreatePostChip(
                            onClick = { filterName ->
                                filter_chip = filterName
                            },
                            filter_chip,
                            filter,
                            ""
                        )
                    }
                }
                Text(
                    text = "Ajouter des images",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedImageUris) { uri ->
                        DisplaySquareImage(uri) {
                            // Callback pour supprimer l'image
                            selectedImageUris =
                                selectedImageUris.filter { it != uri }.toMutableList()
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(shape = RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                        ) {
                            IconButton(
                                onClick = {
                                    imageLauncher.launch("image/*")
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Ajouter une image",
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }


                Button(
                    onClick = {
                        val imagesUploadedUrls = mutableListOf<String>()

                        val storageRef = Firebase.storage.reference

                        val uploadTasks = selectedImageUris.map { uri ->
                            val imageRef = storageRef.child("images/posts/${UUID.randomUUID()}")
                            val uploadTask = imageRef.putFile(uri)

                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                // L'image a été téléchargée avec succès, obtenez l'URL de téléchargement
                                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    val imageUrl = downloadUri.toString()
                                    imagesUploadedUrls.add(imageUrl)

                                    if (imagesUploadedUrls.size == selectedImageUris.size) {
                                        // Toutes les images ont été téléchargées, créez le post avec les URLs d'images
                                        val newPost = auth.currentUser?.uid?.let {
                                            ForumPostItem(
                                                id = UUID.randomUUID().toString(),
                                                author = it,
                                                date = Calendar.getInstance().time,
                                                icon = "ic_profile",
                                                title = title.text,
                                                nb = 0,
                                                filter = filter_chip,
                                                description = description.text,
                                                imageUrls = imagesUploadedUrls
                                            )
                                        }
                                        if (filter_chip != "") {
                                            if (newPost != null) {
                                                savePostToDatabase(newPost)
                                            }
                                            navController.navigate("forum")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Créer le post")
                }

            }
        }
    }
}

// Fonction pour enregistrer un post dans la base de données Firestore
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
        "ImageUrls" to post.imageUrls
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

@Composable
fun DisplaySquareImage(uri: Uri, onDeleteClick: () -> Unit) {
    println("Uri de l'image : $uri")
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(Color.Gray)
    ) {
        Image(
            painter = rememberImagePainter(data = uri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onDeleteClick, // Appel de la fonction de suppression
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Supprimer l'image",
                tint = Color.White
            )
        }
    }
}