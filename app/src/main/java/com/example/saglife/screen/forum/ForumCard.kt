package com.example.saglife.screen.forum


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.example.saglife.database.getUsernameFromUid
import com.example.saglife.models.ForumPostItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.firestore


@SuppressLint("DiscouragedApi")
@Composable
fun ForumCard(data: ForumPostItem, navController: NavHostController) {
    // Déclaration de variables pour stocker les données de la carte du forum
    var author by remember { mutableStateOf("") }
    var icon = R.drawable.ic_profile
    val title = data.title
    val author_id = data.author
    val date = data.getDay()
    val hour = data.getTime()
    val id = data.id
    val filter = data.filter

    // Sélection de l'icône en fonction du filtre du message du forum
    when (filter) {
        "Immigration" -> icon = R.drawable.ic_immigration
        "Vie courante" -> icon = R.drawable.ic_daily
        "Assurance" -> icon = R.drawable.ic_insurance
        "Santé" -> icon = R.drawable.ic_health
        "Tourisme" -> icon = R.drawable.ic_tourism
        "Bon plan" -> icon = R.drawable.ic_deal
        else -> icon = R.drawable.ic_question
    }

    // Récupération du nom d'auteur en fonction de l'ID de l'auteur
    if (author_id != "") {
        getUsernameFromUid(author_id) { username ->
            author = username
        }
    }

    // Accès à la base de données Firestore
    val db = Firebase.firestore

    // Comptage du nombre de commentaires pour ce message du forum
    val nb_comments = db.collection("forum").document(id).collection("comments").count()

    var snapshot: AggregateQuerySnapshot? = null
    var nb_com by remember { mutableStateOf(0) }

    // Récupération du nombre de commentaires en fonction du résultat de la requête
    nb_comments.get(AggregateSource.SERVER).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            snapshot = task.result
        } else {
            task.exception?.message?.let {
                print("Count : $it")
            }
        }
        nb_com = (snapshot?.count ?: 0).toInt()
    }
        .addOnFailureListener { e ->
            println("Erreur lors du comptage des commentaires : $e")
        }

    // Composable Card qui affiche la carte du forum
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth().shadow(4.dp, shape = RoundedCornerShape(8.dp)).clickable {
            navController.navigate("forum/$id")
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            // Affichage de l'icône en fonction du filtre
            Icon(
                painter = painterResource(icon),
                contentDescription = "Forum",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(2.dp)
                    .background(Color(0xFFCCCCCC))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                // Affichage du titre du message
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall)
                // Affichage de l'auteur du message
                Text(
                    text = author,
                    style = TextStyle(
                        fontSize = 12.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Color.Gray
                )
                // Affichage du nombre de commentaires et de la date/heure du message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$nb_com réponse(s)",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 0.sp
                        ),
                    )
                    Text(
                        text = "$date à $hour",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 0.sp
                        ),
                    )
                }
            }
        }
    }
}

