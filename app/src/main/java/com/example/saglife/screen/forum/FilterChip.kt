package com.example.saglife.screen.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.saglife.models.ForumFilterItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(onClick: (String) -> Unit, filtername: ForumFilterItem) {
    // Déclaration d'une variable mutable pour suivre l'état de la sélection du chip
    var selected by remember { mutableStateOf(false) }

    // Composable FilterChip qui affiche le chip filtrable
    FilterChip(
        onClick = {
            // Inverser l'état de la sélection lorsque le chip est cliqué
            selected = !selected
            // Appeler la fonction de rappel onClick avec le nom du filtre
            onClick(filtername.name)
        },
        label = {
            // Afficher le nom du filtre
            Text(filtername.name)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                // Afficher une icône "Done" lorsque le chip est sélectionné
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        // Définir la couleur de fond du chip en fonction de l'état de sélection

        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
            selectedContainerColor = MaterialTheme.colorScheme.tertiary,
        )
    )
}

@Composable
fun FilterCreatePostChip(
    onClick: (String) -> Unit,
    selectedFilters: String,
    filtername: ForumFilterItem,
    defaultFilter: String
) {
    // Vérifier si ce chip est sélectionné en fonction du nom du filtre
    println("defaultFilter : $defaultFilter")
    val isSelected = selectedFilters == filtername.name || filtername.name == defaultFilter
    println("isSelected : $isSelected")

    // Composable Card qui affiche le chip pour la création de messages
    Card(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
            .clickable {
                // Gérer le clic en fonction de l'état de sélection
                if (isSelected) {
                    // Désélectionner le filtre en appelant la fonction de rappel avec une chaîne vide
                    onClick("")
                } else {
                    // Sélectionner le filtre en appelant la fonction de rappel avec le nom du filtre
                    onClick(filtername.name)
                }
            },
        // Définir la couleur de la Card en fonction de l'état de sélection
        colors = if (isSelected) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ) else CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        // Afficher le nom du filtre avec une mise en forme de style
        Text(
            text = filtername.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

