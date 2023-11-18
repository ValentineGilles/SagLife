package com.example.saglife.screen.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.example.saglife.database.getUsernameFromUid
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private val auth = Firebase.auth
@Composable
fun ForumPostCard (navController : NavHostController, data: ForumPostItem) {

    // Etat de l'affichage de la description
    var showFullDescription by remember { mutableStateOf(false) }
    var author by remember { mutableStateOf("") }

    val icon = data.icon
    val title = data.title
    val author_id = data.author
    val date = data.getDay()
    val hour = data.getTime()
    val description = data.description

    if (author_id != "") {
        getUsernameFromUid(author_id) { username ->
            author = username
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
                text = author,
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
                text = title,
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
            if (showFullDescription || description.length < 500) {
                Text(
                    text = description,
                    textAlign = TextAlign.Justify
                )
            } else {
                Text(
                    text = description.substring(
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
                    bottom = 8.dp
                )
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 30.dp,
                end = 30.dp,
                bottom = 30.dp
            ))
        {
            if (author_id == auth.currentUser?.uid) {
                Text(
                    text = "Modifier",
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .clickable {println("id = $data.id")
                            navController.navigate("forum/modifypost/${data.id}")}
                        .fillMaxWidth()
                )
            }
        }
    }
}