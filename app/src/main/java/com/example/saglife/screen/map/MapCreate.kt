package com.example.saglife.screen.map

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.firestore
import java.util.Calendar
import java.util.UUID

private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun MapCreate(navController: NavHostController) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var adresse by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var photoPath by remember { mutableStateOf(TextFieldValue()) }


    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf("") }
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }



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
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = adresse,
            onValueChange = { adresse = it },
            label = { Text("Adresse") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale
                .padding(bottom = 16.dp)
        )

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


        ElevatedButton(
            onClick = {


                if (name.text != "" && description.text !="" && adresse.text != "") {
                    val map = MapItem(
                        id = "",
                        name = name.text,
                        adresse = adresse.text,
                        filter = selectedFilter,
                        description = description.text,
                        photoPath = "map.png"
                    )
                    map.toFirebase()
                navController.popBackStack()}
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }

}




