package com.example.saglife.screen.sections

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.ForumCard
import com.example.saglife.R
import com.example.saglife.component.FilterChip

@Composable
fun ForumScreen(navController : NavHostController) {
    val filterList = listOf("Tout", "Immigration", "Assurance", "Autre filtre", "Encore un filtre", "Dernier filtre")
    val forumDataList = listOf(
        mapOf(
            "icon" to R.drawable.ic_profile,
            "title" to "Titre 1",
            "author" to "Auteur 1",
            "nb" to 10,
            "date" to "01/11/2023"
        ),
        mapOf(
            "icon" to R.drawable.ic_profile,
            "title" to "Titre 2",
            "author" to "Auteur 2",
            "nb" to 20,
            "date" to "02/11/2023"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(forumDataList) { data ->
                    ForumCard(
                        data["icon"] as Int,
                        data["title"] as String,
                        data["author"] as String,
                        data["nb"] as Int,
                        data["date"] as String
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
