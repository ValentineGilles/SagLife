package com.example.saglife.screen.forum

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumFilterItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.saglife.models.ForumPostItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.util.Date

@SuppressLint("MutableCollectionMutableState", "LogNotTimber")
@Composable
fun ForumScreen(navController : NavHostController) {

    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }

    // Etat du rafraîchissement
    var refreshing by remember { mutableStateOf(false) }

    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val db = Firebase.firestore
    val forumfilter = mutableListOf<ForumFilterItem>()

    LaunchedEffect(postLoaded) {
        if (!postLoaded) {

            db.collection("filter_forum").get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.get("Name").toString()
                    forumfilter.add(ForumFilterItem(name))
                }
                filterList = forumfilter
                postLoaded = true
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données des filtres de post : $e")
                }
        }
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
            val description = document.get("Description").toString()
            val filter = document.get("Filter").toString()

            forumpost.add(ForumPostItem(document.id,author, date, icon, title, nb, filter, description))
        }
        ForumPostList = forumpost

    }
        .addOnFailureListener { e ->
            println("Erreur lors de la récupération des données des posts: $e")
        }



    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            refreshing = true
        },
        modifier = Modifier.fillMaxSize()

    ) {
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(1000) // Simule une attente de 2 secondes
                refreshing = false
                postLoaded = false
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {

            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(filterList) { filter ->
                    FilterChip(
                        onClick = { filterName ->
                            if (selectedFilters.contains(filterName)) {
                                selectedFilters = selectedFilters.filter { it != filterName }
                                println("Test selectedFilters : $selectedFilters")
                            } else {
                                selectedFilters = selectedFilters + filterName
                                println("Test selectedFilters : $selectedFilters")
                            }
                            println("Test forumpost : $forumpost")
                            ForumPostList = if (selectedFilters.isEmpty()) {
                                println("Test Empty")
                                forumpost
                            } else {
                                println("Test not Empty)")
                                forumpost.filter { post ->
                                    // Vérifier si un des filtres sélectionnés est dans la liste de filtres du post
                                    selectedFilters.any { filterName ->
                                        post.filter.contains(filterName)
                                    }
                                }.toMutableList()
                            }
                            println("Test forumpostlist : $ForumPostList")

                        },
                        filter
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
}
