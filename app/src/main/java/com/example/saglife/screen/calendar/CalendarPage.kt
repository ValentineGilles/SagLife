package com.example.saglife.screen.calendar

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.saglife.R
import com.example.saglife.database.getUsernameFromUid
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date


/**
 * Écran affichant les détails d'un événement.
 *
 * @param navController Contrôleur de navigation.
 * @param id Identifiant de l'événement à afficher.
 */
@Composable
fun EventScreen(id: String?) {
    var postLoaded by remember { mutableStateOf(false) }
    var event by remember {
        mutableStateOf(
            EventItem(
                "",
                "",
                Date(),
                Date(),
                "Description...",
                "",
                "",
                ""
            )
        )
    }

    val db = Firebase.firestore
    LaunchedEffect(postLoaded) {
        if (!postLoaded && id != null) {
            db.collection("event").document(id).get().addOnSuccessListener { document ->
                val name = document.get("Name").toString()
                val dateStart: Date = document.getDate("Date_start")!!
                val dateEnd = document.getDate("Date_stop")!!
                val description = document.get("Description").toString()
                val photoPath = document.get("Photo").toString()
                val filter = document.get("Filter").toString()
                val author_id = document.get("Author").toString()
                event = EventItem(
                    document.id,
                    name,
                    dateStart,
                    dateEnd,
                    description,
                    photoPath,
                    filter,
                    author_id
                )
            }
            postLoaded = true
        }
    }


    EventCard(event = event)

}


@Composable
fun EventCard(event: EventItem) {
    var urlImage = event.photoPath
    if (!urlImage.startsWith("https://firebasestorage.googleapis.com/"))
    {
        urlImage = "https://firebasestorage.googleapis.com/v0/b/saglife-94b7c.appspot.com/o/images%2Fevent.jpg?alt=media&token=d050b68e-9cac-49f7-99dd-09d60d735e4a"
    }
    var author = ""

    if (event.author_id != "") {
        getUsernameFromUid(event.author_id) { username ->
            author = username
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = urlImage,
                contentDescription = "Photo de l'événement",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(70.dp)) {
                    Text(
                        text = event.getDay(), style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(
                            Alignment.Center
                        ), textAlign = TextAlign.Center
                    )
                }
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
                Column {
                    Text(text = event.name, style = MaterialTheme.typography.titleLarge /*, fontWeight = FontWeight.Bold*/)
                    Text(text = event.getTime(), style = MaterialTheme.typography.bodyMedium)
                }

            }
        }
        Text(
            text = author,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Justify
        )
    }
}

@Preview
@Composable
fun Preview() {
    EventScreen("UqmaBRACBQ44cpING99X")
}
