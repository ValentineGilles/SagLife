package com.example.saglife.screen.navbars

import android.os.Bundle
import android.text.style.BackgroundColorSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.saglife.ui.theme.SagLifeTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    // Récupération des couleurs du thème Material
    val colors = MaterialTheme.colorScheme

    // Composant BottomNavigation qui affiche les éléments de la barre de navigation inférieure
    BottomNavigation(backgroundColor = colors.surface) {

        // Première option de navigation : Accueil
        BottomNavigationItem(
            selected = selectedItem == 0, // Détermine si cette option est sélectionnée
            onClick = { onItemSelected(0) }, // Appelé lorsqu'on clique sur cette option
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }, // Icône de l'option
            label = { Text(text = "Accueil", style = MaterialTheme.typography.labelSmall) } // Texte de l'option
        )

        // Deuxième option de navigation : Événements
        BottomNavigationItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar") },
            label = { Text(text = "Evenements", style = MaterialTheme.typography.labelSmall) }
        )

        // Troisième option de navigation : Carte
        BottomNavigationItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Map") },
            label = { Text(text = "Carte", style = MaterialTheme.typography.labelSmall) }
        )

        // Quatrième option de navigation : Forum
        BottomNavigationItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = { Icon(imageVector = Icons.Default.MailOutline, contentDescription = "Forum") },
            label = { Text(text = "Forum", style = MaterialTheme.typography.labelSmall) }
        )
    }
}
