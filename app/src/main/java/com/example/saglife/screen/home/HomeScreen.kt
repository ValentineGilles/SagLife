package com.example.saglife.screen.home
import androidx.compose.foundation.layout.Column
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumPostItem
import com.example.saglife.screen.forum.ForumCard
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun HomeScreen(navController : NavHostController) {

    val db = Firebase.firestore
    val forumpost = mutableListOf<ForumPostItem>()
    var ForumPostList by remember { mutableStateOf(mutableListOf<ForumPostItem>()) }
    var postLoaded by remember { mutableStateOf(false) }

    // Etat du rafraîchissement
    var refreshing by remember { mutableStateOf(false) }

    db.collection("forum").orderBy("Date", Query.Direction.DESCENDING).limit(3).get().addOnSuccessListener { result ->
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            LazyColumn {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start=8.dp, bottom = 16.dp),
                        text = "Derniers Posts",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                    )
                }
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

