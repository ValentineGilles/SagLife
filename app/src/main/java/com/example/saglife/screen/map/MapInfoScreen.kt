package com.example.saglife.screen.map
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.example.saglife.models.MapComment
import com.example.saglife.models.MapItem
import com.example.saglife.screen.forum.insertIntoFirebase
import com.example.saglife.ui.theme.Purple40
import com.example.saglife.ui.theme.Purple80
import com.example.saglife.ui.theme.PurpleGrey40
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Calendar
import java.util.Date

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
fun MapInfoScreen(navController: NavHostController, id : String?) {
// État pour stocker les informations sur l'établissement
    var map by remember {
        mutableStateOf(
            MapItem(
                "id",
                "Nom de l'établissement",
                "Adresse",
                "Catégorie",
                "Description...",
                "event.jpg"
            )
        )
    }
    // URL de l'image à afficher
    var urlImage : Uri? by remember { mutableStateOf(null) }
    // État du chargement des données
    var postLoaded by remember { mutableStateOf(false) }
    // Liste des commentaires
    var comments by remember{  mutableStateOf(mutableListOf<MapComment>())}

    // Etat du commentaire
    var comment by remember { mutableStateOf(TextFieldValue()) }

    val db = Firebase.firestore
    LaunchedEffect(postLoaded) {
        if (!postLoaded) {
            if (id != null) {
                // Récupération des informations sur l'établissement depuis Firestore
                db.collection("map").document(id).get().addOnSuccessListener { document ->
                    val name = document.getString("Name")!!
                    val adresse = document.getString("Adresse")!!
                    val filter = document.getString("Filter")!!
                    val description = document.getString("Description")!!
                    val photoPath = document.getString("Photo")!!
                    map = MapItem(document.id, name, adresse, filter, description, photoPath)
                }
                // Récupération des commentaires depuis Firestore
                db.collection("map").document(id).collection("comments").get().addOnSuccessListener { result ->
                    var allComments = mutableListOf<MapComment>()
                    for (document in result) {
                        println(document.data)
                        val author = document.getString("Author")!!
                        val comment = document.getString("Comment")!!
                        val date = document.getDate("Date")!!
                        val note = document.getDouble("Note")!!.toInt()
                        allComments.add(MapComment(author, comment, date, note))
                    }
                    comments=allComments
                }
            }
            // Récupération de l'URL de l'image depuis Storage
            var storage = Firebase.storage
            var storageReference = storage.getReference("images/").child(map.photoPath)
            storageReference.downloadUrl.addOnSuccessListener { url-> urlImage = url}

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
                if (urlImage == null) Image(
                    painter = painterResource(id = R.drawable.event),
                    contentDescription = "null",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),

                    ) else
                    AsyncImage(
                        model = urlImage,
                        contentDescription = "null",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth(),
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
                                    text = "4,7 \nsur 5",
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
                                Text(text = "1,2km", fontSize = 12.sp, textAlign = TextAlign.Center)
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
                        FilledTonalButton(
                            onClick = { },
                            contentPadding = PaddingValues(16.dp, 8.dp),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            shape = RoundedCornerShape(24),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            )
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

                // Affichage du titre "Commentaires"
                Text(
                    text = "Commentaires",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                                    text = "${comment.date}",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row {
                                for (i in 1..star) Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null
                                )
                                for (i in star + 1..5) Icon(
                                    imageVector = Icons.TwoTone.Star,
                                    contentDescription = null
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
                    val author = auth.currentUser?.displayName
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



@Composable
fun Comment(){
    val star = 3
    Surface (color = PurpleGrey40,shape = RoundedCornerShape(8)){ Column (modifier = Modifier
        .padding(8.dp)
        .width(100.dp)){ Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){ Text(text = "Author", fontWeight = FontWeight.Bold)
        Text(text = "18 janvier 2022", color = Purple80)}
        Row { for (i in 1..star)Icon(imageVector = Icons.Default.Star, contentDescription = null)
            for (i in star+1..5)Icon(imageVector = Icons.TwoTone.Star, contentDescription = null) }
        Spacer(
            Modifier
                .height(8.dp)
        )
        Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam aliquam quis turpis eu volutpat. Proin nisi risus, accumsan sit amet mauris a, cursus mattis sem. Nunc ut mi lorem. Curabitur at arcu ullamcorper, elementum nibh at, facilisis est.",textAlign = TextAlign.Justify)
    } }
}

@Composable
fun Comment2(comment : MapComment){
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
                    text = comment.getDay(),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row { for (i in 1..comment.note)Icon(imageVector = Icons.Default.Star, contentDescription = null)
                for (i in comment.note+1..5)Icon(imageVector = Icons.TwoTone.Star, contentDescription = null) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment.comment)
        }

    }
}
@Preview
@Composable
fun PreviewMap() {
    Comment2(MapComment("Sacha","Wshhh coucou", Date(), 3))
    /*
    Column (
        modifier = Modifier.fillMaxSize(),
    ){ Image(
        painter = painterResource(id = R.drawable.event),
        contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth()
    )
        Surface(modifier = Modifier.padding(16.dp).fillMaxWidth().height(72.dp)){
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.padding(8.dp)) {
            Column (){
                Text(text = "map.name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment= Alignment.CenterVertically, ){
                    Text(text = "4,7 \nsur 5", fontSize = 12.sp, textAlign= TextAlign.Center)
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
                    Text(text = "1,2km", fontSize = 12.sp, textAlign= TextAlign.Center)
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
                    Text(text = "map.categorie", fontSize = 12.sp, textAlign= TextAlign.Center)

                }
            }
            FilledTonalButton(onClick = {  }, contentPadding = PaddingValues(16.dp,8.dp),modifier = Modifier
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp), shape = RoundedCornerShape(24), elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp
            )
            ) {
                Text("Itinéraire")
            }
        }
        }
        Divider(modifier = Modifier
            .fillMaxWidth()  //fill the max width
            .height(1.dp).padding(start = 16.dp, end= 16.dp))
        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla suscipit ex erat. Maecenas tempus nulla neque, vehicula posuere libero porta in. Donec fermentum enim vitae dui semper luctus. Nullam rhoncus ligula at tellus molestie, sed facilisis dui volutpat. Nunc fringilla massa id purus porttitor maximus. Aenean diam ante, consectetur euismod tincidunt nec, elementum in nisl. Sed bibendum tempus lorem, sed accumsan massa pellentesque at. Vivamus ullamcorper at mauris in aliquam. Vivamus fermentum libero quis purus tempor, non consequat dui rutrum. Nam blandit nibh nec odio ornare iaculis. Nulla id pharetra diam.\n" +
                "\n" +
                "Mauris placerat sed elit in ullamcorper. Vivamus viverra eu lorem nec blandit. Morbi id scelerisque augue. Vivamus a ante nibh. Nam cursus purus nec sapien ultrices, eu vulputate libero vestibulum. Phasellus eget velit malesuada, porttitor nunc ut, semper ante. Nunc hendrerit viverra lacus, eu venenatis diam maximus et. Nulla dignissim tristique nulla non feugiat.\n" +
                "\n" +
                "Morbi vitae semper risus. Donec mollis lacinia dictum. In justo elit, faucibus non libero ut, pharetra blandit ligula. Pellentesque non egestas arcu, vel ornare magna. Nam molestie nunc vel purus hendrerit, id volutpat urna congue. Phasellus faucibus tincidunt risus, at iaculis ex mollis quis. Cras non porta nisi. Aenean a felis arcu. Quisque pulvinar at arcu vel condimentum. Nulla nec fringilla est. Cras maximus, elit sed elementum semper, elit velit tincidunt nunc, eu congue augue quam quis lacus. Duis fermentum ligula et faucibus faucibus. Duis felis tortor, sagittis non imperdiet sit amet, fermentum nec purus. Vivamus elementum est odio, sit amet vulputate odio pharetra vitae.",fontSize = 16.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Justify)
        Divider(modifier = Modifier
            .fillMaxWidth()  //fill the max width
            .height(1.dp).padding(start = 16.dp, end= 16.dp))
    }*/

}
