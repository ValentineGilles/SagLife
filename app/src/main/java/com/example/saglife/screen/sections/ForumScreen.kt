package com.example.saglife.screen.sections

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.ForumCard
import com.example.saglife.R
import com.example.saglife.component.FilterChip
import com.example.saglife.models.ForumFilterItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log

@SuppressLint("MutableCollectionMutableState", "LogNotTimber")
@Composable
fun ForumScreen(navController : NavHostController) {

    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val db = Firebase.firestore
    val forumfilter = mutableListOf<ForumFilterItem>()
    db.collection("filter_forum").get().addOnSuccessListener { result ->
        for (document in result) {
            val name = document.get("Name").toString()
            forumfilter.add(ForumFilterItem(name))
        }
        filterList = forumfilter
    }

        .addOnFailureListener { e ->
            println("Erreur lors de la récupération des données : $e")
        }

    //val filterList = listOf("Tout", "Immigration", "Assurance", "Autre filtre", "Encore un filtre", "Dernier filtre")
    val forumDataList = listOf(
        mapOf(
            "icon" to R.drawable.ic_profile,
            "title" to "Titre 1",
            "author" to "Auteur 1",
            "nb" to 10,
            "date" to "01/11/2023",
            "id" to "1"
        ),
        mapOf(
            "icon" to R.drawable.ic_profile,
            "title" to "Titre 2",
            "author" to "Auteur 2",
            "nb" to 20,
            "date" to "02/11/2023",
            "id" to "2"
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterList) { filter ->
                FilterChip(filter)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {navController.navigate("ForumPage")},
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            LazyColumn {
                items(forumDataList) { data ->

                    ForumCard(
                        navController = navController,
                        data = data
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
