package com.example.saglife.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController) {
    // Utilisation du composant Scaffold pour créer une disposition de base
    Scaffold(
        content = {
            // Colonne pour organiser les éléments de l'écran
            Column(
                modifier = Modifier.fillMaxSize(), // Remplit tout l'espace disponible
                verticalArrangement = Arrangement.Center, // Centre verticalement les éléments
                horizontalAlignment = Alignment.CenterHorizontally // Centre horizontalement les éléments
            ) {
                // Texte temporaire. A remplacer par les parametres et infos de l'utilisateur
                Text(
                    text = "Profile",
                    fontSize = 20.sp
                )
            }
        }
    )
}
