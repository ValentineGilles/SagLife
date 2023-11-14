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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saglife.models.ForumFilterItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(onClick: (String) -> Unit, filtername: ForumFilterItem) {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        onClick = { selected = !selected
                    onClick(filtername.name)},
        label = {
            Text(filtername.name)
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
    )
}


@Composable
fun FilterCreatePostChip(
    onClick: (String) -> Unit,
    selectedFilters: String,
    filtername: ForumFilterItem
) {
    val isSelected = selectedFilters == filtername.name

    Card(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
            .clickable {
                if (isSelected) {
                    onClick("")
                } else {
                    onClick(filtername.name)
                }
            },
                colors = if (isSelected) CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
    ) else CardDefaults.cardColors(
        containerColor = Color.Transparent
    )) {
        Text(
            text = filtername.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

