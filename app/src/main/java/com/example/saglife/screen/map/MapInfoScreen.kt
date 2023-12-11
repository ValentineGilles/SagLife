package com.example.saglife.screen.map
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.models.MapComment
import com.example.saglife.models.MapItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Calendar

// Authentification Firebase
private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth

/**
 * Écran d'informations détaillées pour un établissement.
 *
 * @param navController Le contrôleur de navigation.
 * @param id L'identifiant de l'établissement sur la carte.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapInfoScreen(navController: NavHostController, id : String?, clientLocation: GeoPoint) {
// État pour stocker les informations sur l'établissement
    var map by remember {
        mutableStateOf(
            MapItem(
                "id",
                "Utilisateur supprimé",
                "Nom de l'établissement",
                "Adresse",
                GeoPoint(0.0, 0.0),
                "Catégorie",
                "Description...",
                "event.jpg",
                0.0,
                0F
            )
        )
    }
    // URL de l'image à afficher
    var urlImage = map.photoPath
    if (!urlImage.startsWith("https://firebasestorage.googleapis.com/"))
    {
        urlImage = "https://firebasestorage.googleapis.com/v0/b/saglife-94b7c.appspot.com/o/images%2Fevent.jpg?alt=media&token=d050b68e-9cac-49f7-99dd-09d60d735e4a"
    }

    // État du chargement des données
    var postLoaded by remember { mutableStateOf(false) }
    // Liste des commentaires
    var comments by remember{  mutableStateOf(mutableListOf<MapComment>())}

    // Etat du commentaire
    var comment by remember { mutableStateOf(TextFieldValue()) }

    //Etat des stars
    var stars by remember { mutableStateOf(0) }

    val context = LocalContext.current


    val db = Firebase.firestore
    LaunchedEffect(postLoaded) {
        if (!postLoaded) {
            if (id != null) {
                // Récupération des informations sur l'établissement depuis Firestore
                db.collection("map").document(id).get().addOnSuccessListener { document ->
                    val author_id = document.getString("Author_id") ?: ""
                    val name = document.getString("Name")!!
                    val adresseName = document.getString("AdresseName")!!
                    val adresseLocation = document.getGeoPoint("AdresseLocation")!!
                    val filter = document.getString("Filter")!!
                    val description = document.getString("Description")!!
                    val photoPath = document.getString("Photo")!!
                    val results = FloatArray(1)
                    Location.distanceBetween(adresseLocation.latitude, adresseLocation.longitude,clientLocation.latitude, clientLocation.longitude,results)
                    print("Location :"+ results[0])

                    // Récupération des notes depuis Firestore
                    db.collection("map").document(id).collection("notes").get().addOnSuccessListener { res ->
                        var note = 0.0
                        var sommeNote = 0
                        for (doc in res) {
                            sommeNote += doc.getDouble("Note")?.toInt() ?: 0
                        }
                        if(res.size()!=0){
                            note= (sommeNote/res.size()).toDouble()
                        }
                        map = MapItem(document.id, author_id, name, adresseName, adresseLocation, filter, description, photoPath,note, results[0]/1000)


                    }

                }
                // Récupération des commentaires depuis Firestore
                db.collection("map").document(id).collection("comments").get().addOnSuccessListener { result ->
                    var allComments = mutableListOf<MapComment>()
                    for (document in result) {
                        println(document.data)
                        val author = document.getString("Author")!!
                        val comment = document.getString("Comment")!!
                        val date = document.getDate("Date")!!
                        allComments.add(MapComment(author, comment, date))
                    }
                    comments=allComments
                }



                // Récupération de la note de l'utilisateur depuis Firestore
                db.collection("map").document(id).collection("notes").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { document ->
                    stars = document.getDouble("Note")?.toInt() ?: 0
                }
            }

        }
        }

    @Composable
    fun Star(number: Int){
        IconButton(onClick = {
            stars = number
            insertNoteIntoFirebase(id!!,number,auth.currentUser?.uid.toString())
        }){
            if(stars>=number)
                Icon(imageVector = Icons.Default.Star, contentDescription = null,modifier = Modifier.size(48.dp))
            else
                Icon(imageVector = Icons.TwoTone.Star, contentDescription = null,modifier = Modifier.size(48.dp))
        }


    }



    Box(
        modifier = Modifier
            .fillMaxSize(),
    )
    {
        // Affichage de l'écran d'informations détaillées
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            item {
                // Affichage de l'image
                AsyncImage(
                    model = urlImage,
                    contentDescription = "null",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )

                // Informations principales sur l'établissement
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(72.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column() {
                            Text(text = map.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically,) {
                                Text(
                                    text = if(map.note>0)"${map.note} \n sur 5" else "Non noté",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(
                                    Modifier
                                        .width(8.dp)
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()  //fill the max height
                                        .width(1.dp)
                                        .padding(top = 8.dp, bottom = 8.dp)

                                )
                                Spacer(
                                    Modifier
                                        .width(8.dp)
                                )
                                Text(text = String.format("%.1f", map.distance)+"km", fontSize = 12.sp, textAlign = TextAlign.Center)
                                Spacer(
                                    Modifier
                                        .width(8.dp)
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()  //fill the max height
                                        .width(1.dp)
                                        .padding(top = 8.dp, bottom = 8.dp)

                                )
                                Spacer(
                                    Modifier
                                        .width(8.dp)
                                )
                                Text(
                                    text = map.filter,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )

                            }
                        }
                        Button(
                            onClick = {
                                val adresse = map.adresseName
                                val gmmIntentUri = Uri.parse("geo:0,0?q=$adresse")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val playStoreIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=com.google.android.apps.maps")
                                    )

                                    val playStoreInfo =
                                        context.packageManager.resolveActivity(playStoreIntent, 0)
                                    if (playStoreInfo != null) {
                                        // Si le Play Store est disponible, l'ouvrir
                                        context.startActivity(playStoreIntent)
                                    } else {
                                        // Si le Play Store n'est pas disponible, ouvrir une URL web vers le Play Store
                                        val webPlayStoreIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                                        )
                                        context.startActivity(webPlayStoreIntent)
                                    }
                                }
                            },
                            modifier = Modifier
                                .wrapContentSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Itinéraire")
                        }
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()  //fill the max width
                        .height(1.dp)
                        .padding(start = 16.dp, end = 16.dp)
                )
                Text(
                    text = map.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Justify
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()  //fill the max width
                        .height(1.dp)
                        .padding(start = 16.dp, end = 16.dp)
                )
                Spacer(
                    Modifier
                        .height(8.dp)
                )

                Row {
                    Text(
                        text = "Toucher pour noter : ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )


                    }
                Row (horizontalArrangement = Arrangement.End){
                    Star(1)
                    Star(2)
                    Star(3)
                    Star(4)
                    Star(5)
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()  //fill the max width
                        .height(1.dp)
                        .padding(start = 16.dp, end = 16.dp)
                )
                Spacer(
                    Modifier
                        .height(8.dp)
                )

                // Affichage du titre "Commentaires"
                Text(
                    text = "Commentaires",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )
                Spacer(
                    Modifier
                        .height(8.dp)
                )
                for (comment in comments) {
                    val star = 3
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
                                Text(text = comment.author)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${comment.getDay()}",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = comment.comment)
                        }
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
                        /*
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
                        */

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
                        MapComment(author, commentText, commentdate).toFirebase(id,auth.currentUser?.uid.toString(), context) // Ajoute le commentaire à la base de données
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

fun insertNoteIntoFirebase(mapId: String, note: Int, userId : String) {
    val db = Firebase.firestore

    // Ajoute le nouveau commentaire à la collection "comments"
    db.collection("map")
        .document(mapId)
        .collection("notes")
        .document(userId).set(mapOf("Note" to note))
        .addOnSuccessListener { documentReference ->
            println("Note ajouté avec l'ID : ${userId}")
        }
        .addOnFailureListener { e ->
            println("Erreur lors de l'ajout de la note : $e")
        }
}

