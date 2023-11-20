package com.example.saglife.screen.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.saglife.database.getUsernameFromUid
import com.example.saglife.models.ForumFilterItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.saglife.models.ForumPostItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date


@SuppressLint("MutableCollectionMutableState", "LogNotTimber")
@Composable
fun ForumScreen(navController: NavHostController) {

    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }

    // Etat du rafraîchissement
    var refreshing by remember { mutableStateOf(false) }

    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    var filterList by remember { mutableStateOf(mutableListOf<ForumFilterItem>()) }
    val db = Firebase.firestore
    val forumfilter = mutableListOf<ForumFilterItem>()

    // Effect pour charger les filtres de forum depuis Firebase
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

    // Récupération des posts de forum depuis Firebase
    db.collection("forum").orderBy("Date", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
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

    // Affiche la liste des posts dans un SwipeRefresh
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

        // Contenu de l'écran
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Affiche la liste des filtres sous forme de puce
                LazyRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterList) { filter ->
                        FilterChip(
                            onClick = { filterName ->
                                if (selectedFilters.contains(filterName)) {
                                    selectedFilters = selectedFilters.filter { it != filterName }
                                } else {
                                    selectedFilters = selectedFilters + filterName
                                }
                                ForumPostList = if (selectedFilters.isEmpty()) {
                                    forumpost
                                } else {
                                    forumpost.filter { post ->
                                        // Vérifier si un des filtres sélectionnés est dans la liste de filtres du post
                                        selectedFilters.any { filterName ->
                                            post.filter.contains(filterName)
                                        }
                                    }.toMutableList()
                                }
                            },
                            filter
                        )
                    }
                }

                // Affiche la liste des posts
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

            // Bouton flottant pour ajouter un nouveau post
            FloatingActionButton(
                modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
                onClick = {
                    navController.navigate("forum/createpost")
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un post"
                )
            }
        }
    }
}


