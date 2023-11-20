package com.example.saglife.screen.calendar

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Calendar
import java.util.Date

/**
 * Écran affichant la liste des événements.
 *
 * @param navController Contrôleur de navigation.
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
fun CalendarScreen(navController: NavHostController) {
    // État pour suivre le chargement des données.
    var postLoaded by remember { mutableStateOf(false) }

    // Liste des filtres sélectionnés.
    var selectedFilters by remember { mutableStateOf(mutableListOf<String>()) }

    // Liste de tous les filtres disponibles.
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }

    // Instance de la base de données Firestore.
    val db = Firebase.firestore

    // Récupération des filtres depuis Firestore.
    db.collection("filter_event").get().addOnSuccessListener { result ->
        println("filter")
        val filters = mutableListOf<String>()
        for (document in result) {
            val name = document.getString("Name")!!
            filters.add(name)
        }
        filterList = filters
    }
        .addOnFailureListener { e ->
            println("Erreur lors de la récupération des données des filtres : $e")
        }

    // Liste des événements filtrés.
    var eventsFiltered by remember { mutableStateOf(mutableListOf<EventItem>()) }


    // Liste de tous les événements.
    val allEvents = mutableListOf<EventItem>()
    db.collection("event").orderBy("Date_start", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
        println("event")
        for (document in result) {

            val name = document.get("Name").toString()
            val dateStart: Date = document.getDate("Date_start")!!
            val dateEnd = document.getDate("Date_stop")!!
            val description = document.get("Description").toString()
            val photoPath = document.get("Photo").toString()
            val filter = document.get("Filter").toString()
            allEvents.add(
                EventItem(
                    document.id,
                    name,
                    dateStart,
                    dateEnd,
                    description,
                    photoPath,
                    filter
                )
            )
        }
        eventsFiltered = allEvents
    }
    // Affichage de la page principale.
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Affichage des filtres sous forme de puce.
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterList) { filter ->
                    FilterChip(
                        onClick = { filterName ->
                            // Gestion de la sélection/déselection du filtre.
                            if (selectedFilters.contains(filterName)) {
                                selectedFilters.remove(filterName)
                            } else {
                                selectedFilters.add(filterName)
                            }
                            // Filtrage des événements en fonction des filtres sélectionnés.
                            if (selectedFilters.isNotEmpty()) {
                                eventsFiltered = filterEventItems(selectedFilters, allEvents)
                            } else {
                                eventsFiltered = allEvents
                            }


                        },
                        filter
                    )
                }
            }
// Affichage des événements filtrés.
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(eventsFiltered) { event ->
                    EventComposant(event = event, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        // Bouton flottant pour ajouter un nouvel événement.
        FloatingActionButton(
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
            onClick = {
                navController.navigate("event/create")
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter un événement"
            )
        }
    }
}

/**
 * Fonction qui prend en paramètre une liste de chaînes de caractères [filters] et une liste d'objets [EventItem].
 * La fonction filtre la liste d'objets en ne retournant que les éléments où l'un des éléments de [filters] est égal au champ [filter].
 *
 * @param filters Liste des filtres à appliquer.
 * @param eventItems Liste des événements à filtrer.
 * @return Liste filtrée d'objets [EventItem].
 */
fun filterEventItems(filters: List<String>, eventItems: List<EventItem>): MutableList<EventItem> {
    val filteredEventItems = mutableListOf<EventItem>()
    for (eventItem in eventItems) {
        if (filters.any { it == eventItem.filter }) {
            filteredEventItems.add(eventItem)
        }
    }
    return filteredEventItems
}

/**
 * Composant qui affiche un événement.
 *
 * @param event Objet [EventItem] à afficher.
 * @param navController Contrôleur de navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventComposant(event: EventItem, navController: NavHostController) {
    // Récupération de l'URL de l'image depuis Firebase Storage.
    val storage = Firebase.storage
    val storageReference = storage.getReference("images/").child(event.photoPath)
    var urlImage: Uri? by remember { mutableStateOf(null) }
    storageReference.downloadUrl.addOnSuccessListener { url -> urlImage = url }

// Affichage de la carte d'événement.
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
        onClick = { navController.navigate("event/${event.id}") }) {
        Box(
            contentAlignment = Alignment.BottomCenter,

        ) {
            if (urlImage != null)
                AsyncImage(
                    model = urlImage,
                    contentDescription = "null",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),
                )
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

        }

    }

}


