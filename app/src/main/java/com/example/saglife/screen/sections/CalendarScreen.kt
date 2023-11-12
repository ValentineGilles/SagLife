package com.example.saglife.screen.sections

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.component.calendar.FilterChip
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CalendarScreen(navController: NavHostController) {

    var selectedFilters by remember { mutableStateOf(mutableListOf<String>()) }
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }

    val db = Firebase.firestore

    db.collection("filter_event").get().addOnSuccessListener { result ->
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

    var eventsFiltered by remember { mutableStateOf(mutableListOf<EventItem>()) }


    val allEvents = mutableListOf<EventItem>()
    db.collection("event").get().addOnSuccessListener { result ->
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

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterList) { filter ->
                FilterChip(
                    onClick = { filterName ->

                        if (selectedFilters.contains(filterName)) {
                            selectedFilters.remove(filterName)
                        } else {
                            selectedFilters.add(filterName)
                        }
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

        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(eventsFiltered) { event ->
                EventComposant(event = event, navController = navController)
            }
        }
    }
}

// Fonction qui prend en paramètre une liste de String filters et une liste de EventItem (val id:String,val name : String, val dateStart : Date, val dateEnd : Date, val description : String, val photoPath : String, val filter : String). La fonction doit filtrer la liste de EventItem en ne retournant que les élément ou l'un des Strings de la liste filters est égale au champ val filter : String.
fun filterEventItems(filters: List<String>, eventItems: List<EventItem>): MutableList<EventItem> {
    val filteredEventItems = mutableListOf<EventItem>()
    for (eventItem in eventItems) {
        if (filters.any { it == eventItem.filter }) {
            filteredEventItems.add(eventItem)
        }
    }
    return filteredEventItems
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventComposant(event: EventItem, navController: NavHostController) {
    val storage = Firebase.storage
    val storageReference = storage.getReference("images/").child(event.photoPath)
    var urlImage: Uri? by remember { mutableStateOf(null) }
    storageReference.downloadUrl.addOnSuccessListener { url -> urlImage = url }


    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp), onClick = { navController.navigate("event/${event.id}") }) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
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
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(80.dp)) {
                        Text(
                            text = event.getDay(), fontSize = 24.sp, modifier = Modifier.align(
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
                        Text(text = event.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(text = event.getTime())
                    }
                }
            }

        }

    }

}


