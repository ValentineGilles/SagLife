package com.example.saglife.component.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.example.saglife.models.ForumCommentItem
import com.example.saglife.models.ForumFilterItem
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.util.Date

data class ItemData(
    val icon: Int,
    val author: String,
    val date: String,
    val hour: String,
    val comment: String
)

@SuppressLint("MutableCollectionMutableState", "DiscouragedApi")
@Composable
fun ForumPage(navController: NavHostController, id: String?) {
    val context = LocalContext.current

    var showFullDescription by remember { mutableStateOf(false) }

    /*val dataList = listOf(
        ItemData(R.drawable.ic_profile, "Auteur 1", "01/11/2023", "12:30", "Commentaire 1"),
        ItemData(R.drawable.ic_profile, "Auteur 2", "02/11/2023", "14:45", "Commentaire 2"),
        ItemData(R.drawable.ic_profile, "Auteur 3", "03/11/2023", "16:20", "Commentaire 3")
    )*/

    var forumpost by remember { mutableStateOf(ForumPostItem("", "", Date(), "", "", 0, "")) }

    val db = Firebase.firestore
    if (id != null) {
        db.collection("forum").document(id).get().addOnSuccessListener { document ->
            val date: Date = document.getDate("Date")!!
            val author = document.get("Author").toString()
            val icon = document.get("Icon").toString()
            val title = document.get("Title").toString()
            val nb = document.get("Nb").toString().toIntOrNull() ?: 0
            val description = document.get("Description").toString()

            forumpost = ForumPostItem(document.id, author, date, icon, title, nb, description)
        }
            .addOnFailureListener { e ->
                println("Erreur lors de la récupération des données des posts: $e")
            }
    }

    val date = forumpost.getDay()
    val hour = forumpost.getTime()

    // Recupération des commentaires

    val commentspost = mutableListOf<ForumCommentItem>()
    var CommentsPostList by remember { mutableStateOf(mutableListOf<ForumCommentItem>()) }

    if (id != null) {
        db.collection("forum").document(id).collection("comments")
            .orderBy("Date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val comment_author = document.getString("Author") ?: ""
                    val comment = document.getString("Comment") ?: ""
                    val comment_date = document.getDate("Date") ?: Date()
                    val postcomment = ForumCommentItem(comment_author, comment, comment_date)
                    commentspost.add(postcomment)
                }
                CommentsPostList = commentspost
            }
            .addOnFailureListener { e ->
                println("Erreur lors de la récupération des commentaires : $e")
            }
    }



        Column {

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
                    Text(forumpost.author)
                    Text(
                        text = "$date à $hour",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                )
                {
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
                )
                {
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
            Text(
                text = "Commentaires",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            )

            LazyColumn {
                items(CommentsPostList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, start = 16.dp, bottom = 5.dp)
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
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.author)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Date: ${item.getDay()}, Hour: ${item.getTime()}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = item.comment)
                        }
                    }
                }
            }
        }

}