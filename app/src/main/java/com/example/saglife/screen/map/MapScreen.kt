package com.example.saglife.screen.map

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.example.saglife.screen.calendar.FilterChip
import com.example.saglife.models.MapItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

/**
 * Écran affichant la liste des établissements.
 *
 * @param navController Le contrôleur de navigation.
 */
@Composable
fun MapScreen(navController: NavHostController) {

    // Initialisation des états
    var selectedFilters = mutableListOf<String>()
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }
    var mapsFiltered by remember { mutableStateOf(mutableListOf<MapItem>()) }
    var allMaps = mutableListOf<MapItem>()

    // Instance de Firebase Firestore
    val db = Firebase.firestore

    // Récupération des filtres depuis la collection "filter_map"
    db.collection("filter_map").get().addOnSuccessListener { result ->
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



    // Récupération de toutes les cartes depuis la collection "map"
    db.collection("map").get().addOnSuccessListener { result ->
        for (document in result) {
            val name = document.getString("Name")!!
            val adresseName = document.getString("AdresseName")!!
            val adresseLocation = document.getGeoPoint("AdresseLocation")!!
            val filter = document.getString("Filter")!!
            val description = document.getString("Description")!!
            val photoPath = document.getString("Photo")!!

            allMaps.add(MapItem(document.id, name, adresseName, adresseLocation, filter, description, photoPath,0.0))

        }

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



    // Mise en page de l'interface utilisateur avec Compose
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            // Affichage des filtres dans une LazyRow
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterList) { filter ->
                    FilterChip(
                        onClick = { filterName ->
                            // Filtrage des cartes en fonction des filtres sélectionnés
                            if (selectedFilters.contains(filterName)) {
                                selectedFilters.remove(filterName)
                            } else {
                                selectedFilters.add(filterName)
                            }
                            println(selectedFilters)
                            if (selectedFilters.isNotEmpty()) {
                                print("All map" + allMaps)
                                mapsFiltered = filterMapItems(selectedFilters, allMaps)
                                println("not empty")
                                println("filter : " + filterMapItems(selectedFilters, allMaps))
                            } else {
                                println("empty")

                                mapsFiltered = allMaps
                            }
                            println(mapsFiltered)


                        },
                        filter
                    )
                }
            }
            // Affichage des cartes filtrées dans une LazyColumn
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(mapsFiltered) { map ->
                    MapComposant(map = map, navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


        }
        // Bouton flottant pour ajouter une nouvelle carte
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                navController.navigate("map/create")
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter un établissement"
            )
        }
    }
}

/**
 * Fonction qui prend en paramètre une liste de chaînes de caractères [filters] et une liste d'objets [MapItem].
 * La fonction filtre la liste d'objets en ne retournant que les éléments où l'un des éléments de [filters] est égal au champ [filter].
 *
 * @param filters Liste des filtres à appliquer.
 * @param mapItems Liste des établissement à filtrer.
 * @return Liste filtrée d'objets [MapItem].
 */
fun filterMapItems(filters: List<String>, mapItems: List<MapItem>): MutableList<MapItem> {
    val filteredMapItems = mutableListOf<MapItem>()
    for (mapItem in mapItems) {
        if (filters.any { it == mapItem.filter }) {
            filteredMapItems.add(mapItem)
        }
    }
    return filteredMapItems
}


/**
 * Composant qui affiche un établissement.
 *
 * @param map Objet [MapItem] à afficher.
 * @param navController Contrôleur de navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComposant(map: MapItem, navController: NavHostController) {
    var storage = Firebase.storage
    var storageReference = storage.getReference("images/").child(map.photoPath)
    var urlImage: Uri? by remember { mutableStateOf(null) }
    storageReference.downloadUrl.addOnSuccessListener { url -> urlImage = url }


    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
        onClick = { navController.navigate("mapInfo/${map.id}") }) {
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
            Surface(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(modifier = Modifier) {
                        Text(text = map.name, style = MaterialTheme.typography.titleLarge/*, fontWeight = FontWeight.Bold*/)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if(map.note>0)"${map.note} \n sur 5" else "Non noté",
                                style = MaterialTheme.typography.bodyMedium,
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
                            Text(text = "1,2km", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
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
                            Text(text = map.filter, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

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

        }

    }

}


