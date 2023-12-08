package com.example.saglife.screen.home
import androidx.compose.foundation.layout.Column
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.EventItem
import com.example.saglife.models.ForumPostItem
import com.example.saglife.models.MapItem
import com.example.saglife.screen.calendar.EventComposant
import com.example.saglife.screen.forum.ForumCard
import com.example.saglife.screen.map.MapComposant
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun HomeScreen(navController: NavHostController, clientLocation: GeoPoint) {
    val db = Firebase.firestore
    val forumpost = mutableListOf<ForumPostItem>()
    var ForumPostList by remember { mutableStateOf(mutableListOf<ForumPostItem>()) }
    var postLoaded by remember { mutableStateOf(false) }

    // Etat du rafraîchissement
    var refreshing by remember { mutableStateOf(false) }

    // Récupération des derniers posts depuis Firebase
    db.collection("forum").orderBy("Date", Query.Direction.DESCENDING).limit(3)
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val date: Date = document.getDate("Date")!!
                val author = document.get("Author").toString()
                val icon = document.get("Icon").toString()
                val title = document.get("Title").toString()
                val nb = document.get("Nb").toString().toIntOrNull() ?: 0
                val description = document.get("Description").toString()
                val filter = document.get("Filter").toString()

                forumpost.add(
                    ForumPostItem(
                        document.id,
                        author,
                        date,
                        icon,
                        title,
                        nb,
                        filter,
                        description
                    )
                )
            }
            ForumPostList = forumpost
        }
        .addOnFailureListener { e ->
            println("Erreur lors de la récupération des données des posts: $e")
        }

    var mapsFiltered by remember { mutableStateOf(mutableListOf<MapItem>()) }
    val allMaps = mutableListOf<MapItem>()

    db.collection("map").limit(3).get().addOnSuccessListener { result ->
        for (document in result) {
            val name = document.getString("Name")!!
            val adresseName = document.getString("AdresseName")!!
            val adresseLocation = document.getGeoPoint("AdresseLocation")!!
            val filter = document.getString("Filter")!!
            val description = document.getString("Description")!!
            val photoPath = document.getString("Photo")!!
            val results = FloatArray(1)
            Location.distanceBetween(adresseLocation.latitude, adresseLocation.longitude,clientLocation.latitude, clientLocation.longitude,results)
            print("Location :"+ results[0])
            allMaps.add(MapItem(document.id, name, adresseName, adresseLocation, filter, description, photoPath,0.0, (results[0]/1000)))

        }
        print("All map  " + allMaps)
        mapsFiltered = allMaps

        mapsFiltered = allMaps

        for(mapItem in allMaps){
            // Récupération des notes depuis Firestore
            db.collection("map").document(mapItem.id).collection("notes").get().addOnSuccessListener { resultat ->

                var note = 0.0
                var sommeNote = 0
                for (document in resultat) {
                    sommeNote += document.getDouble("Note")?.toInt() ?: 0
                }
                if(resultat.size()!=0){
                    note= (sommeNote/resultat.size()).toDouble()
                }

                mapItem.note = note

            }.continueWith {
                mapsFiltered = allMaps
            }

        }

    }

    // Liste des événements filtrés.
    var eventsFiltered by remember { mutableStateOf(mutableListOf<EventItem>()) }


    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    // Liste de tous les événements.
    val allEvents = mutableListOf<EventItem>()
    db.collection("event").whereGreaterThanOrEqualTo("Date_start", today.time).orderBy("Date_start", Query.Direction.ASCENDING).limit(3).get().addOnSuccessListener { result ->
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

    // Affiche la liste des derniers posts dans un SwipeRefresh
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            refreshing = true
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(1000) // Simule une attente de 1 seconde
                refreshing = false
                postLoaded = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn {
                item {
                    // Affiche le titre "Derniers Posts"
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 16.dp),
                        text = "Derniers Posts",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                    )
                }
                items(ForumPostList) { data ->
                    // Affiche chaque post
                    ForumCard(
                        navController = navController,
                        data = data
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 16.dp),
                        text = "Les prochains évenements",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                    )
                }
                items(eventsFiltered) { event ->
                    EventComposant(event = event, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 16.dp),
                        text = "Quelques lieux à visiter",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                    )
                }

                items(mapsFiltered) { map ->
                    MapComposant(map = map, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }


            }

        }
    }

}

