package com.example.saglife.screen.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumFilterItem
import com.example.saglife.models.ForumPostItem
import com.example.saglife.models.MapItem
import com.example.saglife.screen.calendar.FilterChip
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import java.io.IOException
import java.util.Calendar
import java.util.UUID

/**
 * Écran de création d'un nouvel établissement.
 *
 * @param navController Le contrôleur de navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun MapCreate(navController: NavHostController) {
    // État des champs de saisie
    var name by remember { mutableStateOf(TextFieldValue()) }
    var locationName by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var photoPath by remember { mutableStateOf(TextFieldValue()) }


    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }

    // État des filtres
    var selectedFilter by remember { mutableStateOf("") }
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }

    //Contexte
    val context = LocalContext.current


    val db = Firebase.firestore

    LaunchedEffect(postLoaded) {
        if (!postLoaded) {

            db.collection("filter_map").get().addOnSuccessListener { result ->
                val filters = mutableListOf<String>()
                for (document in result) {
                    val name = document.getString("Name")!!
                    filters.add(name)
                }
                filterList = filters
                selectedFilter = filters[0]
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données des filtres : $e")
                }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajouter un nouvel établissement",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, top= 32.dp)
        )

// Champ de saisie pour le nom de l'établissement
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom de l'établissement") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale
                .padding(bottom = 16.dp)
        )
        // Champ de saisie pour l'adresse
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = locationName,
            onValueChange = { locationName = it },
            label = { Text("Adresse") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale
                .padding(bottom = 16.dp)
        )
        // Champ de saisie pour la description
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(200.dp)
        )

        // Liste de filtres sous forme de puces filtrantes
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterList) { filter ->
                androidx.compose.material3.FilterChip(
                    onClick = {
                        selectedFilter = filter
                    },
                    label = {
                        Text(filter)
                    },
                    selected = selectedFilter == filter,
                    leadingIcon = if (selectedFilter == filter) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }

        // Bouton pour enregistrer les informations sur la carte
        ElevatedButton(
            onClick = {

                // Vérification des champs non vides avant d'enregistrer
                if (name.text != "" && description.text !="" && locationName.text != "") {

                    //TEST

                    val coordinates = getLocationFromAddress(context, locationName.text)


                    if (coordinates != null) {
                        val (latitude, longitude) = coordinates
                        // Faites quelque chose avec les coordonnées obtenues
                        println("Latitude: $latitude, Longitude: $longitude")

                        val map = MapItem(
                            id = "",
                            name = name.text,
                            adresseName = locationName.text,
                            adresseLocation = GeoPoint(coordinates.first, coordinates.second),
                            filter = selectedFilter,
                            description = description.text,
                            photoPath = "map.png",
                            0.0
                        )
                        // Enregistrement des données dans Firebase
                        map.toFirebase()
                        // Retour à l'écran précédent
                        navController.popBackStack()
                    } else {
                        // Gérez le cas où les coordonnées ne peuvent pas être obtenues
                        println("Impossible d'obtenir les coordonnées pour l'adresse donnée.")
                    }

                    }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }

}


// Fonction pour obtenir les coordonnées géographiques à partir d'un nom de lieu
fun getLocationFromAddress(context: Context, locationName: String): Pair<Double, Double>? {
    val geocoder = Geocoder(context)

    try {
        // Utilisez le geocoder pour obtenir la liste d'adresses possibles à partir du nom de lieu
        val addresses: MutableList<Address>? = geocoder.getFromLocationName(locationName, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            // Obtenez la première adresse (la plus probable)
            val address: Address = addresses[0]

            // Récupérez la latitude et la longitude
            val latitude = address.latitude
            val longitude = address.longitude

            // Retournez les coordonnées en tant que Pair
            return Pair(latitude, longitude)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // Retournez null si les coordonnées ne peuvent pas être obtenues
    return null
}





