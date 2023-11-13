package com.example.saglife.screen.map

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
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
import com.example.saglife.screen.calendar.FilterChip
import com.example.saglife.models.MapItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@Composable
fun MapScreen(navController : NavHostController) {

    var selectedFilters by remember { mutableStateOf(mutableListOf<String>()) }
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }

    val db = Firebase.firestore

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

    var mapsFiltered by remember { mutableStateOf(mutableListOf<MapItem>()) }
    var allMaps = mutableListOf<MapItem>()

    db.collection("map").get().addOnSuccessListener { result ->
        for (document in result) {
            val name = document.getString("Name")!!
            val adresse = document.getString("Adresse")!!
            val filter = document.getString("Filter")!!
            val description = document.getString("Description")!!
            val photoPath = document.getString("Photo")!!
            allMaps.add(MapItem(document.id, name, adresse, filter, description, photoPath))
        }
        mapsFiltered = allMaps

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
                            mapsFiltered = filterMapItems(selectedFilters, allMaps)
                        } else {
                            mapsFiltered = allMaps
                        }


                    },
                    filter
                )
            }
        }
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(mapsFiltered) { map ->
            MapComposant(map = map, navController = navController)
        }
    }
}
}

// Fonction qui prend en paramètre une liste de String filters et une liste de EventItem (val id:String,val name : String, val dateStart : Date, val dateEnd : Date, val description : String, val photoPath : String, val filter : String). La fonction doit filtrer la liste de EventItem en ne retournant que les élément ou l'un des Strings de la liste filters est égale au champ val filter : String.
fun filterMapItems(filters: List<String>, mapItems: List<MapItem>): MutableList<MapItem> {
    val filteredMapItems = mutableListOf<MapItem>()
    for (mapItem in mapItems) {
        if (filters.any { it == mapItem.filter }) {
            filteredMapItems.add(mapItem)
        }
    }
    return filteredMapItems
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComposant(map : MapItem, navController : NavHostController){
    var storage = Firebase.storage
    var storageReference = storage.getReference("images/").child(map.photoPath)
    var urlImage : Uri? by remember { mutableStateOf(null) }
    storageReference.downloadUrl.addOnSuccessListener { url-> urlImage = url}


    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp), onClick = {navController.navigate("mapInfo/${map.id}")}) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ){
            if(urlImage==null) Image(
                painter = painterResource(id = R.drawable.event),
                contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth(),

                ) else
                AsyncImage(
                    model = urlImage,contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth(),
                )
            Surface(modifier = Modifier.fillMaxWidth().height(72.dp)){
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.padding(8.dp)) {
                    Column (){
                        Text(text = map.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            Text(text = map.filter, fontSize = 12.sp, textAlign= TextAlign.Center)

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

        }

    }

}


