package com.example.saglife.screen.forum

import ForumCommentCard
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumCommentItem
import com.example.saglife.models.ForumPostItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth

fun insertIntoFirebase(forumId: String, author: String, commentText: String, date: Date) {
    val db = Firebase.firestore

    // Crée un nouvel objet de commentaire avec les données fournies
    val newComment = hashMapOf(
        "Author" to author,
        "Comment" to commentText,
        "Date" to date
    )

    // Ajoute le nouveau commentaire à la collection "comments"
    db.collection("forum")
        .document(forumId)
        .collection("comments")
        .add(newComment)
        .addOnSuccessListener { documentReference ->
            println("Commentaire ajouté avec l'ID : ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Erreur lors de l'ajout du commentaire : $e")
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "DiscouragedApi")
@Composable
fun ForumPage(navController: NavHostController, id: String?) {

    // Initialisation de la base de données Firebase
    val db = Firebase.firestore

    // Etat du post
    var forumpost by remember { mutableStateOf(ForumPostItem("", "", Date(), "", "", 0, "", "")) }

    // Etat du commentaire
    var comment by remember { mutableStateOf(TextFieldValue()) }

    // Etat de chargement des données du post
    var postLoaded by remember { mutableStateOf(false) }

    // Etat de chargement des commentaires
    var commentsLoaded by remember { mutableStateOf(false) }

    // Etat du rafraîchissement
    var refreshing by remember { mutableStateOf(false) }

    // Liste des commentaires
    val commentspost = mutableListOf<ForumCommentItem>()
    var CommentsPostList by remember { mutableStateOf(mutableListOf<ForumCommentItem>()) }

    // Récupération du post au lancement de la page
    LaunchedEffect(id, postLoaded) {
        if (id != null && !postLoaded) {
            db.collection("forum").document(id).get().addOnSuccessListener { document ->
                val date: Date = document.getDate("Date")!!
                val author_id = document.get("Author").toString()
                val icon = document.get("Icon").toString()
                val title = document.get("Title").toString()
                val nb = document.get("Nb").toString().toIntOrNull() ?: 0
                val description = document.get("Description").toString()

                // Affichage des données du post dans la console (à des fins de débogage)
                println("forum : $author_id, $date, $icon, $title, $nb, $description")

                // Création d'un objet ForumPostItem avec les données récupérées
                forumpost =
                    ForumPostItem(document.id, author_id, date, icon, title, nb, "", description)
                postLoaded = true // Indique que les données du post ont été récupérées pour empêcher le refresh automatique
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données du post : $e")
                }
        }
    }

    // Récupération des commentaires au lancement de la page
    LaunchedEffect(id, postLoaded) {
        if (id != null && !postLoaded) {
            db.collection("forum").document(id).collection("comments")
                .orderBy("Date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val comment_author = document.getString("Author") ?: ""
                        val comment_comment = document.getString("Comment") ?: ""
                        val comment_date = document.getDate("Date") ?: Date()
                        val comment_id = document.id

                        // Création d'un objet ForumCommentItem avec les données récupérées
                        val postcomment =
                            ForumCommentItem(comment_id, comment_author, comment_comment, comment_date)

                        commentspost.add(postcomment)
                    }
                    CommentsPostList = commentspost
                    commentsLoaded = true // Indique que les données des commentaires ont été récupérées pour empêcher le refresh automatique
                }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des commentaires : $e")
                }
        }
    }

    // Mise en page de la page du forum
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // Rafraîchissement de la page lorsque l'utilisateur swipe vers le bas
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = {
                refreshing = true
            }
        ) {
            // Gestion de l'effet de rafraîchissement simulé
            LaunchedEffect(refreshing) {
                if (refreshing) {
                    delay(1000) // Simule une attente de 1 seconde
                    refreshing = false
                    postLoaded = false
                    commentsLoaded = false
                }
            }

            // Contenu de la page (LazyColumn)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {
                // Affichage du post principal
                item {
                    ForumPostCard(
                        navController = navController,
                        data = forumpost)
                }

                // Affichage du titre "Commentaires"
                item {
                    Text(
                        text = "Commentaires",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }

                // Affichage des commentaires
                items(CommentsPostList) { item ->
                    ForumCommentCard(navController = navController, postId = id?:"", data = item)
                }
            }

            // Zone de saisie pour ajouter un commentaire
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                // Champ de texte pour ajouter un commentaire
                TextField(
                    label = { Text(text = "Votre commentaire...") },
                    value = comment,
                    onValueChange = { comment = it },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val author = auth.currentUser?.displayName
                            val commentText = comment.text
                            val commentdate =
                                Calendar.getInstance().time

                            if (author != null && id != null) {
                                insertIntoFirebase(
                                    id,
                                    author,
                                    commentText,
                                    commentdate
                                ) // Ajoute le commentaire à la base de données
                            }
                            comment = TextFieldValue("") // Réinitialise le champ de texte
                        }
                    )
                )
                // Bouton d'envoi du commentaire
                IconButton(
                    onClick = {
                        val author = auth.currentUser?.uid
                        val commentText = comment.text
                        val commentdate = Calendar.getInstance().time

                        if (author != null && id != null) {
                            insertIntoFirebase(
                                id,
                                author,
                                commentText,
                                commentdate
                            ) // Ajoute le commentaire à la base de données
                        }
                        comment = TextFieldValue("") // Réinitialise le champ de texte
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Envoyer"
                    )
                }
            }
        }
    }
}

