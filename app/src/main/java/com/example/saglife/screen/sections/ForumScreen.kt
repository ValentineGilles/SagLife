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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.component.forum.ForumCard
import com.example.saglife.component.forum.FilterChip
import com.example.saglife.models.ForumFilterItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.saglife.models.ForumPostItem
import java.util.Date

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
            println("Erreur lors de la récupération des données des filtres de post : $e")
        }

    val forumpost = mutableListOf<ForumPostItem>()
    var ForumPostList by remember { mutableStateOf(mutableListOf<ForumPostItem>()) }

    db.collection("forum").get().addOnSuccessListener { result ->
        for (document in result) {
            val date : Date = document.getDate("Date")!!
            val author = document.get("Author").toString()
            val icon= document.get("Icon").toString()
            val title= document.get("Title").toString()
            val nb= document.get("Nb").toString().toIntOrNull() ?: 0
            forumpost.add(ForumPostItem(document.id,author, date, icon, title, nb))
        }
        ForumPostList = forumpost
    }
        .addOnFailureListener { e ->
            println("Erreur lors de la récupération des données des posts: $e")
        }

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
                items(ForumPostList) { data ->

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
