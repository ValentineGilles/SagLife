package com.example.saglife.component.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.R
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

    // Initialisation de la base de données
    val db = Firebase.firestore

    // Etat de l'affichage de la description
    var showFullDescription by remember { mutableStateOf(false) }

    // Etat du post
    var forumpost by remember { mutableStateOf(ForumPostItem("", "", Date(), "", "", 0, "", "")) }

    // Etat du commentaire
    var comment by remember { mutableStateOf(TextFieldValue()) }

    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }
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
                val author = document.get("Author").toString()
                val icon = document.get("Icon").toString()
                val title = document.get("Title").toString()
                val nb = document.get("Nb").toString().toIntOrNull() ?: 0
                val description = document.get("Description").toString()

                forumpost =
                    ForumPostItem(document.id, author, date, icon, title, nb, "", description)
                postLoaded = true // Indique que les données ont été récupérées pour empecher le refresh automatique
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données des posts: $e")
                }
        }
    }

    // Modification de la date et heure pour l'affichage
    val date = forumpost.getDay()
    val hour = forumpost.getTime()

    // Recupération des commentaires au lancement de la page
    LaunchedEffect(id, postLoaded) {
        if (id != null && !postLoaded) {
            db.collection("forum").document(id).collection("comments")
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val comment_author = document.getString("Author") ?: ""
                        val comment_comment = document.getString("Comment") ?: ""
                        val comment_date = document.getDate("Date") ?: Date()
                        val postcomment =
                            ForumCommentItem(comment_author, comment_comment, comment_date)
                        commentspost.add(postcomment)
                    }
                    CommentsPostList = commentspost
                    commentsLoaded = true // Indique que les données ont été récupérées pour empecher le refresh automatique
                }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des commentaires : $e")
                }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        // Refresh la page quand on swipe up
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = {
                refreshing = true
            }
        ) {
            LaunchedEffect(refreshing) {
                if (refreshing) {
                    delay(1000) // Simule une attente de 2 secondes
                    refreshing = false
                    postLoaded = false
                    commentsLoaded = false
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {

                // Affichage du post
                item {
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
                                .padding(
                                    start = 30.dp,
                                    end = 30.dp,
                                    top = 30.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_profile),
                                contentDescription = "Forum",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = forumpost.author,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "$date à $hour",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodySmall
                                )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                text = forumpost.title,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 30.dp,
                                    end = 30.dp,
                                    bottom = 30.dp
                                )
                        ) {
                            // Affiche la description complète si elle est inférieure à 500 caractères
                            if (showFullDescription || forumpost.description.length < 500) {
                                Text(
                                    text = forumpost.description,
                                    textAlign = TextAlign.Justify
                                )
                            } else {
                                Text(
                                    text = forumpost.description.substring(
                                        0,
                                        500
                                    ), // Limite la description à 500 caractères
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 30.dp,
                                    end = 30.dp,
                                    bottom = 30.dp
                                )
                        ) {
                            // Affiche le bouton "Voir plus" ou "Voir moins" si la description est supérieure à 500 caractères
                            if (forumpost.description.length > 500) {
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
                    }
                }

                item {
                    // Affichage du titre "Commentaires"
                    Text(
                        text = "Commentaires",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }

                items(CommentsPostList) { item ->
                    // Affichage des commentaires
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
                            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_profile),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.author)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${item.getDay()} à ${item.getTime()}",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = item.comment)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                // Affichage du champ de texte pour ajouter un commentaire
                TextField(
                    label = { Text(text = "Votre commentaire...") },
                    value = comment,
                    onValueChange = { comment = it },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surface),
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
                                insertIntoFirebase(id, author, commentText, commentdate) // Ajoute le commentaire à la base de données
                            }
                            comment = TextFieldValue("") // Réinitialise le champ de texte
                        }
                    )
                )
                // Bouton d'envoi du commentaire
                IconButton(
                    onClick = {
                        val author = auth.currentUser?.displayName
                        val commentText = comment.text
                        val commentdate = Calendar.getInstance().time

                        if (author != null && id != null) {
                            insertIntoFirebase(id, author, commentText, commentdate) // Ajoute le commentaire à la base de données
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


