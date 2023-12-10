package com.example.saglife.screen.calendar

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.unit.dp
import com.example.saglife.models.ForumFilterItem

/**
 * Composant de puce filtrante pour la sélection de filtres dans l'interface utilisateur.
 *
 * @param onClick La fonction de rappel appelée lorsqu'un filtre est cliqué.
 * @param filtername Le nom du filtre à afficher dans la puce.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(onClick: (String) -> Unit, filtername: String) {
    // État pour suivre si la puce est sélectionnée ou non
    var selected by remember { mutableStateOf(false) }

    // Composant FilterChip de Material3 pour représenter un filtre
    FilterChip(
        onClick = {
            // Inversion de l'état de sélection lors du clic sur la puce
            selected = !selected
                    onClick(filtername)},
        label = {
            Text(filtername)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
            selectedContainerColor = MaterialTheme.colorScheme.tertiary,
        )
    )
}
